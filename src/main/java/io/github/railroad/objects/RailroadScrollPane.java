package io.github.railroad.objects;

import org.fxmisc.flowless.Virtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;

import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * @author TurtyWurty
 */
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
