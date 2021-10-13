package io.github.railroad.projectexplorer;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.UnaryOperator;

import io.github.railroad.projectexplorer.DirectoryModel.GraphicFactory;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
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

class DirItem extends PathItem {

    private final UnaryOperator<Path> injector;

    protected DirItem(Path path, Node graphic, UnaryOperator<Path> projector, UnaryOperator<Path> injector) {
        super(path, graphic, projector);
        this.injector = injector;
    }

    public static DirItem create(Path path, GraphicFactory graphicFactory, UnaryOperator<Path> projector,
            UnaryOperator<Path> injector) {
        return new DirItem(path, graphicFactory.createGraphic(projector.apply(path), true), projector,
                injector);
    }

    public DirItem addChildDir(Path dirName, GraphicFactory graphicFactory) {
        if (dirName.getNameCount() != 1)
            return null;

        final int index = getDirInsertionIndex(dirName.toString());

        final DirItem child = DirItem.create(inject(getPath().resolve(dirName)), graphicFactory,
                getProjector(), getInjector());
        getChildren().add(index, child);
        return child;
    }

    public FileItem addChildFile(Path fileName, FileTime lastModified, GraphicFactory graphicFactory) {
        if (fileName.getNameCount() != 1)
            return null;

        final int index = getFileInsertionIndex(fileName.toString());

        final FileItem child = FileItem.create(inject(getPath().resolve(fileName)), lastModified,
                graphicFactory, getProjector());
        getChildren().add(index, child);
        return child;
    }

    public final Path inject(Path path) {
        return this.injector.apply(path);
    }

    @Override
    public final boolean isDirectory() {
        return true;
    }

    protected final UnaryOperator<Path> getInjector() {
        return this.injector;
    }

    private int getDirInsertionIndex(String dirName) {
        final ObservableList<TreeItem<String>> children = getChildren();
        final int count = children.size();
        for (int index = 0; index < count; ++index) {
            final PathItem child = (PathItem) children.get(index);
            if (!child.isDirectory())
                return index;
            final String childName = child.getPath().getFileName().toString();
            if (childName.compareToIgnoreCase(dirName) > 0)
                return index;
        }
        return count;
    }

    private int getFileInsertionIndex(String fileName) {
        final ObservableList<TreeItem<String>> children = getChildren();
        final int count = children.size();
        for (int index = 0; index < count; ++index) {
            final PathItem child = (PathItem) children.get(index);
            if (!child.isDirectory()) {
                final String childName = child.getPath().getFileName().toString();
                if (childName.compareToIgnoreCase(fileName) > 0)
                    return index;
            }
        }
        return count;
    }
}

class FileItem extends PathItem {
    private FileTime lastModified;

    private FileItem(Path path, FileTime lastModified, Node graphic, UnaryOperator<Path> projector) {
        super(path, graphic, projector);
        this.lastModified = lastModified;
    }

    public static FileItem create(Path path, FileTime lastModified, GraphicFactory graphicFactory,
            UnaryOperator<Path> projector) {
        return new FileItem(path, lastModified, graphicFactory.createGraphic(projector.apply(path), false),
                projector);
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
}

record ParentChild(DirItem parent, PathItem child) {
}

interface Reporter {
    void reportCreation(Path baseDir, Path relPath, Path initiator);

    void reportDeletion(Path baseDir, Path relPath, Path initiator);

    void reportError(Throwable error);

    void reportModification(Path baseDir, Path relPath, Path initiator);
}

class TopLevelDirItem extends DirItem {
    private final GraphicFactory graphicFactory;
    private final Reporter reporter;

    TopLevelDirItem(Path path, GraphicFactory graphicFactory, UnaryOperator<Path> projector,
            UnaryOperator<Path> injector, Reporter reporter) {
        super(path, graphicFactory.createGraphic(projector.apply(path), true), projector, injector);
        this.graphicFactory = graphicFactory;
        this.reporter = reporter;
    }

    public void addDirectory(Path relPath, Path initiator) {
        final PathItem item = resolve(relPath);
        if (item == null || !item.isDirectory()) {
            sync(PathNode.directory(getPath().resolve(relPath), Collections.emptyList()), initiator);
        }
    }

    public void addFile(Path relPath, FileTime lastModified, Path initiator) {
        updateFile(relPath, lastModified, initiator);
    }

    public boolean contains(Path relPath) {
        return resolve(relPath) != null;
    }

