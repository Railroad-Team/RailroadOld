package io.github.railroad.utility;

import static io.github.railroad.utility.helper.ColorHelper.awtColourToFx;

import io.github.railroad.Railroad;
import io.github.railroad.project.lang.LangProvider;
import io.github.railroad.project.settings.theme.Theme;
import io.github.railroad.utility.helper.ColorHelper;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
    public static void displayQuitWindow(final Stage windowToClose, final Theme theme) {
        final var stage = new Stage();
        final var label = new Label("  " + fromQuitWindowLang("confirm") + " ");
        final var yesBtn = new Button("   " + fromQuitWindowLang("yesBtn") + "  ");
        yesBtn.setOnAction(event -> {
            windowToClose.close();
            stage.close();
        });
        final var cancelBtn = new Button(fromQuitWindowLang("cancelBtn"));
        cancelBtn.setOnAction(event -> stage.close());

        final var logo = new Image(Railroad.class.getResourceAsStream("/logo.png"), 128, 128, false, false);
        final var logoView = new ImageView(logo);
        logoView.setTranslateY(-9);

        cancelBtn.setStyle("-fx-background-color: " + ColorHelper.toHex(theme.getButtonColor()));
        yesBtn.setStyle("-fx-background-color: " + ColorHelper.toHex(theme.getButtonColor()));

        cancelBtn.setPrefWidth(70);
        yesBtn.setPrefWidth(70);

        label.setTextFill(awtColourToFx(theme.getTextColor()));
        label.setFont(new Font("Arial", 15));
        label.setPadding(new Insets(10));

        final var btnsVbox = new VBox(20, yesBtn, new Separator(Orientation.HORIZONTAL), cancelBtn);
        btnsVbox.setTranslateY(5);

        final var hbox = new HBox(50, logoView, btnsVbox);

        btnsVbox.setLayoutY(100);

        hbox.setAlignment(Pos.CENTER);

        final var vbox = new VBox(10, label, new Separator(Orientation.HORIZONTAL), hbox);

        vbox.setBackground(new Background(new BackgroundFill(ColorHelper.awtColourToFx(theme.getBackgroundColor()),
            CornerRadii.EMPTY, Insets.EMPTY)));

        vbox.setAlignment(Pos.CENTER);

        final var scene = new Scene(vbox);

        stage.setScene(scene);

        stage.setResizable(false);
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
