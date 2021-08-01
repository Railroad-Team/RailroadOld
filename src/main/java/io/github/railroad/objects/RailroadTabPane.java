package io.github.railroad.objects;

import org.jetbrains.annotations.Nullable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

public class RailroadTabPane extends TabPane {
	private enum Placement {
		LEFT, RIGHT, TOP, BOTTOM, NONE
	}

	protected static final String TAB_DRAG_KEY = "tab";

	protected static ObjectProperty<RailroadTab> draggingTab = new SimpleObjectProperty<>();

	private static final int DEADZONE_PERCENTAGE = 15;

	public RailroadTabPane(final RailroadTab... tabs) {
		super(tabs);
		setOnDragOver(event -> {
			final var dragboard = event.getDragboard();
			if (dragboard.hasString() && dragboard.getString().equals(TAB_DRAG_KEY)
					&& RailroadTabPane.draggingTab.get() != null && RailroadTabPane.draggingTab.get().getTabPane() != this) {
				event.acceptTransferModes(TransferMode.MOVE);
				event.consume();
			}
		});

		setOnDragDropped(event -> {
			final var dragboard = event.getDragboard();
			if (dragboard.hasString() && dragboard.getString().equals(TAB_DRAG_KEY)
					&& RailroadTabPane.draggingTab.get() != null && RailroadTabPane.draggingTab.get().getTabPane() != this) {
				handlePlacement(event);
				event.setDropCompleted(true);
				RailroadTabPane.draggingTab.set(null);
				event.consume();
			}
		});
	}

	@Nullable
	private RailroadSplitPane createSplit(final Placement placement, final RailroadTab toAdd) {
		final var parent = (RailroadPane) getParent();
		RailroadSplitPane newSplit = null;
		switch (placement) {
		case LEFT:
			newSplit = new RailroadSplitPane(new RailroadTabPane(toAdd), this);
			newSplit.setOrientation(Orientation.VERTICAL);
			break;
		case RIGHT:
			newSplit = new RailroadSplitPane(this, new RailroadTabPane(toAdd));
			newSplit.setOrientation(Orientation.VERTICAL);
			break;
		case TOP:
			newSplit = new RailroadSplitPane(new RailroadTabPane(toAdd), this);
			newSplit.setOrientation(Orientation.HORIZONTAL);
			break;
		case BOTTOM:
			newSplit = new RailroadSplitPane(this, new RailroadTabPane(toAdd));
			newSplit.setOrientation(Orientation.HORIZONTAL);
			break;
		case NONE:
			break;
		}

		if (newSplit != null) {
			parent.remove(this);
			parent.add(newSplit);
		}

		return newSplit;
	}

	private Placement getSplitPlacement(Point2D pos, final Point2D dimension) {
		boolean invertX = false;
		boolean invertY = false;

		if (inDeadzone(pos, dimension, DEADZONE_PERCENTAGE))
			return Placement.NONE;

		if (pos.getY() > dimension.getY() / 2) {
			pos = new Point2D(pos.getX(), dimension.getY() - pos.getY());
			invertY = true;
		}

		if (pos.getX() > dimension.getX() / 2) {
			pos = new Point2D(dimension.getX() - pos.getX(), pos.getY());
			invertX = true;
		}

		if (pos.getX() > pos.getY())
			return invertY ? Placement.BOTTOM : Placement.TOP;

		return invertX ? Placement.RIGHT : Placement.LEFT;
	}

	private void handlePlacement(final DragEvent event) {
		final RailroadTab tab = RailroadTabPane.draggingTab.get();
		final var tabPane = (RailroadTabPane) tab.getTabPane();
		tabPane.getTabs().remove(tab);
		if (tabPane.getTabs().isEmpty() && tabPane.getParent() instanceof RailroadSplitPane) {
			final var splitPane = (RailroadSplitPane) tabPane.getParent();
			splitPane.getItems().remove(tabPane);
			if (splitPane.getItems().size() < 2) {
				removeFromParent(splitPane);
			}
		}

		final Point2D localPoint = this.screenToLocal(new Point2D(event.getScreenX(), event.getScreenY()));

		final Placement splitPlacement = getSplitPlacement(localPoint,
				new Point2D(heightProperty().get(), widthProperty().get()));
		createSplit(splitPlacement, tab);
	}

	private boolean inDeadzone(Point2D pos, final Point2D dimension, final int percentage) {
		final float proportion = 0.01F * percentage;

		pos = new Point2D(pos.getX() - dimension.getX() / 2, pos.getY() - dimension.getY() / 2);

		if (pos.getX() < dimension.getX() / 2 * proportion && pos.getX() > dimension.getX() / 2 * -proportion
				&& pos.getY() < dimension.getY() / 2 * proportion && pos.getY() > dimension.getY() / 2 * -proportion)
			return true;

		return false;
	}

	private void removeFromParent(final Node node) {
		final var parent = node.getParent();
		if (parent instanceof RailroadPane) {
			((RailroadPane) parent).remove(node);
		}
	}
}
