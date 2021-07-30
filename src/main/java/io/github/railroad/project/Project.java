package io.github.railroad.project;

import java.io.File;

import io.github.railroad.Railroad;
import io.github.railroad.project.settings.ThemeSettings;
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
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Project {

	private final ThemeSettings theme;

	private File projectFolder;

	public Project(final ThemeSettings themeSettings) {
		this.theme = themeSettings;

		final var window = new Stage(StageStyle.UNIFIED);

		final var dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Choose your project folder");
		dirChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));

		final var textField = new TextField(System.getProperty("user.home") + "\\Desktop");
		textField.autosize();
		textField.deselect();

		final var selectButton = new Button("Select");
		selectButton.setOnAction(event -> {
			this.projectFolder = new File(textField.getText());
			window.close();
		});

		final var cancelButton = new Button("Cancel");
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

		final var browseButton = new Button("Browse...");
		browseButton.setOnAction(event -> {
			final File chosenDir = dirChooser.showDialog(null);
			if (chosenDir != null) {
				textField.setText(chosenDir.getPath());
			}
		});

		final var rootDirLabel = new Label("Root Directory:");
		final var descriptionLabel = new Label("Specify the root directory of the mod that you want to work on!");
		final var titleLabel = new Label("Select your project folder");
		titleLabel.setId("projectTitleLabel");
		descriptionLabel.setId("projectDescriptionLabel");
		rootDirLabel.setWrapText(true);
		descriptionLabel.setWrapText(true);
		titleLabel.setWrapText(true);

		final var buttonLayout = new HBox(cancelButton, selectButton);
		HBox.setMargin(cancelButton, new Insets(0, 5D, 0, 0));
		buttonLayout.setAlignment(Pos.CENTER_RIGHT);

		final var layout = new VBox(titleLabel, descriptionLabel, new HBox(rootDirLabel, textField, browseButton),
				buttonLayout);
		final var scene = new Scene(layout);
		scene.getStylesheets().add(Railroad.class.getResource("/default.css").toExternalForm());

		window.setOnCloseRequest(event -> {
			window.close();
			System.exit(0);
		});
		window.setResizable(false);
		window.setScene(scene);
		window.setTitle("Choose Project");
		window.requestFocus();
		window.setAlwaysOnTop(true);
		window.showAndWait();
	}

	public File getProjectFolder() {
		return this.projectFolder;
	}

	public ThemeSettings getTheme() {
		return this.theme;
	}
}
