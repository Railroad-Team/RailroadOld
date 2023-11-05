package io.github.railroad.project.pages.creation.mod.forge.tasks;

import groovy.lang.Binding;
import io.github.railroad.project.task.Task;
import org.codehaus.groovy.runtime.StringBufferWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

public class ModifySettingsGradleStep extends Task.Step {
    public ModifySettingsGradleStep(Supplier<ModifyBuildGradleStep.CreateForgeModArgs> arguments, Supplier<Path> folderSupplier) {
        super("Modify settings.gradle", 75, () -> {
            try {
                Path folder = folderSupplier.get();
                if (folder == null)
                    throw new IllegalStateException("Path is provided is invalid!");

                Path settingsGradle = folder.resolve("settings.gradle");
                if (Files.notExists(settingsGradle))
                    throw new IllegalStateException("settings.gradle is not found!");

                Map<String, Object> args = ModifyBuildGradleStep.createArgs(arguments.get());

                String settingsGradleContent = Files.readString(settingsGradle);
                if (settingsGradleContent.startsWith("// fileName:")) {
                    int newLineIndex = settingsGradleContent.indexOf('\n');
                    Binding binding = new Binding(args);
                    binding.setVariable("defaultName",
                            folder.relativize(settingsGradle.toAbsolutePath()).toString());
                    Object result = ModifyBuildGradleStep.SHELL.parse(
                            settingsGradleContent.substring("// fileName:".length() + 1, newLineIndex),
                            binding).run();
                    if (result == null)
                        throw new IllegalStateException("Unable to parse settings.gradle!");

                    settingsGradleContent = settingsGradleContent.substring(newLineIndex + 1);
                }

                var buffer = new StringBuffer();
                ModifyBuildGradleStep.TEMPLATE_ENGINE.createTemplate(settingsGradleContent)
                        .make(args)
                        .writeTo(new StringBufferWriter(buffer));
                Files.writeString(settingsGradle, buffer);
            } catch (final Exception exception) {
                throw new IllegalStateException("Unable to modify settings.gradle!", exception);
            }
        }, () -> {
            // TODO: Make reversible
        });
    }
}
