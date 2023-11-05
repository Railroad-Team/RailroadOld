package io.github.railroad.project.pages;

import javafx.scene.Node;

public abstract class Page {
    private final Node core;
    
    protected Page(Node core) {
        this.core = core;
    }
    
    public Node getCore() {
        return this.core;
    }
}
