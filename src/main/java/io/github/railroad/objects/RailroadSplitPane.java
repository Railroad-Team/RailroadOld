package io.github.railroad.objects;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class RailroadSplitPane extends SplitPane implements RailroadPane {

	public RailroadSplitPane(final Node... items) {
		super(items);
	}

	@Override
	public void add(final Node item) {
		getItems().add(item);
	}

	@Override
	public void remove(final Node item) {
		if (getItems().contains(item)) {
			getItems().remove(item);
			if (getParent() instanceof RailroadPane) {
				if (getItems().isEmpty()) {
					((RailroadPane) getParent()).remove(this);
				} else if (getItems().size() == 1) {
					((RailroadPane) getParent()).remove(this);
					((RailroadPane) getParent()).add(getItems().get(0));
				}
			}
		}
	}
}
