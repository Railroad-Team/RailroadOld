package io.github.railroad;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import io.github.railroad.editor.CodeEditor;
import io.github.railroad.editor.RailroadCodeArea;
import io.github.railroad.editor.SimpleFileEditorController;
import io.github.railroad.project.Project;
import io.github.railroad.project.explorer.ProjectExplorer;
import io.github.railroad.project.explorer.ProjectExplorer.ExplorerTreeItem;
import io.github.railroad.project.settings.ThemeSettings;
import io.github.railroad.utility.RailroadScrollPane;
import io.github.railroad.utility.WindowTools;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class Railroad extends Application {

	public DiscordEventHandlers discordHandlers;

	public DiscordRichPresence discordRichPresense;
	private CodeEditor codeEditor;
	private final List<SimpleFileEditorController> fileControllers = new ArrayList<>();

	private Project project = null;

	private void createCodeArea(final TabPane tabPane, final RailroadScrollPane<RailroadCodeArea> scrollPane,
			final File file) {
		if (file != null) {
			if (tabPane.getParent() == null && scrollPane.getRealParent() instanceof SplitPane
					&& scrollPane.getRealParent().getParent() instanceof AnchorPane) {
				final var parent = (SplitPane) scrollPane.getRealParent();
				final var scrollPaneIndex = parent.getItems().indexOf(scrollPane);
				parent.getItems().remove(scrollPaneIndex);

				if (tabPane.getTabs().isEmpty()) {
					tabPane.getTabs().add(new Tab(file.getName(), scrollPane));
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
					tabPane.getTabs().add(new Tab(file.getName(), newScrollPane));
					newScrollPane.setRealParent(tabPane);
				}
				parent.getItems().add(scrollPaneIndex, tabPane);
			} else if (tabPane.getParent() != null && tabPane.getTabs().isEmpty()) {
				final var parent = (SplitPane) tabPane.getParent();
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
				tabPane.getTabs().add(new Tab(file.getName(), newScrollPane));
				newScrollPane.setRealParent(tabPane);

				final var controller = new SimpleFileEditorController();
				this.fileControllers.add(controller);
				controller.textArea = newCodeArea;
				controller.setFile(file);
			}
		}
	}

	private void setupDiscord() {
		this.discordHandlers = new DiscordEventHandlers.Builder()
				.setReadyEventHandler(user -> System.out.println(user.username + "#" + user.discriminator)).build();
		DiscordRPC.discordInitialize("853387211897700394", this.discordHandlers, true);
		DiscordRPC.discordRunCallbacks();
		this.discordRichPresense = new DiscordRichPresence.Builder("Working on Untitled Project")
				.setDetails("Making an amazing mod!").setBigImage("logo", "Railroad IDE")
				.setSmallImage("logo", "An IDE built for modders, made by modders.").setParty("", 0, 0)
				.setStartTimestamps(System.currentTimeMillis()).build();
		DiscordRPC.discordUpdatePresence(this.discordRichPresense);
	}

	// Test
	@Override
	public void start(final Stage primaryStage) throws Exception {
		this.project = new Project(new ThemeSettings(true));

		this.codeEditor = new CodeEditor(Executors.newSingleThreadExecutor());
		final Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

		this.fileControllers.add(new SimpleFileEditorController());
		final var borderPane = new BorderPane();

		final var codeAreaTabPane = new TabPane();
		final var codeArea = this.codeEditor.createCodeArea();
		this.fileControllers.get(0).textArea = codeArea;
		final var scrollPane = new RailroadScrollPane<>(codeArea);

		final var openItem = new MenuItem("Open");
		final var saveItem = new MenuItem("Save");
		final var fileMenu = new Menu("File", null, openItem, saveItem);
		final var menuBar = new MenuBar(fileMenu);
		borderPane.setTop(menuBar);

		final var projectExplorer = new ProjectExplorer().createProjectExplorer(this.project);
		projectExplorer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		final var splitPane = new SplitPane(projectExplorer, scrollPane);
		scrollPane.setRealParent(splitPane);
		splitPane.setDividerPosition(0, 0.15D);
		new Timeline(new KeyFrame(Duration.millis(10), event -> splitPane.setDividerPosition(0, 0.15D))).play();

		final var anchorPane = new AnchorPane(splitPane);
		AnchorPane.setBottomAnchor(splitPane, 5.0D);
		AnchorPane.setLeftAnchor(splitPane, 5.0D);
		AnchorPane.setRightAnchor(splitPane, 5.0D);
		AnchorPane.setTopAnchor(splitPane, 5.0D);
		borderPane.setCenter(anchorPane);

		final var statusMessage = new Label("Checking for Changes...");
		statusMessage.prefWidth(150);
		statusMessage.setId("statusMessage");
		// this.fileControllers.get(0).statusMessage = statusMessage;

		final var progressBar = new ProgressBar(0);
		progressBar.prefWidth(150);
		progressBar.setId("progressBar");
		// this.fileControllers.get(0).progressBar = progressBar;

		final var hboxLeft = new HBox(statusMessage, progressBar);
		hboxLeft.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(hboxLeft, Priority.ALWAYS);

		final var loadChangesButton = new Button("Load Changes");
		loadChangesButton.setId("loadChangesButton");
		loadChangesButton.setOnAction(event -> {
			if (this.fileControllers.isEmpty()) {
				loadChangesButton.setDisable(true);
				return;
			}

			SimpleFileEditorController selectedFileController = null;
			if (codeAreaTabPane.getParent().getParent().equals(anchorPane)) {
				final Node possibleArea = codeAreaTabPane.getSelectionModel().getSelectedItem().getContent();
				final List<SimpleFileEditorController> controllers = this.fileControllers.stream()
						.filter(controller -> controller.textArea.equals(possibleArea)).collect(Collectors.toList());
				if (controllers.isEmpty()) {
					loadChangesButton.setDisable(true);
					return;
				}
				selectedFileController = controllers.get(0);
			}

			if (codeArea != null && codeArea.getParent() != null) {
				final List<SimpleFileEditorController> controllers = this.fileControllers.stream()
						.filter(controller -> controller.textArea.equals(codeArea)).collect(Collectors.toList());
				if (controllers.isEmpty()) {
					loadChangesButton.setDisable(true);
					return;
				}
				selectedFileController = controllers.get(0);
			}

			if (selectedFileController != null) {
				selectedFileController.loadChanges();
			}
		});
		// this.fileControllers.get(0).loadChangesButton = loadChangesButton;

		final var hboxRight = new HBox(loadChangesButton);
		hboxRight.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(hboxRight, Priority.ALWAYS);

		final var hbox = new HBox(hboxLeft, hboxRight);
		hbox.setPadding(new Insets(0, 5, 5, 5));
		borderPane.setBottom(hbox);

		openItem.setOnAction(event -> {
			final SimpleFileEditorController controller;
			if (!this.fileControllers.isEmpty()) {
				controller = this.fileControllers.get(this.fileControllers.size() - 1);
			} else {
				controller = new SimpleFileEditorController();
				this.fileControllers.add(controller);
			}
			createCodeArea(codeAreaTabPane, scrollPane, controller.openFile());
		});

		saveItem.setOnAction(event -> {
			if (this.fileControllers.isEmpty()) {
				saveItem.setDisable(true);
				return;
			}

			SimpleFileEditorController selectedFileController = null;
			if (codeAreaTabPane.getParent().getParent().equals(anchorPane)) {
				final var possibleArea = (RailroadCodeArea) codeAreaTabPane.getSelectionModel().getSelectedItem()
						.getContent();
				final List<SimpleFileEditorController> controllers = this.fileControllers.stream()
						.filter(controller -> controller.textArea.equals(possibleArea)).collect(Collectors.toList());
				if (controllers.isEmpty()) {
					saveItem.setDisable(true);
					return;
				}
				selectedFileController = controllers.get(0);
			}

			if (codeArea != null && codeArea.getParent() != null) {
				final List<SimpleFileEditorController> controllers = this.fileControllers.stream()
						.filter(controller -> controller.textArea.equals(codeArea)).collect(Collectors.toList());
				if (controllers.isEmpty()) {
					saveItem.setDisable(true);
					return;
				}
				selectedFileController = controllers.get(0);
			}

			if (selectedFileController != null) {
				selectedFileController.saveFile();
			}
		});

		projectExplorer.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				final List<TreeItem<String>> items = projectExplorer.getSelectionModel().getSelectedItems();
				for (final var item : items) {
					createCodeArea(codeAreaTabPane, scrollPane, new File(((ExplorerTreeItem) item).getFullName()));
				}
			}
		});

		final var scene = new Scene(borderPane);
		scene.getStylesheets().add(Railroad.class.getResource("/default.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("Railroad IDE");
		primaryStage.setWidth(primaryScreenBounds.getWidth());
		primaryStage.setHeight(primaryScreenBounds.getHeight());
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(event -> {
			event.consume();
			WindowTools.displayQuitWindow(primaryStage);
		});
	}

	@Override
	public void stop() {
		this.codeEditor.executor.shutdown();
		DiscordRPC.discordShutdown();
	}
}
