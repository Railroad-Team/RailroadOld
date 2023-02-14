package io.github.railroad.project.pages.creation.mod.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Task {
    private final List<Runnable> onTaskStarted = new ArrayList<>();
    private final List<Runnable> onTaskComplete = new ArrayList<>();
    private final List<Runnable> onTaskFailed = new ArrayList<>();

    private final List<BiDirectionalRunnable> processes = new ArrayList<>();

    private final String taskName;
    private String taskDescription;
    private TaskStatus status = TaskStatus.NOT_STARTED;

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public Task(String taskName) {
        this(taskName, "");
    }

    public void run() {
        setTaskStatus(TaskStatus.IN_PROGRESS);
        for (BiDirectionalRunnable process : this.processes) {
            process.run();
        }

        setTaskStatus(TaskStatus.COMPLETE);
    }

    public void revert() {
        setTaskStatus(TaskStatus.ERROR);
        for (BiDirectionalRunnable process : this.processes) {
            process.revert();
        }

        setTaskStatus(TaskStatus.NOT_STARTED);
    }

    public String getTaskName() {
        return this.taskName;
    }

    public String getTaskDescription() {
        return this.taskDescription;
    }

    public void setTaskDescription(String description) {
        this.taskDescription = description;
    }

    public TaskStatus getTaskStatus() {
        return this.status;
    }

    public void setTaskStatus(TaskStatus status) {
        this.status = status;

        switch (status) {
            case NOT_STARTED:
                break;
            case IN_PROGRESS:
                this.onTaskStarted.forEach(Runnable::run);
                break;
            case COMPLETE:
                this.onTaskComplete.forEach(Runnable::run);
                break;
            case ERROR:
                this.onTaskFailed.forEach(Runnable::run);
                break;
        }
    }

    public abstract Collection<BiDirectionalRunnable> getProcesses();

    public void addProcess(BiDirectionalRunnable process) {
        this.processes.add(process);
    }

    public void setOnTaskStarted(Runnable runnable) {
        this.onTaskStarted.add(runnable);
    }

    public void setOnTaskComplete(Runnable runnable) {
        this.onTaskComplete.add(runnable);
    }

    public void setOnTaskFailed(Runnable runnable) {
        this.onTaskFailed.add(runnable);
    }
}
