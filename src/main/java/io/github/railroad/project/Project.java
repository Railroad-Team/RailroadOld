package io.github.railroad.project;

import static io.github.railroad.project.lang.LangProvider.fromLang;

import java.io.File;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.font.FontResources;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.railroad.Railroad;
import io.github.railroad.project.pages.ActionSelection;
import io.github.railroad.project.pages.CreateProject;
import io.github.railroad.project.pages.ImportProject;
import io.github.railroad.project.pages.OpenProject;
import io.github.railroad.project.pages.creation.CreateModProject;
import io.github.railroad.project.pages.creation.CreatePluginProject;
import io.github.railroad.project.pages.creation.mod.ForgeModProject;
import io.github.railroad.project.settings.theme.Theme;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author TurtyWurty
 */
public class Project {
    /**
     * How to use the config before showing the folder selector add this if
     * (JsonConfigs.GENERAL_CONFIG.getConfig() != null &&
     * JsonConfigs.GENERAL_CONFIG.getConfig().getProjectSettings().projectPath !=
     * null) { this.projectFolder = new
     * File(JsonConfigs.GENERAL_CONFIG.getConfig().getProjectSettings().projectPath);
     * } else { implement the selector here. in order to save the path to the
     * config.. execute this code: JsonConfigs.GENERAL_CONFIG.writeConfig(new
     * RailroadConfigJson(new
     * ProjectSettingsEntry(this.projectFolder.getPath().toString())); } i dont see
     * a reason to format this. i just threw it here so you know how to use the
     * <b>temporary</b> solution
     */

    private final Theme theme;
    private File projectFolder;

    private final ActionSelection actionSelection = new ActionSelection();
    private final OpenProject openProject = new OpenProject();
    private final ImportProject importProject = new ImportProject();
    private final CreateProject createProject = new CreateProject();
    private final CreateModProject createModProject = new CreateModProject();
    private final CreatePluginProject createPluginProject = new CreatePluginProject();
    private final ForgeModProject.Page1 forgeModProject1 = new ForgeModProject.Page1();
    private final ForgeModProject.Page2 forgeModProject2 = new ForgeModProject.Page2(forgeModProject1);
    private final ForgeModProject.Page3 forgeModProject3 = new ForgeModProject.Page3(forgeModProject2);

    public Project(final Theme themeSettings) {
        this.theme = themeSettings;

        final var window = new Stage();

        final var title = new Label("Action Selection");
        title.setTextFill(Color.LIGHTSLATEGREY);
        title.setStyle("-fx-font-weight: 600; -fx-font-size: 16;");
        title.setTextAlignment(TextAlignment.CENTER);

        final var topMenu = new HBox(20, title);
        topMenu.setAlignment(Pos.CENTER);
        topMenu.setStyle("-fx-background-color: #0B151F;");
        topMenu.setPrefHeight(30);

        final var separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);

        final var topStack = new StackPane(topMenu);
        final var continueButton = new MFXButton("",
                new MFXFontIcon(FontResources.ARROW_FORWARD.getDescription(), 25, Color.CRIMSON));
        continueButton.setStyle("-fx-background-color: transparent;");
        final var previousButton = new MFXButton("",
                new MFXFontIcon(FontResources.ARROW_BACK.getDescription(), 25, Color.CRIMSON));
        previousButton.setStyle("-fx-background-color: transparent;");

        topStack.getChildren().add(continueButton);
        StackPane.setAlignment(continueButton, Pos.CENTER_RIGHT);

        final var top = new VBox(topStack, separator);
        top.setPickOnBounds(false);

        final var layout = new BorderPane(this.actionSelection.getCore());
        layout.setTop(top);
        layout.setPickOnBounds(false);

        continueButton.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                if (layout.getCenter() == this.actionSelection.getCore() && this.actionSelection.currentlySelected.get() != null) {
                    final BorderPane selected = this.actionSelection.currentlySelected.get();
                    if (selected == this.actionSelection.openButton) {
                        layout.setCenter(this.openProject.getCore());
                        title.setText("Open Project");
                    } else if (selected == this.actionSelection.importButton) {
                        layout.setCenter(this.importProject.getCore());
                        title.setText("Import Project");
                    } else if (selected == this.actionSelection.createButton) {
                        layout.setCenter(this.createProject.getCore());
                        title.setText("Create Project");
                    }

                    topStack.getChildren().add(previousButton);
                    StackPane.setAlignment(previousButton, Pos.CENTER_LEFT);
                } else if (layout.getCenter() == this.createProject.getCore() && this.createProject.currentlySelected.get() != null) {
                    final BorderPane selected = this.createProject.currentlySelected.get();
                    if (selected == this.createProject.modButton) {
                        layout.setCenter(this.createModProject.getCore());
                        title.setText("Create Mod");
                    } else if (selected == this.createProject.pluginButton) {
                        layout.setCenter(this.createPluginProject.getCore());
                        title.setText("Create Plugin");
                    }
                } else if (layout.getCenter() == this.createModProject.getCore() && this.createModProject.currentlySelected.get() != null) {
                    final BorderPane selected = this.createModProject.currentlySelected.get();
                    if (selected == this.createModProject.forgeButton) {
                        layout.setCenter(this.forgeModProject1.getCore());
                        title.setText("Create Forge Mod");
                    }
                } else if (layout.getCenter() == this.forgeModProject1.getCore() && this.forgeModProject1.isComplete()) {
                    layout.setCenter(this.forgeModProject2.getCore());
                } else if (layout.getCenter() == this.forgeModProject2.getCore() && this.forgeModProject2.isComplete()) {
                    layout.setCenter(this.forgeModProject3.getCore());
                }
            }
        });

        previousButton.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                if (layout.getCenter() == this.openProject.getCore() || layout.getCenter() == this.importProject.getCore() || layout.getCenter() == this.createProject.getCore()) {
                    topStack.getChildren().remove(previousButton);
                    layout.setCenter(this.actionSelection.getCore());
                    title.setText("Action Selection");
                } else if (layout.getCenter() == this.actionSelection.getCore()) {
                    topStack.getChildren().remove(previousButton);
                } else if (layout.getCenter() == this.createModProject.getCore() || layout.getCenter() == this.createPluginProject.getCore()) {
                    layout.setCenter(this.createProject.getCore());
                    title.setText("Create Project");
                } else if (layout.getCenter() == this.forgeModProject1.getCore()) {
                    layout.setCenter(this.createModProject.getCore());
                    title.setText("Create Mod");
                } else if (layout.getCenter() == this.forgeModProject2.getCore()) {
                    layout.setCenter(this.forgeModProject1.getCore());
                }
            }
        });

        final var scene = new Scene(layout, 1200, 600);
        scene.getStylesheets().add(Railroad.class.getResource("/project.css").toExternalForm());

        window.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        window.setResizable(false);
        window.setScene(scene);
        window.setTitle(fromLang("project.selectWindowTitle"));
        window.requestFocus();
        window.initModality(Modality.APPLICATION_MODAL);
        window.centerOnScreen();
        window.showAndWait();
    }

    /**
     * @return The Folder used for this {@link Project}.
     */
    public File getProjectFolder() {
        return this.projectFolder;
    }

    public String getProjectName() {
        return getProjectFolder().getName();
    }

    /**
     * @return The {@link Theme} used for this {@link Project}.
     */
    public Theme getTheme() {
        return this.theme;
    }
}
