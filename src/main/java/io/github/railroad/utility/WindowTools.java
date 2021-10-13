package io.github.railroad.utility;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author TurtyWurty
 */
public final class WindowTools {

	/**
	 * Creates the quit confirmation window.
	 *
	 * @param windowToClose the window to be closed if clicked "Yes".
	 */
	public static void displayQuitWindow(final Stage windowToClose) {
		final var stage = new Stage();
		final var label = new Label("Are you sure you would like to quit Railroad IDE?");
		final var yesBtn = new Button("Yes");
		yesBtn.setOnAction(event -> {
			windowToClose.close();
			stage.close();
		});
		final var cancelBtn = new Button("Cancel");
		cancelBtn.setOnAction(event -> stage.close());
		final var vbox = new VBox(label, yesBtn, cancelBtn);
		vbox.setAlignment(Pos.CENTER);
		final var scene = new Scene(vbox);
		stage.setScene(scene);

		stage.sizeToScene();
		stage.centerOnScreen();
		stage.setTitle("Quit Railroad IDE");
		stage.setAlwaysOnTop(true);
		stage.requestFocus();
		stage.showAndWait();
	}

	private WindowTools() {
		throw new IllegalAccessError("Attempted to construct utility class!");
	}
}
