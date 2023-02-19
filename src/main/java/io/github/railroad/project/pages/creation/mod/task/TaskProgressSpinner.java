package io.github.railroad.project.pages.creation.mod.task;

import com.sun.javafx.collections.ObservableMapWrapper;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableMap;
import javafx.scene.control.Button;

import java.util.LinkedHashMap;
import java.util.Map;

public class TaskProgressSpinner extends MFXProgressSpinner {
    private final ObservableMap<Task, DoubleProperty> tasks = new ObservableMapWrapper<>(new LinkedHashMap<>());
    private final SimpleObjectProperty<Button> startButton = new SimpleObjectProperty<>();

    public TaskProgressSpinner(Task... tasks) {
        for (Task task : tasks) {
            this.tasks.put(task, new SimpleDoubleProperty(0.0));
        }
    }

    public TaskProgressSpinner(double progress, Task... tasks) {
        super(progress);
        for (Task task : tasks) {
            this.tasks.put(task, new SimpleDoubleProperty(0.0));
        }
    }

    public Button getStartButton() {
        return startButton.get();
    }

    public SimpleObjectProperty<Button> startButtonProperty() {
        return startButton;
    }

    public void setStartButton(Button startButton) {
        this.startButton.set(startButton);

        if (startButton != null) {
            visibleProperty().bind(startButton.disableProperty());
            managedProperty().bind(startButton.disableProperty());

            startButton.setOnAction(event -> {
                start(0);
                startButton.setDisable(true);
            });
        }
    }

    private void start(int index) {
        if (tasks.isEmpty()) {
            return;
        }

        Map.Entry<Task, DoubleProperty> entry = tasks.entrySet().stream().skip(index).findFirst().orElse(null);
        if (entry == null) {
            return;
        }

        Task task = entry.getKey();
        task.setOnTaskComplete(() -> {
            if (index < tasks.size() - 1) {
                start(index + 1);
            }
        });

        DoubleProperty progress = entry.getValue();
        task.run(progress);
    }

    public void revert(int index) {
        if (tasks.isEmpty()) {
            return;
        }

        Map.Entry<Task, DoubleProperty> entry = tasks.entrySet().stream().skip(index).findFirst().orElse(null);
        if (entry == null) {
            return;
        }

        Task task = entry.getKey();
        task.setOnTaskComplete(() -> {
            if (index < tasks.size() - 1) {
                revert(index + 1);
            }
        });

        DoubleProperty progress = entry.getValue();
        task.revert(progress);
    }
}
