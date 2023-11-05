package io.github.railroad.project.pages;

import java.util.concurrent.atomic.AtomicReference;

import io.github.palexdev.materialfx.factories.InsetsFactory;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class CreateProject extends Page {
    private static final Border SELECTION_OUTLINE = new Border(
        new BorderStroke(Color.CORNFLOWERBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));

    public final StackPane modImage, pluginImage;
    public final Label modText, pluginText;
    public final BorderPane modButton, pluginButton;
    public final StackPane mainPane;
    public final AtomicReference<BorderPane> currentlySelected = new AtomicReference<>();
    public final Region selection;
    public final HBox buttonHolder;

    public CreateProject() {
        super(new StackPane());

        this.mainPane = (StackPane) getCore();
        this.modImage = new StackPane(new ImageView(
            new Image("https://static.wikia.nocookie.net/minecraft_gamepedia/images/9/93/Smithing_Table_JE2_BE2.png",
                300, 300, true, true, false)));
        this.pluginImage = new StackPane(new ImageView(new Image(
            "https://static.wikia.nocookie.net/minecraft_gamepedia/images/9/96/Repeating_Command_Block_JE2_BE1.png",
            300, 300, true, true, false)));

        this.modText = new Label("Mod");
        this.modText.setTextFill(Color.LIGHTGRAY);
        this.modText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.modImage.getChildren().add(this.modText);
        StackPane.setAlignment(this.modText, Pos.TOP_CENTER);
        StackPane.setMargin(this.modText, InsetsFactory.top(80));

        this.pluginText = new Label("Plugin");
        this.pluginText.setTextFill(Color.LIGHTGRAY);
        this.pluginText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.pluginImage.getChildren().add(this.pluginText);
        StackPane.setAlignment(this.pluginText, Pos.TOP_CENTER);
        StackPane.setMargin(this.pluginText, InsetsFactory.top(80));

        this.modButton = new BorderPane(this.modImage);
        this.pluginButton = new BorderPane(this.pluginImage);

        this.selection = new Region();
        this.selection.setBorder(SELECTION_OUTLINE);

        this.modButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.modButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }

                this.selection.setTranslateX(-(this.modButton.getWidth() / 2) - 4);
                this.selection.setMaxWidth(this.modButton.getWidth());
                this.selection.setMaxHeight(this.modButton.getHeight());

                this.currentlySelected.set(this.modButton);
            }
        });

        this.pluginButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.pluginButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }

                this.selection.setTranslateX(this.pluginButton.getWidth() / 2 + 2);
                this.selection.setMaxWidth(this.pluginButton.getWidth());
                this.selection.setMaxHeight(this.pluginButton.getHeight());

                this.currentlySelected.set(this.pluginButton);
            }
        });

        this.buttonHolder = new HBox(this.modButton, new Separator(Orientation.VERTICAL), this.pluginButton);
        HBox.setHgrow(this.modButton, Priority.ALWAYS);
        HBox.setHgrow(this.pluginButton, Priority.ALWAYS);
        this.buttonHolder.setStyle("-fx-background-color: #1B232C;");
        this.buttonHolder.setPickOnBounds(true);
        this.buttonHolder.setAlignment(Pos.CENTER);

        this.mainPane.getChildren().add(this.buttonHolder);
    }
}
