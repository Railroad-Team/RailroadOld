package io.github.railroad.project;

import static io.github.railroad.project.lang.LangProvider.fromLang;

import java.io.File;
import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;

import com.sun.javafx.tk.Toolkit;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.enums.StepperToggleState;
import io.github.palexdev.materialfx.utils.BindingUtils;
import io.github.railroad.Railroad;
import io.github.railroad.project.settings.theme.Theme;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author TurtyWurty
 */
public class Project {

    private final Theme theme;

    private File projectFolder;

    /**
     * Creates the Project Settings and displays the initial "Project Directory
     * Chooser".
     *
     * @param themeSettings - The {@link Theme} to apply.
     */
    public Project(final Theme themeSettings) {
        this.theme = themeSettings;
        createWindow();
    }

    public Stage createWindow() {
        final var window = new Stage();

        /*
         * final var icon0 = new FontIcon("mdi2f-folder-home"); icon0.setIconSize(32);
         * icon0.setIconColor(Color.web("#1fb0b5"));
         *
         * final var dirChooser = new DirectoryChooser();
         * dirChooser.setTitle(LangProvider.fromLang("project.chooseFolder"));
         * dirChooser.setInitialDirectory( new
         * File(System.getProperty("user.home").replace('\\', '/') + "/Documents"));
         *
         * final var directoryField = new MFXTextField(
         * System.getProperty("user.home").replace('\\', '/') + "/Documents");
         * directoryField.autosize(); directoryField.deselect(); new Timeline(new
         * KeyFrame(Duration.millis(10), event -> directoryField.deselect())).play();
         *
         * final var browseButton = new MFXButton(fromLang("project.browseFolders"));
         * browseButton.setTextFill(Color.BLACK); browseButton.setOnAction(event -> {
         * final File chosenDir = dirChooser.showDialog(null); if (chosenDir != null) {
         * directoryField.setText(chosenDir.getPath().replace('\\', '/')); } });
         *
         * final var horizontal = new HBox(directoryField, browseButton);
         * horizontal.setAlignment(Pos.CENTER); horizontal.setSpacing(20f); final var
         * content0 = new VBox(horizontal); content0.setAlignment(Pos.CENTER); final var
         * toggle0 = new MFXStepperToggle(fromLang("project.home"), icon0, content0);
         * directoryField.setOnInputMethodTextChanged( event ->
         * toggle0.getValidator().add(BindingUtils.toProperty(new BooleanBinding() {
         *
         * @Override protected boolean computeValue() { return new
         * File(directoryField.getText()).exists(); } }),
         * fromLang("project.validDirectory")));
         *
         * final var icon1 = new FontIcon("mdi2f-folder-cog"); icon1.setIconSize(32);
         * icon1.setIconColor(Color.web("#1fb0b5")); final var button0 = new
         * MFXButton(); final var button1 = new MFXButton(); final var button2 = new
         * MFXButton(); final var content1 = new VBox(button0, button1, button2);
         * content1.setAlignment(Pos.CENTER); final var toggle1 = new
         * MFXStepperToggle(fromLang("project.configure"), icon1, content1);
         *
         * final var stepper = new MFXStepper(List.of(toggle0, toggle1));
         */

        final var scene = new Scene(createStepper());
        scene.getStylesheets().add(Railroad.class.getResource("/default.css").toExternalForm());
        window.setScene(scene);
        window.setOnCloseRequest(event -> {
            Toolkit.getToolkit().exitAllNestedEventLoops();
            System.exit(0);
            window.close();
        });
        window.setResizable(false);
        window.setScene(scene);
        window.setTitle(fromLang("project.selectWindowTitle"));
        window.requestFocus();
        window.setAlwaysOnTop(true);
        window.showAndWait();
        return window;
    }

    /**
     * @return The Folder used for this {@link Project}.
     */
    public File getProjectFolder() {
        return this.projectFolder;
    }

    /**
     * @return The {@link Theme} used for this {@link Project}.
     */
    public Theme getTheme() {
        return this.theme;
    }

    private MFXStepper createStepper() {
        final var openCreateIcon = new FontIcon(MaterialDesignF.FOLDER_OPEN);
        openCreateIcon.setIconSize(32);
        openCreateIcon.setIconColor(Color.web("#64b5f6"));
        final var openCreateLabel = new Label(fromLang("project.openCreateLabel"));

        final var openCreateToggleGroup = new ToggleGroup();
        final var openRadioBtn = new MFXRadioButton(fromLang("project.openButton"));
        openRadioBtn.setToggleGroup(openCreateToggleGroup);
        final var createRadioBtn = new MFXRadioButton(fromLang("project.createButton"));
        createRadioBtn.setToggleGroup(openCreateToggleGroup);
        final var buttonHBox = new HBox(openRadioBtn, createRadioBtn);
        buttonHBox.setAlignment(Pos.CENTER);
        buttonHBox.setSpacing(10f);

        final var openCreateVBox = new VBox(openCreateLabel, buttonHBox);
        openCreateVBox.setAlignment(Pos.CENTER);
        openCreateVBox.setSpacing(20f);
        final var openCreateToggle = new MFXStepperToggle(fromLang("project.openCreateSection"),
                openCreateIcon, openCreateVBox);
        openCreateToggle.getValidator()
                .add(BindingUtils
                        .toProperty(openRadioBtn.selectedProperty().or(createRadioBtn.selectedProperty())),
                        fromLang("project.openCreateError"));

        final var configurationIcon = new FontIcon(MaterialDesignF.FOLDER_COG);
        openCreateIcon.setIconSize(32);
        openCreateIcon.setIconColor(Color.web("#64b5f6"));

        final var configurationVBox = new VBox();
        configurationVBox.setAlignment(Pos.CENTER);
        configurationVBox.setSpacing(10f);

        // Open
        final var openLabel = new Label(fromLang("project.openLabel"));
        final var openFolderText = new MFXTextField(
                System.getProperty("user.home").replace('\\', '/') + "/Documents");
        final var openFolderButton = new MFXButton(fromLang("project.openFolderButton"));
        final var openHBox = new HBox(openFolderText, openFolderButton);
        openHBox.setAlignment(Pos.CENTER);
        openHBox.setSpacing(20f);

        // Create

        final var configurationToggle = new MFXStepperToggle(fromLang("project.configurationSection"),
                configurationIcon, configurationVBox);
        configurationToggle.stateProperty().addListener(state -> {
            if (configurationToggle.getState() == StepperToggleState.SELECTED) {
                if (openRadioBtn.isSelected()) {
                    configurationVBox.getChildren().clear();
                    configurationVBox.getChildren().addAll(openLabel, openHBox);
                } else if (createRadioBtn.isSelected()) {
                    configurationVBox.getChildren().clear();
                    configurationVBox.getChildren().addAll();
                }
            }
        });

        openFolderText.setOnInputMethodTextChanged(
                event -> configurationToggle.getValidator().add(BindingUtils.toProperty(new BooleanBinding() {
                    @Override
                    protected boolean computeValue() {
                        return new File(openFolderText.getText()).exists();
                    }
                }), fromLang("project.validDirectory")));

        final var toggles = List.of(openCreateToggle, configurationToggle);
        return new MFXStepper(toggles);
    }
}
