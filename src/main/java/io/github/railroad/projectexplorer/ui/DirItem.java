package io.github.railroad.projectexplorer.ui;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.function.UnaryOperator;

import io.github.railroad.projectexplorer.model.GraphicFactory;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

public class DirItem extends PathItem {
    private final UnaryOperator<Path> injector;
    
    protected DirItem(Path path, Node graphic, UnaryOperator<Path> projector, UnaryOperator<Path> injector) {
        super(path, graphic, projector);
        this.injector = injector;
    }
    
    public DirItem addChildDir(Path dirName, GraphicFactory graphicFactory) {
        if (dirName.getNameCount() != 1)
            return null;
        
        final int index = getDirInsertionIndex(dirName.toString());
        
        final DirItem child = DirItem.create(inject(getPath().resolve(dirName)), graphicFactory, getProjector(),
                getInjector());
        getChildren().add(index, child);
        return child;
    }
    
    public FileItem addChildFile(Path fileName, FileTime lastModified, GraphicFactory graphicFactory) {
        if (fileName.getNameCount() != 1)
            return null;
        
        final int index = getFileInsertionIndex(fileName.toString());
        
        final FileItem child = FileItem.create(inject(getPath().resolve(fileName)), lastModified, graphicFactory,
                getProjector());
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
    
    public static DirItem create(Path path, GraphicFactory graphicFactory, UnaryOperator<Path> projector,
            UnaryOperator<Path> injector) {
        return new DirItem(path, graphicFactory.createGraphic(projector.apply(path), true), projector, injector);
    }
}