package io.github.railroad.projectexplorer.ui;

import java.nio.file.Path;

public interface Reporter {
    void reportCreation(Path baseDir, Path relPath, Path initiator);

    void reportDeletion(Path baseDir, Path relPath, Path initiator);

    void reportError(Throwable error);

    void reportModification(Path baseDir, Path relPath, Path initiator);
}