    public void remove(Path relPath, Path initiator) {
        final PathItem item = resolve(relPath);
        if (item != null) {
            removeNode(item, initiator);
        }
    }

    public void sync(PathNode tree, Path initiator) {
        final Path path = tree.getPath();
        final Path relPath = getPath().relativize(path);
        final ParentChild pc = resolveInParent(relPath);
        final DirItem parent = pc.parent();
        final PathItem item = pc.child();
        if (parent != null) {
            syncChild(parent, relPath.getFileName(), tree, initiator);
        } else if (item == null) { // neither path nor its parent present in model
            raise(new NoSuchElementException(
                    "Parent directory for " + relPath + " does not exist within " + getValue()));
        } else { // resolved to top-level dir
            assert item == this;
            if (tree.isDirectory()) {
                syncContent(this, tree, initiator);
            } else {
                raise(new IllegalArgumentException(
                        "Cannot replace top-level directory " + getValue() + " with a file"));
            }
        }
    }

    public void updateModificationTime(Path relPath, FileTime lastModified, Path initiator) {
        updateFile(relPath, lastModified, initiator);
    }

    private void raise(Exception exception) {
        try {
            throw exception;
        } catch (final Exception ex) {
            this.reporter.reportError(ex);
        }
    }

    private void removeNode(PathItem node, Path initiator) {
        signalDeletionRecursively(node, initiator);
        node.getParent().getChildren().remove(node);
    }

    private ParentChild resolveInParent(Path relPath) {
        final int len = relPath.getNameCount();
        if (len == 0)
            return new ParentChild(null, this);
        if (len == 1) {
            if (getPath().resolve(relPath).equals(pathProperty().getValue()))
                return new ParentChild(null, this);
            return new ParentChild(this, getRelChild(relPath.getName(0)));
        }
        final PathItem parent = resolve(relPath.subpath(0, len - 1));
        if (parent == null || !parent.isDirectory())
            return new ParentChild(null, null);
        final PathItem child = parent.getRelChild(relPath.getFileName());
        return new ParentChild(parent.asDirItem(), child);
    }

    private void signalDeletionRecursively(PathItem node, Path initiator) {
        for (final TreeItem<String> child : node.getChildren()) {
            signalDeletionRecursively((PathItem) child, initiator);
        }
        this.reporter.reportDeletion(getPath(), getPath().relativize(getProjector().apply(node.getPath())),
                initiator);
    }

    private void syncChild(DirItem parent, Path childName, PathNode tree, Path initiator) {
        final PathItem child = parent.getRelChild(childName);
        if (child != null && child.isDirectory() != tree.isDirectory()) {
            removeNode(child, null);
        }
        if (child == null) {
            if (tree.isDirectory()) {
                final DirItem dirChild = parent.addChildDir(childName, this.graphicFactory);
                this.reporter.reportCreation(getPath(), getPath().relativize(dirChild.getPath()), initiator);
                syncContent(dirChild, tree, initiator);
            } else {
                final FileItem fileChild = parent.addChildFile(childName, tree.getLastModified(),
                        this.graphicFactory);
                this.reporter.reportCreation(getPath(), getPath().relativize(fileChild.getPath()), initiator);
            }
        } else if (child.isDirectory()) {
            syncContent(child.asDirItem(), tree, initiator);
        } else if (child.asFileItem().updateModificationTime(tree.getLastModified())) {
            this.reporter.reportModification(getPath(), getPath().relativize(child.getPath()), initiator);
        }
    }

    private void syncContent(DirItem dir, PathNode tree, Path initiator) {
        final Set<Path> desiredChildren = new HashSet<>();
        for (final PathNode child : tree.getChildren()) {
            desiredChildren.add(child.getPath());
        }

        final var actualChildren = new ArrayList<TreeItem<String>>(dir.getChildren());

        // remove undesired children
        for (final TreeItem<String> child : actualChildren) {
            final PathItem pathItem = (PathItem) child;
            if (!desiredChildren.contains(getProjector().apply(pathItem.getPath()))) {
                removeNode(pathItem, null);
            }
        }

        // synchronize desired children
        for (final PathNode child : tree.getChildren()) {
            sync(child, initiator);
        }
    }

    private void updateFile(Path relPath, FileTime lastModified, Path initiator) {
        final PathItem item = resolve(relPath);
        if (item == null || item.isDirectory()) {
            sync(PathNode.file(getPath().resolve(relPath), lastModified), initiator);
        }
    }
}