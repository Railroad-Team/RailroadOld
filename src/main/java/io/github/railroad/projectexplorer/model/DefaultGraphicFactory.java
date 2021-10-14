package io.github.railroad.projectexplorer.model;

import java.nio.file.Path;

import io.github.railroad.utility.ImageUtility;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class DefaultGraphicFactory implements GraphicFactory {
    @Override
    public Node createGraphic(Path path, boolean isDirectory) {
        return new ImageView(ImageUtility.getIconImage(path.toFile()));
    }
}