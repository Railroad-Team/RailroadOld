package io.github.railroad.utility;

import io.github.railroad.project.lang.LangProvider;
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

    private WindowTools() {
        throw new IllegalAccessError("Attempted to construct utility class!");
    }

    /**
     * Creates the quit confirmation window.
     *
     * @param windowToClose the window to be closed if clicked "Yes".
     */
    public static void displayQuitWindow(final Stage windowToClose) {
        final var stage = new Stage();
        final var label = new Label(fromQuitWindowLang("label"));
        final var yesBtn = new Button(fromQuitWindowLang("yesBtn"));
        yesBtn.setOnAction(event -> {
            windowToClose.close();
            stage.close();
        });
        final var cancelBtn = new Button(fromQuitWindowLang("cancelBtn"));
        cancelBtn.setOnAction(event -> stage.close());
        final var vbox = new VBox(label, yesBtn, cancelBtn);
        vbox.setAlignment(Pos.CENTER);
        final var scene = new Scene(vbox);
        stage.setScene(scene);

        stage.sizeToScene();
        stage.centerOnScreen();
        stage.setTitle(fromQuitWindowLang("title"));
        stage.setAlwaysOnTop(true);
        stage.requestFocus();
        stage.showAndWait();
    }
    
    private static String fromQuitWindowLang(String key) {
    	return LangProvider.fromLang("quitWindow." + key);
    }
}
