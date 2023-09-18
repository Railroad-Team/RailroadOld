package io.github.railroad.project.task.ui;

import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.railroad.project.task.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class TaskProgressBar extends BorderPane {
    private final Task task;
    private final VBox vBox = new VBox();
    private final Label taskName = new Label();
    private final Label taskStatus = new Label();
    private final Label taskProgress = new Label();
    private final Label stepName = new Label();
    private final MFXProgressBar progressBar = new MFXProgressBar();

    public TaskProgressBar(Task task) {
        this.task = task;
        this.progressBar.progressProperty().bind(task.progressProperty());
        this.taskName.textProperty().set(task.getName());
        this.taskName.setTextFill(Color.WHITE);

        task.statusProperty().addListener((observable, oldValue, newValue) ->
                this.taskStatus.textProperty().set(newValue.getName()));
        this.taskStatus.setTextFill(Color.WHITE);

        task.progressProperty().addListener((observable, oldValue, newValue) ->
                this.taskProgress.textProperty().set(String.format("%.2f%%", newValue.doubleValue() * 100)));
        this.taskProgress.setTextFill(Color.WHITE);

        task.stepProperty().addListener((observable, oldValue, newValue) ->
                this.stepName.textProperty().set(newValue.getName()));
        this.stepName.setTextFill(Color.WHITE);

        this.vBox.getChildren().addAll(taskName, taskStatus, taskProgress, stepName);
        this.vBox.setAlignment(Pos.CENTER);

        setTop(vBox);
        setCenter(progressBar);

        BorderPane.setAlignment(vBox, Pos.CENTER);
        BorderPane.setAlignment(progressBar, Pos.CENTER);
    }

    public Task getTask() {
        return task;
    }

    public MFXProgressBar getProgressBar() {
        return progressBar;
    }

    public VBox getVBox() {
        return vBox;
    }

    public Label getTaskName() {
        return taskName;
    }

    public Label getTaskStatus() {
        return taskStatus;
    }

    public Label getTaskProgress() {
        return taskProgress;
    }

    public Label getStepName() {
        return stepName;
    }

    public void update() {
        this.progressBar.setProgress(task.progressProperty().get());
    }

    public void update(double progress) {
        this.progressBar.setProgress(progress);
    }

    public void start() {
        this.task.run();
    }
}
