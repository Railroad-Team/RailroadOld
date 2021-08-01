package io.github.railroad;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;
import org.fxmisc.richtext.CodeArea;

import io.github.railroad.editor.CodeEditor;
import io.github.railroad.editor.SimpleFileEditorController;
import io.github.railroad.objects.ProjectExplorer;
import io.github.railroad.objects.ProjectExplorer.ExplorerTreeItem;
import io.github.railroad.objects.RailroadAnchorPane;
import io.github.railroad.objects.RailroadBorderPane;
import io.github.railroad.objects.RailroadCodeArea;
import io.github.railroad.objects.RailroadMenuBar;
import io.github.railroad.objects.RailroadMenuBar.FileMenu;
import io.github.railroad.objects.RailroadScrollPane;
import io.github.railroad.objects.RailroadSplitPane;
import io.github.railroad.objects.RailroadTabPane;
import io.github.railroad.project.Project;
import io.github.railroad.project.settings.ThemeSettings;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
import javafx.util.Duration;

/**
 * @author TurtyWurty
 */
public class Setup {

	protected final CodeEditor codeEditor;
	private final SimpleFileEditorController baseController;
	private final List<SimpleFileEditorController> fileControllers = new ArrayList<>();
	protected final Rectangle2D primaryScreenBounds;
	protected final RailroadBorderPane mainPane;
	private final RailroadTabPane editorTabPane;
	private RailroadCodeArea baseCodeArea;
	private RailroadScrollPane<RailroadCodeArea> baseCodeScrollPane;
	private final RailroadMenuBar menuBar;
	private final TreeView<String> projectExplorer;
	private final RailroadSplitPane mainSplitPane;
	private final RailroadAnchorPane anchorPane;
	private final Triple<Label, ProgressBar, Button> fileLoadItems;
	// TODO: Add this back if I find it is needed anywhere
	// private final HBox fileLoadPlacement;

	private final Project project;

	/**
	 * Sets up the core layout and project settings.
	 *
	 * @param darkMode - Whether or not dark mode should be enabled.
	 */
	public Setup(final boolean darkMode) {
		// Core
		this.primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		this.project = new Project(new ThemeSettings(darkMode));

		// Code Editor
		this.codeEditor = new CodeEditor(Executors.newSingleThreadExecutor());
		this.editorTabPane = new RailroadTabPane();
		this.baseController = new SimpleFileEditorController();
		this.baseCodeArea = this.codeEditor.createCodeArea();
		this.baseController.textArea = this.baseCodeArea;
		this.baseCodeScrollPane = new RailroadScrollPane<>(this.baseCodeArea);
		this.fileLoadItems = createFileLoad();

		// Project Explorer
		this.projectExplorer = createProjectExplorer();

		// Primary
		this.mainPane = new RailroadBorderPane();
		this.menuBar = createTopMenu();
		this.mainSplitPane = createMainSplit();
		this.anchorPane = anchorMainSplit();
		/* this.fileLoadPlacement = */placeFileLoad();

		onMenuAction();
		onProjectExplorerAction();
	}

	/**
	 * Create the {@link AnchorPane} for the primary vertical {@link SplitPane}.
	 *
	 * @return The created {@link AnchorPane}.
	 */
	private RailroadAnchorPane anchorMainSplit() {
		final var localAnchorPane = new RailroadAnchorPane(this.mainSplitPane);
		AnchorPane.setBottomAnchor(this.mainSplitPane, 5.0D);
		AnchorPane.setLeftAnchor(this.mainSplitPane, 5.0D);
		AnchorPane.setRightAnchor(this.mainSplitPane, 5.0D);
		AnchorPane.setTopAnchor(this.mainSplitPane, 5.0D);
		this.mainPane.setCenter(localAnchorPane);
		return localAnchorPane;
	}

