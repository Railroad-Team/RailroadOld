package io.github.railroad.project.task.ui;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.railroad.project.task.Task;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class TaskProgressSpinner extends VBox {
    private final Task task;
    private final MFXProgressSpinner progressSpinner = new MFXProgressSpinner();
    private final TaskProgressBar taskProgressBar;

    public TaskProgressSpinner(Task task) {
        super(10);
        this.task = task;
        this.taskProgressBar = new TaskProgressBar(task);
        this.taskProgressBar.getVBox().setAlignment(Pos.CENTER);
        this.progressSpinner.progressProperty().bind(task.progressProperty());

        getChildren().addAll(taskProgressBar, progressSpinner);
    }

    public Task getTask() {
        return task;
    }

    public MFXProgressSpinner getProgressSpinner() {
        return progressSpinner;
    }

    public TaskProgressBar getTaskProgressBar() {
        return taskProgressBar;
    }

    public VBox getVBox() {
        return taskProgressBar.getVBox();
    }

    public void update() {
        this.taskProgressBar.update();
        this.progressSpinner.setProgress(task.progressProperty().get());
    }

    public void update(double progress) {
        this.taskProgressBar.update();
        this.progressSpinner.setProgress(progress);
    }

    public void start() {
        this.taskProgressBar.start();
    }
}
