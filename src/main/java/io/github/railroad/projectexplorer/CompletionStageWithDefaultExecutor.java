package io.github.railroad.projectexplorer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Completion stage that uses the provided executor as both the default
 * execution facility and the default asynchronous execution facility.
 *
 * A {@code CompletionStage} returned from any of this completion stage's
 * methods inherits this stage's execution facilities.
 *
 * This means, for example, that
 *
 * <pre>
 * this.thenApply(f).thenAcceptAsync(g).thenRun(h)
 * </pre>
 *
 * is equivalent to
 *
 * <pre>
 * this.thenApplyAsync(f, executor).thenAcceptAsync(g, executor).thenRunAsync(h, executor)
 * </pre>
 *
 * where {@code executor} is the executor this stage was created with.
 */
class CompletionStageWithDefaultExecutor<T> implements CompletionStage<T> {
    private final CompletionStage<T> original;
    private final Executor defaultExecutor;

    public CompletionStageWithDefaultExecutor(CompletionStage<T> original, Executor defaultExecutor) {
        this.original = original;
        this.defaultExecutor = defaultExecutor;
    }

    @Override
    public CompletionStage<Void> acceptEither(CompletionStage<? extends T> other,
            Consumer<? super T> action) {
        return wrap(this.original.acceptEitherAsync(other, action, this.defaultExecutor));
    }

    @Override
    public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other,
            Consumer<? super T> action) {
        return wrap(this.original.acceptEitherAsync(other, action, this.defaultExecutor));
    }

    @Override
    public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other,
            Consumer<? super T> action, Executor executor) {
        return wrap(this.original.acceptEitherAsync(other, action, executor));
    }

    @Override
    public <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> other,
            Function<? super T, U> fn) {
        return wrap(this.original.applyToEitherAsync(other, fn, this.defaultExecutor));
    }

    @Override
    public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other,
            Function<? super T, U> fn) {
        return wrap(this.original.applyToEitherAsync(other, fn, this.defaultExecutor));
    }

    @Override
    public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other,
            Function<? super T, U> fn, Executor executor) {
        return wrap(this.original.applyToEitherAsync(other, fn, executor));
    }

    @Override
    public CompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn) {
        return wrap(this.original.exceptionally(fn));
    }

    @Override
    public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        return wrap(this.original.handleAsync(fn, this.defaultExecutor));
    }

    @Override
    public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return wrap(this.original.handleAsync(fn, this.defaultExecutor));
    }

    @Override
    public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn,
            Executor executor) {
        return wrap(this.original.handleAsync(fn, executor));
    }

    @Override
    public CompletionStage<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
        return wrap(this.original.runAfterBothAsync(other, action, this.defaultExecutor));
    }

    @Override
    public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        return wrap(this.original.runAfterBothAsync(other, action, this.defaultExecutor));
    }

    @Override
    public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action,
            Executor executor) {
        return wrap(this.original.runAfterBothAsync(other, action, executor));
    }

    @Override
    public CompletionStage<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
        return wrap(this.original.runAfterEitherAsync(other, action, this.defaultExecutor));
    }

    @Override
    public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        return wrap(this.original.runAfterEitherAsync(other, action, this.defaultExecutor));
    }

    @Override
    public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action,
            Executor executor) {
        return wrap(this.original.runAfterEitherAsync(other, action, executor));
    }

    @Override
    public CompletionStage<Void> thenAccept(Consumer<? super T> action) {
        return wrap(this.original.thenAcceptAsync(action, this.defaultExecutor));
    }

    @Override
    public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action) {
        return wrap(this.original.thenAcceptAsync(action, this.defaultExecutor));
    }

    @Override
    public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
        return wrap(this.original.thenAcceptAsync(action, executor));
    }

    @Override
    public <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action) {
        return wrap(this.original.thenAcceptBothAsync(other, action, this.defaultExecutor));
    }

    @Override
    public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action) {
        return wrap(this.original.thenAcceptBothAsync(other, action, this.defaultExecutor));
    }

    @Override
    public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action, Executor executor) {
        return wrap(this.original.thenAcceptBothAsync(other, action, executor));
    }

    @Override
    public <U> CompletionStage<U> thenApply(Function<? super T, ? extends U> fn) {
        return wrap(this.original.thenApplyAsync(fn, this.defaultExecutor));
    }

    @Override
    public <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        return wrap(this.original.thenApplyAsync(fn, this.defaultExecutor));
    }

    @Override
    public <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
        return wrap(this.original.thenApplyAsync(fn, executor));
    }

    @Override
    public <U, V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        return wrap(this.original.thenCombineAsync(other, fn, this.defaultExecutor));
    }

    @Override
    public <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        return wrap(this.original.thenCombineAsync(other, fn, this.defaultExecutor));
    }

    @Override
    public <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        return wrap(this.original.thenCombineAsync(other, fn, executor));
    }

    @Override
    public <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
        return wrap(this.original.thenComposeAsync(fn, this.defaultExecutor));
    }

    @Override
    public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
        return wrap(this.original.thenComposeAsync(fn, this.defaultExecutor));
    }

    @Override
    public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn,
            Executor executor) {
        return wrap(this.original.thenComposeAsync(fn, executor));
    }

    @Override
    public CompletionStage<Void> thenRun(Runnable action) {
        return wrap(this.original.thenRunAsync(action, this.defaultExecutor));
    }

    @Override
    public CompletionStage<Void> thenRunAsync(Runnable action) {
        return wrap(this.original.thenRunAsync(action, this.defaultExecutor));
    }

    @Override
    public CompletionStage<Void> thenRunAsync(Runnable action, Executor executor) {
        return wrap(this.original.thenRunAsync(action, executor));
    }

    @Override
    public CompletableFuture<T> toCompletableFuture() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        return wrap(this.original.whenCompleteAsync(action, this.defaultExecutor));
    }

    @Override
    public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        return wrap(this.original.whenCompleteAsync(action, this.defaultExecutor));
    }

    @Override
    public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action,
            Executor executor) {
        return wrap(this.original.whenCompleteAsync(action, executor));
    }

    private <U> CompletionStage<U> wrap(CompletionStage<U> original) {
        return new CompletionStageWithDefaultExecutor<>(original, this.defaultExecutor);
    }
}