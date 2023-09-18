package io.github.railroad.project.pages.creation.mod.forge.tasks;

import io.github.railroad.project.task.Task;
import org.apache.commons.io.FileUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;

public class DeleteUnusedFilesStep extends Task.Step {
    public DeleteUnusedFilesStep(Supplier<Path> directory) {
        super("Deleting unused files", 50, () -> {
            try {
                final Path path = directory.get();
                if(path == null)
                    throw new IllegalStateException("Path is provided is invalid!");

                Files.deleteIfExists(path.resolve("changelog.txt"));
                Files.deleteIfExists(path.resolve("CREDITS.txt"));
                Files.deleteIfExists(path.resolve("LICENSE.txt"));
                Files.deleteIfExists(path.resolve("README.txt"));
                Files.deleteIfExists(path.resolve("build.gradle"));
                FileUtils.deleteDirectory(path.resolve("src/main/java/com").toFile());

                String gradleProperties = Files.readString(path.resolve("gradle.properties"));
                gradleProperties = gradleProperties.replace(
                        "org.gradle.jvmargs=-Xmx3G",
                        "org.gradle.jvmargs=-Xmx4G");
                Files.writeString(path.resolve("gradle.properties"), gradleProperties);
            } catch (final Exception exception) {
                throw new IllegalStateException("Unable to delete unused files!", exception);
            }
        }, () -> {});
    }
}
