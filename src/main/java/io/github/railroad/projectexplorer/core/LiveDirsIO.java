package io.github.railroad.projectexplorer.core;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import io.github.railroad.projectexplorer.utility.CompletionStageWithDefaultExecutor;
import io.github.railroad.projectexplorer.utility.DirWatcher;
import io.github.railroad.projectexplorer.utility.InitiatorTrackingIOFacility;

public class LiveDirsIO implements InitiatorTrackingIOFacility {
    private final DirWatcher dirWatcher;
    private final LiveDirsModel model;
    private final Executor clientThreadExecutor;

    public LiveDirsIO(DirWatcher dirWatcher, LiveDirsModel model, Executor clientThreadExecutor) {
        this.dirWatcher = dirWatcher;
        this.model = model;
        this.clientThreadExecutor = clientThreadExecutor;
    }

    @Override
    public CompletionStage<Void> createDirectory(Path dir, Path initiator) {
        final CompletableFuture<Void> created = new CompletableFuture<>();
        this.dirWatcher.createDirectory(dir, () -> {
            if (this.model.containsPrefixOf(dir)) {
                this.model.addDirectory(dir, initiator);
                this.dirWatcher.watchOrLogError(dir);
            }
            created.complete(null);
        }, created::completeExceptionally);
        return wrap(created);
    }

    @Override
    public CompletionStage<Void> createFile(Path file, Path initiator) {
        final CompletableFuture<Void> created = new CompletableFuture<>();
        this.dirWatcher.createFile(file, lastModified -> {
            this.model.addFile(file, initiator, lastModified);
            created.complete(null);
        }, created::completeExceptionally);
        return wrap(created);
    }

    @Override
    public CompletionStage<Void> delete(Path file, Path initiator) {
        final CompletableFuture<Void> deleted = new CompletableFuture<>();
        this.dirWatcher.deleteFileOrEmptyDirectory(file, () -> {
            this.model.delete(file, initiator);
            deleted.complete(null);
        }, deleted::completeExceptionally);
        return wrap(deleted);
    }

    @Override
    public CompletionStage<Void> deleteTree(Path root, Path initiator) {
        final CompletableFuture<Void> deleted = new CompletableFuture<>();
        this.dirWatcher.deleteTree(root, () -> {
            this.model.delete(root, initiator);
            deleted.complete(null);
        }, deleted::completeExceptionally);
        return wrap(deleted);
    }

    @Override
    public CompletionStage<byte[]> loadBinaryFile(Path file) {
        final CompletableFuture<byte[]> loaded = new CompletableFuture<>();
        this.dirWatcher.loadBinaryFile(file, loaded::complete, loaded::completeExceptionally);
        return wrap(loaded);
    }

    @Override
    public CompletionStage<String> loadTextFile(Path file, Charset charset) {
        final CompletableFuture<String> loaded = new CompletableFuture<>();
        this.dirWatcher.loadTextFile(file, charset, loaded::complete, loaded::completeExceptionally);
        return wrap(loaded);
    }

    @Override
    public CompletionStage<Void> saveBinaryFile(Path file, byte[] content, Path initiator) {
        final CompletableFuture<Void> saved = new CompletableFuture<>();
        this.dirWatcher.saveBinaryFile(file, content, lastModified -> {
            this.model.updateModificationTime(file, lastModified, initiator);
            saved.complete(null);
        }, saved::completeExceptionally);
        return wrap(saved);
    }

    @Override
    public CompletionStage<Void> saveTextFile(Path file, String content, Charset charset, Path initiator) {
        final CompletableFuture<Void> saved = new CompletableFuture<>();
        this.dirWatcher.saveTextFile(file, content, charset, lastModified -> {
            this.model.updateModificationTime(file, lastModified, initiator);
            saved.complete(null);
        }, saved::completeExceptionally);
        return wrap(saved);
    }

    private <T> CompletionStage<T> wrap(CompletionStage<T> stage) {
        return new CompletionStageWithDefaultExecutor<>(stage, this.clientThreadExecutor);
    }
}