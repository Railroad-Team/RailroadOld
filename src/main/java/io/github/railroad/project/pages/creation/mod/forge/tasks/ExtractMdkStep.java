package io.github.railroad.project.pages.creation.mod.forge.tasks;

import io.github.railroad.project.task.Task;
import io.github.railroad.utility.ZipUtility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class ExtractMdkStep extends Task.Step {
    public ExtractMdkStep(Supplier<Path> directory) {
        super("Extracting MDK", 100, () -> {
            try {
                final Path path = directory.get();
                if(path == null)
                    throw new IllegalStateException("Path is provided is invalid!");

                if (Files.notExists(path))
                    Files.createDirectories(path);

                final Path mdk = path.resolve("mdk.zip");
                if (Files.notExists(mdk))
                    throw new IllegalStateException("MDK is not downloaded!");

                ZipUtility.unzip(mdk, path);
                Files.deleteIfExists(mdk);
            } catch (final IOException exception) {
                throw new IllegalStateException("Unable to extract MDK!", exception);
            }
        }, () -> {
            try {
                Path path = directory.get();
                if(path == null)
                    throw new IllegalStateException("Path is provided is invalid!");

                Path mdk = path.resolve("mdk.zip");
                Files.deleteIfExists(mdk);
                ZipUtility.zipDirectory(path, mdk);
            } catch (final IOException exception) {
                throw new IllegalStateException("Unable to delete MDK!", exception);
            }
        });
    }
}
