package io.github.railroad.objects;

import org.fxmisc.flowless.Virtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;

import javafx.scene.Node;
import javafx.scene.Parent;

public class RailroadScrollPane<V extends Node & Virtualized> extends VirtualizedScrollPane<V> implements RailroadPane {

	public Parent realParent;

	public RailroadScrollPane(final V content) {
		super(content);
	}

	@Override
	public void add(final Node item) {
		getChildren().add(item);
	}

	public Parent getRealParent() {
		return this.realParent;
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

	public void setRealParent(final Parent parent) {
		this.realParent = parent;
	}
}