	/**
	 * Creates a {@link CodeArea} for the file as well as shuffling around the main
	 * split pane in order to let the file be opened.
	 *
	 * @param tabPane    - The primary {@link TabPane} that holds the code editors.
	 * @param scrollPane - The base {@link ScrollPane} that holds the base code
	 *                   editor.
	 * @param file       - The {@link File} that this Code Area is being created
	 *                   for.
	 */
	private void createCodeArea(final RailroadTabPane tabPane, final RailroadScrollPane<RailroadCodeArea> scrollPane,
			final File file) {
		if (file != null) {
			if (tabPane.getParent() == null && scrollPane.getRealParent()instanceof final RailroadSplitPane parent
					&& scrollPane.getRealParent().getParent() instanceof RailroadAnchorPane) {
				final var scrollPaneIndex = parent.getItems().indexOf(scrollPane);
				parent.getItems().remove(scrollPaneIndex);

				if (tabPane.getTabs().isEmpty()) {
					final var newTab = new Tab(file.getName(), scrollPane);
					tabPane.getTabs().add(newTab);
					onTabAction(newTab);
					scrollPane.setRealParent(tabPane);

					if (this.fileControllers.isEmpty()) {
						this.fileControllers.add(new SimpleFileEditorController());
					}

					this.fileControllers.get(0).textArea = scrollPane.getContent();
					this.fileControllers.get(0).setFile(file);
				} else {
					final var controller = new SimpleFileEditorController();
					this.fileControllers.add(controller);

					final var newCodeArea = this.codeEditor.createCodeArea();
					controller.textArea = newCodeArea;
					controller.setFile(file);

					final var newScrollPane = new RailroadScrollPane<>(newCodeArea);
					final var newTab = new Tab(file.getName(), newScrollPane);
					tabPane.getTabs().add(newTab);
					onTabAction(newTab);
					newScrollPane.setRealParent(tabPane);
				}
				parent.getItems().add(scrollPaneIndex, tabPane);
			} else if (tabPane.getParent() != null && tabPane.getTabs().isEmpty()) {
				final var parent = (RailroadSplitPane) tabPane.getParent();
				final var tabPaneIndex = parent.getItems().indexOf(tabPane);
				parent.getItems().remove(tabPaneIndex);
				parent.getItems().add(tabPaneIndex, scrollPane);
				scrollPane.setRealParent(parent);

				if (this.fileControllers.isEmpty()) {
					this.fileControllers.add(new SimpleFileEditorController());
				}

				this.fileControllers.get(0).textArea = scrollPane.getContent();
				this.fileControllers.get(0).setFile(file);
			} else if (!tabPane.getTabs().isEmpty()) {
				final var newCodeArea = this.codeEditor.createCodeArea();
				final var newScrollPane = new RailroadScrollPane<>(newCodeArea);
				final var newTab = new Tab(file.getName(), newScrollPane);
				tabPane.getTabs().add(newTab);
				onTabAction(newTab);
				newScrollPane.setRealParent(tabPane);

				final var controller = new SimpleFileEditorController();
				this.fileControllers.add(controller);
				controller.textArea = newCodeArea;
				controller.setFile(file);
			}
		} else if (this.editorTabPane.getTabs().isEmpty()) {
			this.mainSplitPane.getItems().remove(1);
			this.baseCodeArea = this.codeEditor.createCodeArea();
			this.baseCodeScrollPane = new RailroadScrollPane<>(this.baseCodeArea);
			this.baseCodeScrollPane.setRealParent(this.mainSplitPane);
			this.mainSplitPane.getItems().add(1, this.baseCodeScrollPane);
		}

		// TODO: Use this. But fix the tab pane and scroll bar issues that occur
		// dividerAdjust();
	}

