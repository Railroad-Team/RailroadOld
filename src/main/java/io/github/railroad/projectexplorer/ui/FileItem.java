package io.github.railroad.projectexplorer.ui;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.function.UnaryOperator;

import io.github.railroad.projectexplorer.model.GraphicFactory;
import javafx.scene.Node;

public class FileItem extends PathItem {
    private FileTime lastModified;

    private FileItem(Path path, FileTime lastModified, Node graphic, UnaryOperator<Path> projector) {
        super(path, graphic, projector);
        this.lastModified = lastModified;
    }

    @Override
    public final boolean isDirectory() {
        return false;
    }

    public boolean updateModificationTime(FileTime lastModified) {
        if (lastModified.compareTo(this.lastModified) > 0) {
            this.lastModified = lastModified;
            return true;
        }
        return false;
    }

    public static FileItem create(Path path, FileTime lastModified, GraphicFactory graphicFactory,
            UnaryOperator<Path> projector) {
        return new FileItem(path, lastModified, graphicFactory.createGraphic(projector.apply(path), false), projector);
    }
}