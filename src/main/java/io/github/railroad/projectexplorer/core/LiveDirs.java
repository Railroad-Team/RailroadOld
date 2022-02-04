package io.github.railroad.projectexplorer.core;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.UnaryOperator;

import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import io.github.railroad.projectexplorer.model.DirectoryModel;
import io.github.railroad.projectexplorer.utility.CompletionStageWithDefaultExecutor;
import io.github.railroad.projectexplorer.utility.DirWatcher;
import io.github.railroad.projectexplorer.utility.InitiatorTrackingIOFacility;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * LiveDirs combines a directory watcher, a directory-tree model and a simple
 * I/O facility. The added value of this combination is:
 * <ol>
 * <li>the directory-tree model is updated automatically to reflect the current
 * state of the file-system;</li>
 * <li>the application can distinguish file-system changes made via the I/O
 * facility from external changes.</li>
 * </ol>
 * <p>
 * The directory model can be used directly as a model for {@link TreeView}.
 *
 * @param <I> type of the initiator of I/O actions.
 */
public class LiveDirs {
    private final EventSource<Throwable> localErrors = new EventSource<>();
    private final EventStream<Throwable> errors;
    private final Executor clientThreadExecutor;
    private final DirWatcher dirWatcher;
    private final LiveDirsModel model;
    private final LiveDirsIO io;
    private final Path externalInitiator;

    /**
     * Creates a LiveDirs instance to be used from a designated thread.
     *
     * @param  projector            converts the ({@link T})
     *                              {@link TreeItem#getValue()} into a {@link Path}
     *                              object
     * @param  injector             converts a given {@link Path} object into
     *                              {@link T}. The reverse of {@code projector}
     * @param  externalInitiator    object to represent an initiator of an external
     *                              file-system change.
     * @param  clientThreadExecutor executor to execute actions on the caller
     *                              thread. Used to publish updates and errors on
     *                              the caller thread.
     * @throws IOException
     */
    public LiveDirs(Path externalInitiator, UnaryOperator<Path> projector, UnaryOperator<Path> injector,
        Executor clientThreadExecutor) throws IOException {
        this.externalInitiator = externalInitiator;
        this.clientThreadExecutor = clientThreadExecutor;
        this.dirWatcher = new DirWatcher(clientThreadExecutor);
        this.model = new LiveDirsModel(externalInitiator, projector, injector);
        this.io = new LiveDirsIO(this.dirWatcher, this.model, clientThreadExecutor);

        this.dirWatcher.signalledKeys().subscribe(this::processKey);
        this.errors = EventStreams.merge(this.dirWatcher.errors(), this.model.errors(), this.localErrors);
    }

    /**
     * Adds a directory to watch. The directory will be added to the directory model
     * and watched for changes.
     */
    public void addTopLevelDirectory(Path dir) {
        if (!dir.isAbsolute())
            throw new IllegalArgumentException(
                dir + " is not absolute. Only absolute paths may be added as top-level directories.");

        try {
            this.dirWatcher.watch(dir);
            this.model.addTopLevelDirectory(dir);
            refresh(dir);
        } catch (final IOException e) {
            this.localErrors.push(e);
        }
    }

    /**
     * Releases resources used by this LiveDirs instance. In particular, stops the
     * I/O thread (used for I/O operations as well as directory watching).
     */
    public void dispose() {
        this.dirWatcher.shutdown();
    }

    /**
     * Stream of asynchronously encountered errors.
     */
    public EventStream<Throwable> errors() {
        return this.errors;
    }

    /**
     * Asynchronous I/O facility. All I/O operations performed by this facility are
     * performed on a single thread. It is the same thread that is used to watch the
     * file-system for changes.
     */
    public InitiatorTrackingIOFacility io() {
        return this.io;
    }

    /**
     * Observable directory model.
     */
    public DirectoryModel model() {
        return this.model;
    }

    /**
     * Used to refresh the given subtree of the directory model in case automatic
     * synchronization failed for any reason.
     * <p>
     * Guarantees given by {@link WatchService} are weak and the behavior may vary
     * on different operating systems. It is possible that the automatic
     * synchronization is not 100% reliable. This method provides a way to request
     * synchronization in case any inconsistencies are observed.
     */
    public CompletionStage<Void> refresh(Path path) {
        return wrap(this.dirWatcher.getTree(path)).thenAcceptAsync(tree -> {
            this.model.sync(tree);
            watchTree(tree);
        }, this.clientThreadExecutor);
    }

