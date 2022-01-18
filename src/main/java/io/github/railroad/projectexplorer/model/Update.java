package io.github.railroad.projectexplorer.model;

import java.nio.file.Path;

/**
 * Represents an update to the directory model.
 */
public class Update {
    private final Path baseDir;
    private final Path relativePath;
    private final Path initiator;

    private final UpdateType type;

    private Update(Path baseDir, Path relPath, Path initiator, UpdateType type) {
        this.baseDir = baseDir;
        this.relativePath = relPath;
        this.initiator = initiator;
        this.type = type;
    }

    public Path getBaseDir() {
        return this.baseDir;
    }

    public Path getInitiator() {
        return this.initiator;
    }

    public Path getPath() {
        return this.baseDir.resolve(this.relativePath);
    }

    public Path getRelativePath() {
        return this.relativePath;
    }

    public UpdateType getType() {
        return this.type;
    }

    public static Update creation(Path baseDir, Path relPath, Path initiator) {
        return new Update(baseDir, relPath, initiator, UpdateType.CREATION);
    }

    public static Update deletion(Path baseDir, Path relPath, Path initiator) {
        return new Update(baseDir, relPath, initiator, UpdateType.DELETION);
    }

    public static Update modification(Path baseDir, Path relPath, Path initiator) {
        return new Update(baseDir, relPath, initiator, UpdateType.MODIFICATION);
    }
}