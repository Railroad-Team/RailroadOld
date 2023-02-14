package io.github.railroad.project.pages.creation.mod.task;

import org.jetbrains.annotations.NotNull;

public class BiDirectionalRunnable {
    public static final Runnable EMPTY_RUNNABLE = () -> {
    };

    private final Runnable revert, run;

    public BiDirectionalRunnable(@NotNull Runnable run, @NotNull Runnable revert) {
        if (run == null) throw new NullPointerException("run");
        this.run = run;

        if (revert == null) throw new NullPointerException("revert");
        this.revert = revert;
    }

    public void run() {
        this.run.run();
    }

    public void revert() {
        this.revert.run();
    }
}
