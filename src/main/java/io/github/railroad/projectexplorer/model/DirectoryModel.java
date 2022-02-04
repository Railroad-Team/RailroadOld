package io.github.railroad.projectexplorer.model;

import java.nio.file.Path;

import org.reactfx.EventStream;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public interface DirectoryModel {
    /**
     * Graphic factory that always returns {@code null}.
     */
    GraphicFactory NO_GRAPHIC_FACTORY = (path, isDir) -> null;

    /**
     * Graphic factory that returns a folder icon for a directory and a document
     * icon for a regular file.
     */
    GraphicFactory DEFAULT_GRAPHIC_FACTORY = new DefaultGraphicFactory();

    /**
     * Indicates whether this directory model contains the given path.
     */
    boolean contains(Path path);

    /**
     * Returns an observable stream of additions to the model.
     */
    EventStream<Update> creations();

    /**
     * Returns an observable stream of removals from the model.
     */
    EventStream<Update> deletions();

    /**
     * Returns a tree item that can be used as a root of a {@link TreeView}. The
     * returned TreeItem does not contain any Path (its {@link TreeItem#getValue()}
     * method returns {@code null}), but its children are roots of directory trees
     * represented in this model. As a consequence, the returned TreeItem shall be
     * used with {@link TreeView#showRootProperty()} set to {@code false}.
     */
    TreeItem<String> getRoot();

    /**
     * Returns an observable stream of file modifications in the model.
     */
    EventStream<Update> modifications();

    /**
     * Sets graphic factory used to create graphics of {@link TreeItem}s in this
     * directory model.
     */
    void setGraphicFactory(GraphicFactory factory);
}