package io.github.railroad.projectexplorer;

import java.nio.file.Path;
import java.util.function.BiFunction;

import org.reactfx.EventStream;

import io.github.railroad.projectexplorer.DirectoryModel.GraphicFactory;
import io.github.railroad.utility.ImageUtility;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;

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

    /**
     * Factory to create graphics for {@link TreeItem}s in a {@link DirectoryModel}.
     */
    @FunctionalInterface
    interface GraphicFactory extends BiFunction<Path, Boolean, Node> {
        @Override
        default Node apply(Path path, Boolean isDirectory) {
            return createGraphic(path, isDirectory);
        }

        Node createGraphic(Path path, boolean isDirectory);
    }

    /**
     * Represents an update to the directory model.
     */
    class Update {
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

        static Update creation(Path baseDir, Path relPath, Path initiator) {
            return new Update(baseDir, relPath, initiator, UpdateType.CREATION);
        }

        static Update deletion(Path baseDir, Path relPath, Path initiator) {
            return new Update(baseDir, relPath, initiator, UpdateType.DELETION);
        }

        static Update modification(Path baseDir, Path relPath, Path initiator) {
            return new Update(baseDir, relPath, initiator, UpdateType.MODIFICATION);
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
    }

    /**
     * Types of updates to the director model.
     */
    enum UpdateType {
        /** Indicates a new directory entry. */
        CREATION,

        /** Indicates removal of a directory entry. */
        DELETION,

        /** Indicates file modification. */
        MODIFICATION,
    }
}

class DefaultGraphicFactory implements GraphicFactory {
    @Override
    public Node createGraphic(Path path, boolean isDirectory) {
        return new ImageView(ImageUtility.getIconImage(path.toFile()));
    }
}