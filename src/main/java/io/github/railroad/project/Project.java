package io.github.railroad.project;

import static io.github.railroad.project.lang.LangProvider.fromLang;

import java.io.File;
import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;

import com.sun.javafx.tk.Toolkit;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.BindingUtils;
import io.github.railroad.Railroad;
import io.github.railroad.project.lang.LangProvider;
import io.github.railroad.project.settings.theme.Theme;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

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

        final var icon0 = new FontIcon("mdi2f-folder-home");
        icon0.setIconSize(32);
        icon0.setIconColor(Color.web("#1fb0b5"));

        final var dirChooser = new DirectoryChooser();
        dirChooser.setTitle(LangProvider.fromLang("project.chooseFolder"));
        dirChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Documents"));

        final var directoryField = new MFXTextField(System.getProperty("user.home") + "/Documents");
        directoryField.autosize();
        directoryField.deselect();
        new Timeline(new KeyFrame(Duration.millis(10), event -> directoryField.deselect())).play();

        final var browseButton = new MFXButton(fromLang("project.browseFolders"));
        browseButton.setTextFill(Color.BLACK);
        browseButton.setOnAction(event -> {
            final File chosenDir = dirChooser.showDialog(null);
            if (chosenDir != null) {
                directoryField.setText(chosenDir.getPath());
            }
        });

        final var horizontal = new HBox(directoryField, browseButton);
        horizontal.setAlignment(Pos.CENTER);
        horizontal.setSpacing(20f);
        final var content0 = new VBox(horizontal);
        content0.setAlignment(Pos.CENTER);
        final var toggle0 = new MFXStepperToggle("Home", icon0, content0);
        directoryField.setOnInputMethodTextChanged(
                event -> toggle0.getValidator().add(BindingUtils.toProperty(new BooleanBinding() {
                    @Override
                    protected boolean computeValue() {
                        return new File(directoryField.getText()).exists();
                    }
                }), "You must supply a valid directory!"));

        final var icon1 = new FontIcon("mdi2f-folder-cog");
        icon1.setIconSize(32);
        icon1.setIconColor(Color.web("#1fb0b5"));
        final var button0 = new MFXButton();
        final var button1 = new MFXButton();
        final var button2 = new MFXButton();
        final var content1 = new VBox(button0, button1, button2);
        content1.setAlignment(Pos.CENTER);
        final var toggle1 = new MFXStepperToggle("Configure", icon1, content1);

        final var stepper = new MFXStepper(List.of(toggle0, toggle1));
        final var scene = new Scene(stepper);
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
}
