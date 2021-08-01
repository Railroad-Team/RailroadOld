package io.github.railroad.objects;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class RailroadBorderPane extends BorderPane implements RailroadPane {

	public RailroadBorderPane() {

	}

	public RailroadBorderPane(final Node center) {
		super(center);
	}

	public RailroadBorderPane(final Node center, final Node top, final Node right, final Node bottom, final Node left) {
		super(center, top, right, bottom, left);
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
