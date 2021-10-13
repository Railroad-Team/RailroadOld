package io.github.railroad;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.tuple.Triple;
import org.fxmisc.richtext.CodeArea;

import com.panemu.tiwulfx.control.dock.DetachableTab;
import com.panemu.tiwulfx.control.dock.DetachableTabPane;

import io.github.railroad.editor.CodeEditor;
import io.github.railroad.editor.SimpleFileEditorController;
import io.github.railroad.objects.ProjectExplorer;
import io.github.railroad.objects.ProjectExplorer.ExplorerTreeItem;
import io.github.railroad.objects.RailroadCodeArea;
import io.github.railroad.objects.RailroadMenuBar;
import io.github.railroad.objects.RailroadMenuBar.FileMenu;
import io.github.railroad.project.Project;
import io.github.railroad.project.settings.ThemeSettings;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
    protected final BorderPane mainPane;
    private final DetachableTabPane editorTabPane, leftTabPane;
    private RailroadCodeArea baseCodeArea;
    private final RailroadMenuBar menuBar;
    private final TreeView<String> projectExplorer;
    private final SplitPane mainSplitPane;
    private final AnchorPane anchorPane;
    private final Triple<Label, ProgressBar, Button> fileLoadItems;
    // Add this back if I find it is needed anywhere
    // private final HBox fileLoadPlacement;

    private final Project project;

    public Setup(final boolean darkMode) {
        // Core
        this.primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        this.project = new Project(new ThemeSettings(darkMode));

        // Code Editor
        this.codeEditor = new CodeEditor(Executors.newSingleThreadExecutor());
        this.editorTabPane = new DetachableTabPane();
        this.baseController = new SimpleFileEditorController();
        this.baseCodeArea = this.codeEditor.createCodeArea();
        this.baseController.textArea = this.baseCodeArea;
        this.fileLoadItems = createFileLoad();

        // Project Explorer
        this.projectExplorer = createProjectExplorer();
        this.leftTabPane = createLeftTabPane();

        // Primary
        this.mainPane = new BorderPane();
        this.menuBar = createTopMenu();
        this.mainSplitPane = createMainSplit();
        this.anchorPane = anchorMainSplit();
        /* this.fileLoadPlacement = */placeFileLoad();

        onMenuAction();
        onProjectExplorerAction();
    }

    private AnchorPane anchorMainSplit() {
        final var localAnchorPane = new AnchorPane(this.mainSplitPane);
        this.mainSplitPane.getProperties().put("RealParent", localAnchorPane);
        AnchorPane.setBottomAnchor(this.mainSplitPane, 5.0D);
        AnchorPane.setLeftAnchor(this.mainSplitPane, 5.0D);
        AnchorPane.setRightAnchor(this.mainSplitPane, 5.0D);
        AnchorPane.setTopAnchor(this.mainSplitPane, 5.0D);
        this.mainPane.setCenter(localAnchorPane);
        return localAnchorPane;
    }

    private void createCodeArea(final DetachableTabPane tabPane, final RailroadCodeArea codeArea,
            final File file) {
        if (file != null) {
            if (tabPane.getProperties().get("RealParent") == null
                    && codeArea.getProperties().get("RealParent")instanceof final SplitPane parent
                    && parent.getProperties().get("RealParent") instanceof AnchorPane) {
                final var codeAreaIndex = parent.getItems().indexOf(codeArea);
                parent.getItems().remove(codeAreaIndex);

                if (tabPane.getTabs().isEmpty()) {
                    final var newTab = new DetachableTab(file.getName(), codeArea);
                    tabPane.getTabs().add(newTab);
                    onTabAction(newTab);
                    codeArea.getProperties().put("RealParent", tabPane);

                    if (this.fileControllers.isEmpty()) {
                        this.fileControllers.add(new SimpleFileEditorController());
                    }

                    this.fileControllers.get(0).textArea = codeArea;
                    this.fileControllers.get(0).setFile(file);
                } else {
                    final var controller = new SimpleFileEditorController();
                    this.fileControllers.add(controller);

                    final var newCodeArea = this.codeEditor.createCodeArea();
                    controller.textArea = newCodeArea;
                    controller.setFile(file);

                    final var newTab = new DetachableTab(file.getName(), newCodeArea);
                    tabPane.getTabs().add(newTab);
                    onTabAction(newTab);
                    newCodeArea.getProperties().put("RealParent", tabPane);
                }
                parent.getItems().add(codeAreaIndex, tabPane);
                tabPane.getProperties().put("RealParent", parent);
            } else if (tabPane.getParent() != null && tabPane.getTabs().isEmpty()) {
                final var parent = (SplitPane) tabPane.getParent();
                final var tabPaneIndex = parent.getItems().indexOf(tabPane);
                parent.getItems().remove(tabPane);
                tabPane.getProperties().put("RealParent", null);
                parent.getItems().add(tabPaneIndex, codeArea);
                codeArea.getProperties().put("RealParent", parent);

                if (this.fileControllers.isEmpty()) {
                    this.fileControllers.add(new SimpleFileEditorController());
                }

                this.fileControllers.get(0).textArea = codeArea;
                this.fileControllers.get(0).setFile(file);
            } else if (!tabPane.getTabs().isEmpty()) {
                final var newCodeArea = this.codeEditor.createCodeArea();
                final var newTab = new DetachableTab(file.getName());
                newTab.setContent(newCodeArea);
                tabPane.getTabs().add(newTab);
                onTabAction(newTab);
                newCodeArea.getProperties().put("RealParent", tabPane);

                final var controller = new SimpleFileEditorController();
                this.fileControllers.add(controller);
                controller.textArea = newCodeArea;
                controller.setFile(file);
            }
        } else if (this.editorTabPane.getTabs().isEmpty()) {
            if (this.mainSplitPane.getItems().size() > 1) {
                this.mainSplitPane.getItems().remove(1);
            }

            this.editorTabPane.getProperties().put("RealParent", null);
            this.baseCodeArea = this.codeEditor.createCodeArea();
            this.mainSplitPane.getItems().add(this.baseCodeArea);
            this.baseCodeArea.getProperties().put("RealParent", this.mainSplitPane);
        }

        // TODO: Use this. But fix the tab pane and scroll bar issues that occur
        // dividerAdjust();
    }

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
            if (((Node) this.editorTabPane.getProperties().get("RealParent")).getProperties()
                    .get("RealParent").equals(this.anchorPane)) {
                final Node possibleArea = this.editorTabPane.getSelectionModel().getSelectedItem()
                        .getContent();
                final List<SimpleFileEditorController> controllers = this.fileControllers.stream()
                        .filter(controller -> controller.textArea.equals(possibleArea)).toList();
                if (controllers.isEmpty()) {
                    loadChangesBtn.setDisable(true);
                    return;
                }
                selectedFileController = controllers.get(0);
            }

            if (this.baseCodeArea != null && this.baseCodeArea.getParent() != null) {
                final List<SimpleFileEditorController> controllers = this.fileControllers.stream()
                        .filter(controller -> controller.textArea.equals(this.baseCodeArea)).toList();
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

    private DetachableTabPane createLeftTabPane() {
        final var tabPane = new DetachableTabPane();
        final var projExplTab = new DetachableTab("Project Explorer", this.projectExplorer);
        projExplTab.setOnCloseRequest(Event::consume);
        tabPane.getTabs().add(projExplTab);
        return tabPane;
    }

    private SplitPane createMainSplit() {
        final var splitPane = new SplitPane(this.leftTabPane, this.baseCodeArea);
        this.leftTabPane.getProperties().put("RealParent", splitPane);
        this.baseCodeArea.getProperties().put("RealParent", splitPane);
        timedDividerAdjust(splitPane);
        return splitPane;
    }

    private TreeView<String> createProjectExplorer() {
        final var localProjectExplorer = new ProjectExplorer().createProjectExplorer(this.project);
        localProjectExplorer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return localProjectExplorer;
    }

    private RailroadMenuBar createTopMenu() {
        final var openItem = new MenuItem("Open");
        final var saveItem = new MenuItem("Save");
        final var fileMenu = new FileMenu(openItem, saveItem);
        final var localMenuBar = new RailroadMenuBar(fileMenu);
        this.mainPane.setTop(localMenuBar);
        return localMenuBar;
    }

    private void dividerAdjust() {
        dividerAdjust(this.mainSplitPane);
    }

    private void dividerAdjust(final SplitPane split) {
        new Timeline(new KeyFrame(Duration.millis(10), event -> split.setDividerPosition(0, 0.15D))).play();
    }

    private void onMenuAction() {
        this.menuBar.fileMenu.openItem.setOnAction(event -> {
            final SimpleFileEditorController controller;
            if (!this.fileControllers.isEmpty()) {
                controller = this.fileControllers.get(this.fileControllers.size() - 1);
            } else {
                controller = new SimpleFileEditorController();
                this.fileControllers.add(controller);
            }
            createCodeArea(this.editorTabPane, this.baseCodeArea, controller.openFile());
        });

        this.menuBar.fileMenu.saveItem.setOnAction(event -> {
            if (this.fileControllers.isEmpty()) {
                this.menuBar.fileMenu.saveItem.setDisable(true);
                return;
            }

            SimpleFileEditorController selectedFileController = null;
            if (this.editorTabPane.getProperties().get("RealParent") != null
                    && ((Node) this.editorTabPane.getProperties().get("RealParent")).getProperties()
                            .get("RealParent").equals(this.anchorPane)) {
                final var possibleArea = (RailroadCodeArea) this.editorTabPane.getSelectionModel()
                        .getSelectedItem().getContent();
                final List<SimpleFileEditorController> controllers = this.fileControllers.stream()
                        .filter(controller -> controller.textArea.equals(possibleArea)).toList();
                if (controllers.isEmpty()) {
                    this.menuBar.fileMenu.saveItem.setDisable(true);
                    return;
                }
                selectedFileController = controllers.get(0);
            }

            if (this.baseCodeArea != null && this.baseCodeArea.getParent() != null) {
                final List<SimpleFileEditorController> controllers = this.fileControllers.stream()
                        .filter(controller -> controller.textArea.equals(this.baseCodeArea)).toList();
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

    private void onProjectExplorerAction() {
        this.projectExplorer.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                final List<TreeItem<String>> items = this.projectExplorer.getSelectionModel()
                        .getSelectedItems();
                for (final var item : items) {
                    createCodeArea(this.editorTabPane, this.baseCodeArea,
                            new File(((ExplorerTreeItem) item).getFullName()));
                }
            }
        });
    }

    private void onTabAction(final DetachableTab tab) {
        tab.setOnClosed(event -> createCodeArea(this.editorTabPane, this.baseCodeArea, null));
    }

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

    private void timedDividerAdjust() {
        timedDividerAdjust(this.mainSplitPane);
    }

    private void timedDividerAdjust(final SplitPane split) {
        split.setDividerPosition(0, 0.15D);
        new Timeline(new KeyFrame(Duration.millis(10), event -> split.setDividerPosition(0, 0.15D))).play();
    }
}
