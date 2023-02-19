package io.github.railroad.project.pages.creation.mod.task;

import io.github.railroad.utility.ZipUtility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExtractMdkTask extends Task {
    private final Path mdkZipPath = Paths.get("../forge.zip");

    public ExtractMdkTask() {
        super("Extract MDK", "Extracting MDK...");
    }

    @Override
    public Collection<BiDirectionalRunnable> getProcesses() {
        List<BiDirectionalRunnable> processes = new ArrayList<>();
        processes.add(new BiDirectionalRunnable(this::extractMDK, this::deleteExtractedFiles));
        processes.add(new BiDirectionalRunnable(this::deleteMDK, BiDirectionalRunnable.EMPTY_RUNNABLE));
        processes.add(new BiDirectionalRunnable(this::deleteUnnecessaryFiles, BiDirectionalRunnable.EMPTY_RUNNABLE));
        return processes;
    }

    private void extractMDK() {
        try {
            ZipUtility.unzip(mdkZipPath, Paths.get("../forge/"));
        } catch(IllegalStateException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void deleteExtractedFiles() {
        try {
            Files.deleteIfExists(Paths.get("../forge/"));
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void deleteMDK() {
        try {
            Files.deleteIfExists(mdkZipPath);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void deleteUnnecessaryFiles() {
        try {
            Files.deleteIfExists(Paths.get("../forge/changelog.txt"));
            Files.deleteIfExists(Paths.get("../forge/CREDITS.txt"));
            Files.deleteIfExists(Paths.get("../forge/LICENSE.txt"));
            Files.deleteIfExists(Paths.get("../forge/README.txt"));
            Files.deleteIfExists(Paths.get("../forge/src/main/java/com/"));
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }
}
