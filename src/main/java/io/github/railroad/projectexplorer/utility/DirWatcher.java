package io.github.railroad.projectexplorer.utility;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.reactfx.EventSource;
import org.reactfx.EventStream;

import io.github.railroad.projectexplorer.core.PathNode;

public class DirWatcher {
    private final LinkedBlockingQueue<Runnable> executorQueue = new LinkedBlockingQueue<>();
    private final EventSource<WatchKey> signalledKeys = new EventSource<>();
    private final EventSource<Throwable> errors = new EventSource<>();
    private final WatchService watcher;
    private final Thread ioThread;
    private final Executor eventThreadExecutor;

    private volatile boolean shutdown = false;
    private boolean mayInterrupt = false;
    private boolean interrupted = false;

    public DirWatcher(Executor eventThreadExecutor) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.ioThread = new Thread(this::loop, "DirWatchIO");
        this.eventThreadExecutor = eventThreadExecutor;
        this.ioThread.start();
    }

    public void createDirectory(Path dir, Runnable onSuccess, Consumer<Throwable> onError) {
        executeIOOperation(() -> {
            Files.createDirectory(dir);
            return null;
        }, none -> onSuccess.run(), onError);
    }

    public void createFile(Path file, Consumer<FileTime> onSuccess, Consumer<Throwable> onError) {
        executeIOOperation(() -> createFile(file), onSuccess, onError);
    }

    public void deleteFileOrEmptyDirectory(Path fileOrDir, Runnable onSuccess, Consumer<Throwable> onError) {
        executeIOOperation(() -> {
            Files.deleteIfExists(fileOrDir);
            return null;
        }, NULL -> onSuccess.run(), onError);
    }

    public void deleteTree(Path root, Runnable onSuccess, Consumer<Throwable> onError) {
        executeIOOperation(() -> {
            if (Files.exists(root)) {
                deleteRecursively(root);
            }
            return null;
        }, NULL -> onSuccess.run(), onError);
    }

    public EventStream<Throwable> errors() {
        return this.errors;
    }

    public CompletionStage<PathNode> getTree(Path root) {
        final CompletableFuture<PathNode> res = new CompletableFuture<>();
        executeOnIOThread(() -> {
            try {
                res.complete(PathNode.getTree(root));
            } catch (final IOException e) {
                res.completeExceptionally(e);
                e.printStackTrace();
            }
        });
        return res;
    }

    public void loadBinaryFile(Path file, Consumer<byte[]> onSuccess, Consumer<Throwable> onError) {
        executeIOOperation(() -> Files.readAllBytes(file), onSuccess, onError);
    }

    public void loadTextFile(Path file, Charset charset, Consumer<String> onSuccess,
            Consumer<Throwable> onError) {
        executeIOOperation(() -> readTextFile(file, charset), onSuccess, onError);
    }

    public void saveBinaryFile(Path file, byte[] content, Consumer<FileTime> onSuccess,
            Consumer<Throwable> onError) {
        executeIOOperation(() -> writeBinaryFile(file, content), onSuccess, onError);
    }

    public void saveTextFile(Path file, String content, Charset charset, Consumer<FileTime> onSuccess,
            Consumer<Throwable> onError) {
        executeIOOperation(() -> writeTextFile(file, content, charset), onSuccess, onError);
    }

    public void shutdown() {
        this.shutdown = true;
        interrupt();
    }

    public EventStream<WatchKey> signalledKeys() {
        return this.signalledKeys;
    }

    public void watch(Path dir) throws IOException {
        dir.register(this.watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }

    public void watchOrLogError(Path dir) {
        try {
            watch(dir);
        } catch (final IOException e) {
            this.errors.push(e);
        }
    }

    private FileTime createFile(Path file) throws IOException {
        Files.createFile(file);
        return Files.getLastModifiedTime(file);
    }

    private void deleteRecursively(Path root) throws IOException {
        if (Files.isDirectory(root)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
                for (final Path path : stream) {
                    deleteRecursively(path);
                }
            }
        }
        Files.delete(root);
    }

    private void emitError(Throwable e) {
        executeOnEventThread(() -> this.errors.push(e));
    }

    private void emitKey(WatchKey key) {
        executeOnEventThread(() -> this.signalledKeys.push(key));
    }

    private <T> void executeIOOperation(Callable<T> action, Consumer<T> onSuccess,
            Consumer<Throwable> onError) {
        executeOnIOThread(() -> {
            try {
                final T res = action.call();
                executeOnEventThread(() -> onSuccess.accept(res));
            } catch (final Exception exception) {
                executeOnEventThread(() -> onError.accept(exception));
            }
        });
    }

    private void executeOnEventThread(Runnable action) {
        this.eventThreadExecutor.execute(action);
    }

    private void executeOnIOThread(Runnable action) {
        this.executorQueue.add(action);
        interrupt();
    }

    private synchronized void interrupt() {
        if (this.mayInterrupt) {
            this.ioThread.interrupt();
        } else {
            this.interrupted = true;
        }
    }

    private void loop() {
        for (;;) {
            final WatchKey key = takeOrNullIfInterrupted();
            if (key != null) {
                emitKey(key);
            } else if (this.shutdown) {
                try {
                    this.watcher.close();
                } catch (final IOException e) {
                    emitError(e);
                }
                break;
            } else {
                processIOQueues();
            }
        }
    }

    private void processIOQueues() {
        Runnable action;
        while ((action = this.executorQueue.poll()) != null) {
            try {
                action.run();
            } catch (final Exception exception) {
                this.errors.push(exception);
            }
        }
    }

    private String readTextFile(Path file, Charset charset) throws IOException {
        final byte[] bytes = Files.readAllBytes(file);
        final CharBuffer chars = charset.decode(ByteBuffer.wrap(bytes));
        return chars.toString();
    }

    private WatchKey take() throws InterruptedException {
        synchronized (this) {
            if (this.interrupted) {
                this.interrupted = false;
                throw new InterruptedException();
            }
            this.mayInterrupt = true;
        }

        try {
            return this.watcher.take();
        } finally {
            synchronized (this) {
                this.mayInterrupt = false;
            }
        }
    }

    private WatchKey takeOrNullIfInterrupted() {
        try {
            return take();
        } catch (final InterruptedException e) {
            return null;
        }
    }

    private FileTime writeBinaryFile(Path file, byte[] content) throws IOException {
        Files.write(file, content, CREATE, WRITE, TRUNCATE_EXISTING);
        return Files.getLastModifiedTime(file);
    }

    private FileTime writeTextFile(Path file, String content, Charset charset) throws IOException {
        final byte[] bytes = content.getBytes(charset);
        return writeBinaryFile(file, bytes);
    }
}