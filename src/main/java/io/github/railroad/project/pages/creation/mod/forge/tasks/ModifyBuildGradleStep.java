package io.github.railroad.project.pages.creation.mod.forge.tasks;

import groovy.lang.GroovyShell;
import groovy.text.StreamingTemplateEngine;
import groovy.text.Template;
import io.github.railroad.project.task.Task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class ModifyBuildGradleStep extends Task.Step {
    private static final StreamingTemplateEngine TEMPLATE_ENGINE = new StreamingTemplateEngine();
    private static final GroovyShell SHELL = new GroovyShell();

    public ModifyBuildGradleStep(Supplier<Path> folderSupplier) {
        super("Modify build.gradle", 75, () -> {
            try {
                Path folder = folderSupplier.get();
                if (folder == null)
                    throw new IllegalStateException("Path is provided is invalid!");

                Path buildGradle = folder.resolve("build.gradle");
                if (Files.notExists(buildGradle))
                    throw new IllegalStateException("build.gradle is not found!");

                String buildGradleContent = Files.readString(buildGradle);
                Template template = TEMPLATE_ENGINE.createTemplate(buildGradleContent);
                String modifiedBuildGradleContent = template.make().toString();
            } catch (final Exception exception) {
                throw new IllegalStateException("Unable to modify build.gradle!", exception);
            }
        }, () -> {});
    }
}