	/**
	 * <strong>Currently unused</strong> method to create the (File Loading): Status
	 * Message, {@link ProgressBar} and Load Changes {@link Button}.
	 *
	 * @return A {@link Triple} containing the Loading Status, {@link ProgressBar}
	 *         and Load Changes {@link Button}.
	 */
	private Triple<Label, ProgressBar, Button> createFileLoad() {
		final var statusMessage = new Label("Checking for Changes...");
		statusMessage.prefWidth(150);
		statusMessage.setId("statusMessage");
		// this.fileControllers.get(0).statusMessage = statusMessage;

		final var progressBar = new ProgressBar(0);
		progressBar.prefWidth(150);
		progressBar.setId("progressBar");
		// this.fileControllers.get(0).progressBar = progressBar;

		final var loadChangesBtn = new Button("Load Changes");
		loadChangesBtn.setId("loadChangesButton");
		loadChangesBtn.setOnAction(event -> {
			if (this.fileControllers.isEmpty()) {
				loadChangesBtn.setDisable(true);
				return;
			}

			SimpleFileEditorController selectedFileController = null;
			if (this.editorTabPane.getParent().getParent().equals(this.anchorPane)) {
				final Node possibleArea = this.editorTabPane.getSelectionModel().getSelectedItem().getContent();
				final List<SimpleFileEditorController> controllers = this.fileControllers.stream()
						.filter(controller -> controller.textArea.equals(possibleArea)).collect(Collectors.toList());
				if (controllers.isEmpty()) {
					loadChangesBtn.setDisable(true);
					return;
				}
				selectedFileController = controllers.get(0);
			}

			if (this.baseCodeArea != null && this.baseCodeArea.getParent() != null) {
				final List<SimpleFileEditorController> controllers = this.fileControllers.stream()
						.filter(controller -> controller.textArea.equals(this.baseCodeArea)).collect(Collectors.toList());
				if (controllers.isEmpty()) {
					loadChangesBtn.setDisable(true);
					return;
				}
				selectedFileController = controllers.get(0);
			}

			if (selectedFileController != null) {
				selectedFileController.loadChanges();
			}
		});
		// this.fileControllers.get(0).loadChangesButton = loadChangesButton;

		return Triple.of(statusMessage, progressBar, loadChangesBtn);
	}

	/**
	 * Creates the primary vertical {@link SplitPane} that holds the
	 * {@link ProjectExplorer}, {@link CodeEditor} and any other panel on the right.
	 *
	 * @return The created vertical {@link SplitPane}.
	 */
	private RailroadSplitPane createMainSplit() {
		final var splitPane = new RailroadSplitPane(this.projectExplorer, this.baseCodeScrollPane);
		splitPane.setOrientation(Orientation.VERTICAL);
		this.baseCodeScrollPane.setRealParent(splitPane);
		timedDividerAdjust(splitPane);
		return splitPane;
	}

	/**
	 * Creates the {@link ProjectExplorer} tab.
	 *
	 * @return The {@link ProjectExplorer} as a {@link TreeView}.
	 */
	private TreeView<String> createProjectExplorer() {
		final var localProjectExplorer = new ProjectExplorer().createProjectExplorer(this.project);
		localProjectExplorer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		return localProjectExplorer;
	}

	/**
	 * Creates the top {@link MenuBar} for all of the context menus.
	 *
	 * @return The top {@link MenuBar}.
	 */
	private RailroadMenuBar createTopMenu() {
		final var openItem = new MenuItem("Open");
		final var saveItem = new MenuItem("Save");
		final var fileMenu = new FileMenu(openItem, saveItem);
		final var localMenuBar = new RailroadMenuBar(fileMenu);
		this.mainPane.setTop(localMenuBar);
		return localMenuBar;
	}

	/**
	 * Resets the main {@link SplitPane}'s divider position.
	 */
	private void dividerAdjust() {
		dividerAdjust(this.mainSplitPane);
	}

	/**
	 * Resets the {@link SplitPane}'s divider position.
	 *
	 * @param split - The split pane to have reset.
	 */
	private void dividerAdjust(final RailroadSplitPane split) {
		new Timeline(new KeyFrame(Duration.millis(10), event -> split.setDividerPosition(0, 0.15D))).play();
	}

