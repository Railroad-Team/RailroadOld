package io.github.railroad.objects;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * @author TurtyWurty
 */
public class RailroadBorderPane extends BorderPane {

    public RailroadBorderPane() {

    }

    public RailroadBorderPane(final Node center) {
        super(center);
    }

    public RailroadBorderPane(final Node center, final Node top, final Node right, final Node bottom,
            final Node left) {
        super(center, top, right, bottom, left);
    }
}
