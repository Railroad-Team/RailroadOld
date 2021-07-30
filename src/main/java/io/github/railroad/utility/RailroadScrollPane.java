package io.github.railroad.utility;

import org.fxmisc.flowless.Virtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;

import javafx.scene.Node;
import javafx.scene.Parent;

public class RailroadScrollPane<V extends Node & Virtualized> extends VirtualizedScrollPane<V> {

	public Parent realParent;

	public RailroadScrollPane(final V content) {
		super(content);
	}

	public Parent getRealParent() {
		return this.realParent;
	}

	public void setRealParent(final Parent parent) {
		this.realParent = parent;
	}
}
