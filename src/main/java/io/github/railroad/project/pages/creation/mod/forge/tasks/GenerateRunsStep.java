package io.github.railroad.project.pages.creation.mod.forge.tasks;

import io.github.railroad.project.task.Task;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class GenerateRunsStep extends Task.Step {
    public GenerateRunsStep(Supplier<Path> directorySupplier) {
        super("Generate runs", 100, () -> {
            try {
                Path directory = directorySupplier.get();
                if (Files.notExists(directory)) throw new IllegalStateException("Directory does not exist!");

                Path gradlew = directory.resolve("gradlew");
                if (Files.notExists(gradlew)) throw new IllegalStateException("Gradlew does not exist!");

                GradleConnector connector = GradleConnector.newConnector()
                        .forProjectDirectory(directory.toFile())
                        .useBuildDistribution();

                ProjectConnection connection = connector.connect();
                BuildLauncher launcher = connection.newBuild()
                        .forTasks("genIntellijRuns")
                        .setStandardOutput(System.out)
                        .setStandardError(System.err);

                launcher.run();
                connection.close();
            } catch (Exception exception) {
                throw new IllegalStateException("Unable to generate runs!", exception);
            }
        }, () -> {
            throw new UnsupportedOperationException("Unable to revert this step!");
        });
    }
}
