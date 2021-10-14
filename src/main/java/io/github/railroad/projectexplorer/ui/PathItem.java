package io.github.railroad.projectexplorer.ui;

import java.nio.file.Path;
import java.util.function.UnaryOperator;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

public abstract class PathItem extends TreeItem<String> {

    private final UnaryOperator<Path> projector;
    private final SimpleObjectProperty<Path> pathProperty;

    protected PathItem(Path path, Node graphic, UnaryOperator<Path> projector) {
        super(path.getFileName().toString(), graphic);
        this.pathProperty = new SimpleObjectProperty<>(path);
        this.projector = projector;
    }

    public DirItem asDirItem() {
        return (DirItem) this;
    }

    public FileItem asFileItem() {
        return (FileItem) this;
    }

    public final Path getPath() {
        return this.projector.apply(pathProperty().getValue());
    }

    public PathItem getRelChild(Path relPath) {
        if (relPath.getNameCount() != 1)
            return null;

        final Path childValue = getPath().resolve(relPath);
        for (final TreeItem<String> child : getChildren()) {
            final PathItem pathCh = (PathItem) child;
            if (pathCh.getPath().equals(childValue))
                return pathCh;
        }
        return null;
    }

    public abstract boolean isDirectory();

    @Override
    public final boolean isLeaf() {
        return !isDirectory();
    }

    public SimpleObjectProperty<Path> pathProperty() {
        return this.pathProperty;
    }

    protected final UnaryOperator<Path> getProjector() {
        return this.projector;
    }

    protected PathItem resolve(Path relPath) {
        final int len = relPath.getNameCount();
        if (len == 0)
            return this;
        final PathItem child = getRelChild(relPath.getName(0));
        if (child == null)
            return null;
        if (len == 1)
            return child;
        return child.resolve(relPath.subpath(1, len));
    }
}