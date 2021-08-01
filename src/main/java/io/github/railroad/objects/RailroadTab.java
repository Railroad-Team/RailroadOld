package io.github.railroad.objects;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class RailroadTab extends Tab {

	public RailroadTab() {
		super("", null);
	}

	public RailroadTab(final String text) {
		this(text, null);
	}

	public RailroadTab(final String text, final Node content) {
		super("", content);
		setupLabel(text);
	}

	private void setupLabel(final String text) {
		final var label = new Label(text);
		setGraphic(label);
		label.setOnDragDetected(event -> {
			final Dragboard dragboard = label.startDragAndDrop(TransferMode.MOVE);
			final var clipboardContent = new ClipboardContent();
			clipboardContent.putString(RailroadTabPane.TAB_DRAG_KEY);
			dragboard.setContent(clipboardContent);
			RailroadTabPane.draggingTab.set(this);
			event.consume();
		});
	}
}