	/**
	 * Creates the functionality for the {@link MenuItem}s in the top
	 * {@link MenuBar}.
	 */
	private void onMenuAction() {
		this.menuBar.fileMenu.openItem.setOnAction(event -> {
			final SimpleFileEditorController controller;
			if (!this.fileControllers.isEmpty()) {
				controller = this.fileControllers.get(this.fileControllers.size() - 1);
			} else {
				controller = new SimpleFileEditorController();
				this.fileControllers.add(controller);
			}
			createCodeArea(this.editorTabPane, this.baseCodeScrollPane, controller.openFile());
		});

		this.menuBar.fileMenu.saveItem.setOnAction(event -> {
			if (this.fileControllers.isEmpty()) {
				this.menuBar.fileMenu.saveItem.setDisable(true);
				return;
			}

			SimpleFileEditorController selectedFileController = null;
			if (this.editorTabPane.getParent() != null
					&& this.editorTabPane.getParent().getParent().equals(this.anchorPane)) {
				final var possibleArea = (RailroadCodeArea) this.editorTabPane.getSelectionModel().getSelectedItem()
						.getContent();
				final List<SimpleFileEditorController> controllers = this.fileControllers.stream()
						.filter(controller -> controller.textArea.equals(possibleArea)).collect(Collectors.toList());
				if (controllers.isEmpty()) {
					this.menuBar.fileMenu.saveItem.setDisable(true);
					return;
				}
				selectedFileController = controllers.get(0);
			}

			if (this.baseCodeArea != null && this.baseCodeArea.getParent() != null) {
				final List<SimpleFileEditorController> controllers = this.fileControllers.stream()
						.filter(controller -> controller.textArea.equals(this.baseCodeArea)).collect(Collectors.toList());
				if (controllers.isEmpty()) {
					this.menuBar.fileMenu.saveItem.setDisable(true);
					return;
				}
				selectedFileController = controllers.get(0);
			}

			if (selectedFileController != null) {
				selectedFileController.saveFile();
			}
		});
	}

	/**
	 * Creates the functionality for what happens when double clicking on a file in
	 * the {@link ProjectExplorer}.
	 */
	private void onProjectExplorerAction() {
		this.projectExplorer.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				final List<TreeItem<String>> items = this.projectExplorer.getSelectionModel().getSelectedItems();
				for (final var item : items) {
					createCodeArea(this.editorTabPane, this.baseCodeScrollPane,
							new File(((ExplorerTreeItem) item).getFullName()));
				}
			}
		});
	}

	/**
	 * Creates the functionality for what happens when a {@link Tab} is closed.
	 *
	 * @param tab - The tab to create the close functionality for.
	 */
	private void onTabAction(final Tab tab) {
		tab.setOnClosed(event -> createCodeArea(this.editorTabPane, this.baseCodeScrollPane, null));
	}

	/**
	 * Creates the placement area for the file loading items.
	 *
	 * @return The resulting {@link HBox} layout.
	 */
	private HBox placeFileLoad() {
		final var hboxLeft = new HBox(this.fileLoadItems.getLeft(), this.fileLoadItems.getMiddle());
		hboxLeft.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(hboxLeft, Priority.ALWAYS);

		final var hboxRight = new HBox(this.fileLoadItems.getRight());
		hboxRight.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(hboxRight, Priority.ALWAYS);

		final var hbox = new HBox(hboxLeft, hboxRight);
		hbox.setPadding(new Insets(0, 5, 5, 5));
		this.mainPane.setBottom(hbox);
		return hbox;
	}

	/**
	 * Resets the main {@link SplitPane}'s divider position with delay.<br>
	 * <br>
	 * -- Used for properly updating the divider.
	 */
	private void timedDividerAdjust() {
		timedDividerAdjust(this.mainSplitPane);
	}

	/**
	 * Resets the {@link SplitPane}'s divider position.<br>
	 * <br>
	 * -- Used for properly updating the divider.
	 *
	 * @param split - The {@link SplitPane} to reset.
	 */
	private void timedDividerAdjust(final RailroadSplitPane split) {
		split.setDividerPosition(0, 0.15D);
		new Timeline(new KeyFrame(Duration.millis(10), event -> split.setDividerPosition(0, 0.15D))).play();
	}
}
