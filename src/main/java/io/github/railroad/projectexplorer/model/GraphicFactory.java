package io.github.railroad.projectexplorer.model;

import java.nio.file.Path;
import java.util.function.BiFunction;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 * Factory to create graphics for {@link TreeItem}s in a {@link DirectoryModel}.
 */
@FunctionalInterface
public interface GraphicFactory extends BiFunction<Path, Boolean, Node> {
    @Override
    default Node apply(Path path, Boolean isDirectory) {
        return createGraphic(path, isDirectory);
    }
    
    Node createGraphic(Path path, boolean isDirectory);
}