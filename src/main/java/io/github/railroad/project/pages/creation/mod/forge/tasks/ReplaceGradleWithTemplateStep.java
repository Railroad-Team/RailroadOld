package io.github.railroad.project.pages.creation.mod.forge.tasks;

import io.github.railroad.Railroad;
import io.github.railroad.project.task.Task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class ReplaceGradleWithTemplateStep extends Task.Step {
    public ReplaceGradleWithTemplateStep(Supplier<Path> directorySupplier, Supplier<String> versionSupplier) {
        super("Replacing Gradle with template", 10, () -> {
            try {
                Path path = directorySupplier.get();
                if(path == null)
                    throw new IllegalStateException("Path is provided is invalid!");

                Path buildGradle = path.resolve("build.gradle");
                Path settingsGradle = path.resolve("settings.gradle");

                Files.deleteIfExists(buildGradle);
                Files.deleteIfExists(settingsGradle);

                String version = versionSupplier.get();
                if(version == null)
                    throw new IllegalStateException("Version is provided is invalid!");

                version = version.split("\\.")[1];

                String buildGradleLocation = "/templates/forge/%s/template_build.gradle".formatted(version);
                String settingsGradleLocation = "/templates/forge/%s/template_settings.gradle".formatted(version);

                Files.copy(Railroad.class.getResourceAsStream(buildGradleLocation), buildGradle);
                Files.copy(Railroad.class.getResourceAsStream(settingsGradleLocation), settingsGradle);
            } catch (final Exception exception) {
                throw new IllegalStateException("Unable to replace Gradle with template!", exception);
            }
        }, () -> {});
    }
}
