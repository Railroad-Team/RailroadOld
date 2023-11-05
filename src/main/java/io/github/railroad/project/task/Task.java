package io.github.railroad.project.task;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;

import java.util.LinkedList;
import java.util.Queue;

public class Task {
    private final Queue<Step> steps = new LinkedList<>();
    private final String name;
    private final SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
    private final SimpleObjectProperty<Step> step = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<TaskStatus> status = new SimpleObjectProperty<>(TaskStatus.PENDING);

    public Task(String name) {
        this.name = name;
    }

    public void run() {
        int totalCost = steps.stream().mapToInt(Step::getCost).sum();
        this.status.set(TaskStatus.RUNNING);
        while (!steps.isEmpty()) {
            Step step = steps.poll();
            this.step.set(step);

            step.run();
            this.progress.set(this.progress.get() + (step.getCost() / (double) totalCost));
        }

        this.status.set(TaskStatus.COMPLETED);
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    public ObservableDoubleValue progressProperty() {
        return progress;
    }

    public ObservableObjectValue<Step> stepProperty() {
        return step;
    }

    public ObservableObjectValue<TaskStatus> statusProperty() {
        return status;
    }

    public String getName() {
        return name;
    }

    public static class Step {
        private final String name;
        private final int cost;
        private final Runnable action;
        private final Runnable undoAction;

        public Step(String name, int cost, Runnable action, Runnable undoAction) {
            this.name = name;
            this.cost = Math.min(Math.max(cost, 0), 1000);
            this.action = action;
            this.undoAction = undoAction;
        }

        public String getName() {
            return name;
        }

        public int getCost() {
            return cost;
        }

        public void run() {
            action.run();
        }

        public void undo() {
            undoAction.run();
        }
    }

    public enum TaskStatus {
        PENDING("Pending"),
        RUNNING("Running"),
        COMPLETED("Completed"),
        FAILED("Failed"),
        PAUSED("Paused"),
        CANCELED("Canceled");

        private final String name;

        TaskStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
