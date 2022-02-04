package io.github.railroad.projectexplorer.core;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.reactfx.EventSource;
import org.reactfx.EventStream;

import io.github.railroad.projectexplorer.model.DirectoryModel;
import io.github.railroad.projectexplorer.model.GraphicFactory;
import io.github.railroad.projectexplorer.model.Update;
import io.github.railroad.projectexplorer.ui.PathItem;
import io.github.railroad.projectexplorer.ui.Reporter;
import io.github.railroad.projectexplorer.ui.TopLevelDirItem;
import javafx.scene.control.TreeItem;

public class LiveDirsModel implements DirectoryModel {
    private final TreeItem<String> root = new TreeItem<>();
    private final EventSource<Update> creations = new EventSource<>(), deletions = new EventSource<>(),
        modifications = new EventSource<>();
    private final EventSource<Throwable> errors = new EventSource<>();
    private final Reporter reporter;
    private final Path defaultInitiator;
    private final UnaryOperator<Path> projector, injector;

    private GraphicFactory graphicFactory = DEFAULT_GRAPHIC_FACTORY;

    protected LiveDirsModel(Path defaultInitiator, UnaryOperator<Path> projector, UnaryOperator<Path> injector) {
        this.defaultInitiator = defaultInitiator;
        this.projector = projector;
        this.injector = injector;
        this.reporter = new Reporter() {
            @Override
            public void reportCreation(Path baseDir, Path relPath, Path initiator) {
                LiveDirsModel.this.creations.push(Update.creation(baseDir, relPath, initiator));
            }

            @Override
            public void reportDeletion(Path baseDir, Path relPath, Path initiator) {
                LiveDirsModel.this.deletions.push(Update.deletion(baseDir, relPath, initiator));
            }

            @Override
            public void reportError(Throwable error) {
                LiveDirsModel.this.errors.push(error);
            }

            @Override
            public void reportModification(Path baseDir, Path relPath, Path initiator) {
                LiveDirsModel.this.modifications.push(Update.modification(baseDir, relPath, initiator));
            }
        };
    }

    @Override
    public boolean contains(Path path) {
        return topLevelAncestorStream(path).anyMatch(r -> r.contains(r.getPath().relativize(path)));
    }

    public boolean containsPrefixOf(Path path) {
        return this.root.getChildren().stream().anyMatch(item -> item instanceof final PathItem pathItem
            && path.startsWith(this.projector.apply(pathItem.getPath())));
    }

    @Override
    public EventStream<Update> creations() {
        return this.creations;
    }

    @Override
    public EventStream<Update> deletions() {
        return this.deletions;
    }

    public EventStream<Throwable> errors() {
        return this.errors;
    }

    @Override
    public TreeItem<String> getRoot() {
        return this.root;
    }

    @Override
    public EventStream<Update> modifications() {
        return this.modifications;
    }

    @Override
    public void setGraphicFactory(GraphicFactory factory) {
        this.graphicFactory = factory != null ? factory : DEFAULT_GRAPHIC_FACTORY;
    }

    void addDirectory(Path path, Path initiator) {
        topLevelAncestorStream(path).forEach(r -> {
            final Path relPath = r.getPath().relativize(path);
            r.addDirectory(relPath, initiator);
        });
    }

    void addFile(Path path, Path initiator, FileTime lastModified) {
        topLevelAncestorStream(path).forEach(r -> {
            final Path relPath = r.getPath().relativize(path);
            r.addFile(relPath, lastModified, initiator);
        });
    }

    void addTopLevelDirectory(Path dir) {
        this.root.getChildren().add(new TopLevelDirItem(this.injector.apply(dir), this.graphicFactory, this.projector,
            this.injector, this.reporter));
    }

    void delete(Path path, Path initiator) {
        for (final TopLevelDirItem r : getTopLevelAncestorsNonEmpty(path)) {
            final Path relPath = r.getPath().relativize(path);
            r.remove(relPath, initiator);
        }
    }

    void sync(PathNode tree) {
        topLevelAncestorStream(tree.getPath()).forEach(r -> r.sync(tree, this.defaultInitiator));
    }

    void updateModificationTime(Path path, FileTime lastModified, Path initiator) {
        for (final TopLevelDirItem r : getTopLevelAncestorsNonEmpty(path)) {
            final Path relPath = r.getPath().relativize(path);
            r.updateModificationTime(relPath, lastModified, initiator);
        }
    }

    private List<TopLevelDirItem> getTopLevelAncestors(Path path) {
        return Arrays.asList(topLevelAncestorStream(path).<TopLevelDirItem>toArray(TopLevelDirItem[]::new));
    }

    private List<TopLevelDirItem> getTopLevelAncestorsNonEmpty(Path path) {
        final List<TopLevelDirItem> roots = getTopLevelAncestors(path);
        assert !roots.isEmpty()
            : "path resolved against a dir that was reported to be in the model does not have a top-level ancestor in the model";
        return roots;
    }

    private Stream<TopLevelDirItem> topLevelAncestorStream(Path path) {
        return this.root.getChildren().stream().filter(item -> item instanceof final PathItem pathItem
            && path.startsWith(this.projector.apply(pathItem.getPath()))).map(TopLevelDirItem.class::cast);
    }
}