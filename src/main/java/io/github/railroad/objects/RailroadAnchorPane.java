package io.github.railroad.objects;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class RailroadAnchorPane extends AnchorPane implements RailroadPane {

	public RailroadAnchorPane(final Node... children) {
		super(children);
	}

	@Override
	public void add(final Node item) {
		getChildren().add(item);
	}

	@Override
	public void remove(final Node item) {
		if (getChildren().contains(item)) {
			getChildren().remove(item);
			if (getChildren().isEmpty() && getParent() instanceof RailroadPane) {
				((RailroadPane) getParent()).remove(this);
			}
		}
	}
}