    private void handleCreation(Path path, Path initiator) {
        if (Files.isDirectory(path)) {
            handleDirCreation(path, initiator);
        } else {
            handleFileCreation(path, initiator);
        }
    }

    private void handleDirCreation(Path path, Path initiator) {
        if (this.model.containsPrefixOf(path)) {
            this.model.addDirectory(path, initiator);
            this.dirWatcher.watchOrLogError(path);
        }
        refreshOrLogError(path);
    }

    private void handleFileCreation(Path path, Path initiator) {
        try {
            final FileTime timestamp = Files.getLastModifiedTime(path);
            this.model.addFile(path, initiator, timestamp);
        } catch (final IOException e) {
            this.localErrors.push(e);
        }
    }

    private void handleModification(Path path, Path initiator) {
        try {
            final FileTime timestamp = Files.getLastModifiedTime(path);
            this.model.updateModificationTime(path, timestamp, initiator);
        } catch (final IOException e) {
            this.localErrors.push(e);
        }
    }

    private void processEvent(Path dir, WatchEvent<Path> event) {
        // Context for directory entry event is the file name of entry
        final Path relChild = event.context();
        final Path child = dir.resolve(relChild);

        final Kind<Path> kind = event.kind();

        if (kind == ENTRY_MODIFY) {
            handleModification(child, this.externalInitiator);
        } else if (kind == ENTRY_CREATE) {
            handleCreation(child, this.externalInitiator);
        } else if (kind == ENTRY_DELETE) {
            this.model.delete(child, this.externalInitiator);
        } else
            throw new AssertionError("unreachable code");
    }

    private void processKey(WatchKey key) {
        final Path dir = (Path) key.watchable();
        if (!this.model.containsPrefixOf(dir)) {
            key.cancel();
        } else {
            final List<WatchEvent<?>> events = key.pollEvents();
            if (events.stream().anyMatch(evt -> evt.kind() == OVERFLOW)) {
                refreshOrLogError(dir);
            } else {
                for (final WatchEvent<?> evt : key.pollEvents()) {
                    @SuppressWarnings("unchecked")
                    final WatchEvent<Path> event = (WatchEvent<Path>) evt;
                    processEvent(dir, event);
                }
            }

            if (!key.reset()) {
                this.model.delete(dir, this.externalInitiator);
            }
        }
    }

    private void refreshOrLogError(Path path) {
        refresh(path).whenComplete((nothing, ex) -> {
            if (ex != null) {
                this.localErrors.push(ex);
            }
        });
    }

    private void watchTree(PathNode tree) {
        if (tree.isDirectory()) {
            this.dirWatcher.watchOrLogError(tree.getPath());
            for (final PathNode child : tree.getChildren()) {
                watchTree(child);
            }
        }
    }

    private <U> CompletionStage<U> wrap(CompletionStage<U> stage) {
        return new CompletionStageWithDefaultExecutor<>(stage, this.clientThreadExecutor);
    }

    /**
     * Creates a LiveDirs instance to be used from the JavaFX application thread.
     *
     * @param  externalInitiator object to represent an initiator of an external
     *                           file-system change.
     * @throws IOException
     */
    public static LiveDirs getInstance(Path externalInitiator) throws IOException {
        return getInstance(externalInitiator, Platform::runLater);
    }

    /**
     * Creates a LiveDirs instance to be used from a designated thread.
     *
     * @param  externalInitiator    object to represent an initiator of an external
     *                              file-system change.
     * @param  clientThreadExecutor executor to execute actions on the caller
     *                              thread. Used to publish updates and errors on
     *                              the caller thread.
     * @throws IOException
     */
    public static LiveDirs getInstance(Path externalInitiator, Executor clientThreadExecutor) throws IOException {
        return new LiveDirs(externalInitiator, UnaryOperator.identity(), UnaryOperator.identity(),
            clientThreadExecutor);
    }
}