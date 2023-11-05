package io.github.railroad.project.pages.creation.mod.forge.tasks;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.text.StreamingTemplateEngine;
import io.github.railroad.project.task.Task;
import org.codehaus.groovy.runtime.StringBufferWriter;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModifyBuildGradleStep extends Task.Step {

    // TODO: Extract
    public static final StreamingTemplateEngine TEMPLATE_ENGINE = new StreamingTemplateEngine();

    // TODO: Extract
    public static final GroovyShell SHELL = new GroovyShell();

    public ModifyBuildGradleStep(Supplier<CreateForgeModArgs> arguments, Supplier<Path> folderSupplier) {
        super("Modify build.gradle", 75, () -> {
            try {
                Path folder = folderSupplier.get();
                if (folder == null)
                    throw new IllegalStateException("Path is provided is invalid!");

                Path buildGradle = folder.resolve("build.gradle");
                if (Files.notExists(buildGradle))
                    throw new IllegalStateException("build.gradle is not found!");

                Map<String, Object> args = createArgs(arguments.get());

                String buildGradleContent = Files.readString(buildGradle);
                if (buildGradleContent.startsWith("// fileName:")) {
                    int newLineIndex = buildGradleContent.indexOf('\n');
                    Binding binding = new Binding(args);
                    binding.setVariable("defaultName",
                            folder.relativize(buildGradle.toAbsolutePath()).toString());
                    Object result = SHELL.parse(
                            buildGradleContent.substring("// fileName:".length() + 1, newLineIndex),
                            binding).run();
                    if (result == null)
                        throw new IllegalStateException("Unable to parse build.gradle!");

                    buildGradleContent = buildGradleContent.substring(newLineIndex + 1);
                }

                var buffer = new StringBuffer();
                TEMPLATE_ENGINE.createTemplate(new StringReader(buildGradleContent))
                        .make(args)
                        .writeTo(new StringBufferWriter(buffer));
                Files.writeString(buildGradle, buffer);
            } catch (final Exception exception) {
                throw new IllegalStateException("Unable to modify build.gradle!", exception);
            }
        }, () -> {
            // TODO: Make reversible
        });
    }

    // TODO: Extract
    public static Map<String, Object> createArgs(CreateForgeModArgs arguments) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("modId", arguments.modId());
        map.put("modName", arguments.name());
        map.put("modDescription", arguments.description());
        map.put("author", arguments.author());
        map.put("license", arguments.license());
        map.put("displayTest", arguments.displayTest());

        map.put("packageName", arguments.packageName());
        map.put("mainClass", arguments.mainClass());

        map.put("versions", Map.of(
                "minecraft", arguments.mcVersion(),
                "forge", arguments.forgeVersion()));

        map.put("mappings", Map.of(
                "channel", arguments.mappingChannel(),
                "version", arguments.mappingVersion()));

        map.put("props", Map.of(
                "usesAccessTransformers", arguments.usesAccessTransformers(),
                "sharedRunDirs", arguments.sharedRunDirs(),
                "gradleKotlinDSL", arguments.gradleKotlinDSL(),
                "usesMixins", arguments.usesMixins(),
                "mixinGradle", arguments.mixinGradle(),
                "apiSourceSet", arguments.apiSourceSet(),
                "datagenSourceSet", arguments.datagenSourceSet()
        ));

        return map;
    }

    public record CreateForgeModArgs(String modId, String name, String description, String author, String license,
                                     String mcVersion, String forgeVersion, String mappingChannel,
                                     String mappingVersion,
                                     String packageName, String mainClass, boolean displayTest,
                                     boolean usesAccessTransformers,
                                     boolean sharedRunDirs, boolean gradleKotlinDSL, boolean usesMixins,
                                     String mixinGradle,
                                     String apiSourceSet, String datagenSourceSet) {
    }
}
