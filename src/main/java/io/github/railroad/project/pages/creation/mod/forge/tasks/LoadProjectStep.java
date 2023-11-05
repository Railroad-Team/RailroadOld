package io.github.railroad.project.pages.creation.mod.forge.tasks;

import io.github.railroad.project.Project;
import io.github.railroad.project.task.Task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class LoadProjectStep extends Task.Step {
    public LoadProjectStep(Project project, Supplier<Path> directorySupplier) {
        super("Load Project", 10, () -> {
            try {
                Path directory = directorySupplier.get();
                if(directory == null)
                    throw new IllegalStateException("Path is provided is invalid!");

                if(Files.notExists(directory))
                    Files.createDirectories(directory);

                project.setProjectFolder(directory);
            } catch (final Exception exception) {
                throw new IllegalStateException("Unable to load project!", exception);
            }
        }, () -> {
            throw new UnsupportedOperationException("You cannot go back to this step!");
        });
    }
}
