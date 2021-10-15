package io.github.railroad.project;

import java.io.File;

import com.sun.javafx.tk.Toolkit;

import io.github.railroad.Railroad;
import io.github.railroad.project.lang.LangProvider;
import io.github.railroad.project.settings.theme.Theme;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import static io.github.railroad.project.lang.LangProvider.fromLang;

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

        final var window = new Stage();

        final var dirChooser = new DirectoryChooser();
        dirChooser.setTitle(LangProvider.fromLang("project.chooseFolder"));
        dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        final var textField = new TextField(System.getProperty("user.home"));
        textField.autosize();
        textField.deselect();

        final var selectButton = new Button(fromLang("project.selectFolder"));
        selectButton.setOnAction(event -> {
            this.projectFolder = new File(textField.getText());
            window.close();
        });

        final var cancelButton = new Button(fromLang("project.cancelSelection"));
        cancelButton.setOnAction(event -> {
            window.close();
            System.exit(0);
        });
        cancelButton.setId("projectCancelButton");

        textField.setOnInputMethodTextChanged(event -> {
            final var text = textField.getText();
            selectButton.setDisable(!new File(text).exists());
        });
        textField.deselect();
        new Timeline(new KeyFrame(Duration.millis(10), event -> textField.deselect())).play();

        final var browseButton = new Button(fromLang("project.browseFolders"));
        browseButton.setOnAction(event -> {
            final File chosenDir = dirChooser.showDialog(null);
            if (chosenDir != null) {
                textField.setText(chosenDir.getPath());
            }
        });

        final var rootDirLabel = new Label(fromLang("project.rootDirectory"));
        final var descriptionLabel = new Label(fromLang("project.description"));
        final var titleLabel = new Label(fromLang("project.selectProjectFolder"));
        titleLabel.setId("projectTitleLabel");
        descriptionLabel.setId("projectDescriptionLabel");
        rootDirLabel.setWrapText(true);
        descriptionLabel.setWrapText(true);
        titleLabel.setWrapText(true);

        final var buttonLayout = new HBox(cancelButton, selectButton);
        HBox.setMargin(cancelButton, new Insets(0, 5D, 0, 0));
        buttonLayout.setAlignment(Pos.CENTER_RIGHT);

        final var layout = new VBox(titleLabel, descriptionLabel,
                new HBox(rootDirLabel, textField, browseButton), buttonLayout);
        final var scene = new Scene(layout);
        scene.getStylesheets().add(Railroad.class.getResource("/default.css").toExternalForm());

        window.setOnCloseRequest(event -> {
            Toolkit.getToolkit().exitAllNestedEventLoops();
            window.close();
            System.exit(0);
        });
        window.setResizable(false);
        window.setScene(scene);
        window.setTitle(fromLang("project.selectWindowTitle"));
        window.requestFocus();
        window.setAlwaysOnTop(true);
        window.showAndWait();
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
