package io.github.railroad.objects;

import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 * @author TurtyWurty
 */
public class RailroadTab extends Tab {

    public RailroadTab() {
    }

    public RailroadTab(final String text) {
        super(text);
    }

    public RailroadTab(final String text, final Node content) {
        super(text, content);
    }
}
