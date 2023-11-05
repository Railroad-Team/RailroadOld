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

public class ActionSelection extends Page {
    private static final Border SELECTION_OUTLINE = new Border(
        new BorderStroke(Color.CORNFLOWERBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));
    
    public final StackPane mainStack;
    public final StackPane openImage, importImage, createImage;
    public final Label openText, importText, createText;
    public final BorderPane openButton, importButton, createButton;
    public final AtomicReference<BorderPane> currentlySelected = new AtomicReference<>();
    public final Region selection;
    public final HBox buttonHolder;

    public ActionSelection() {
        super(new StackPane());
        
        this.mainStack = (StackPane) getCore();

        this.openImage = new StackPane(new ImageView(new Image(
            "https://static.wikia.nocookie.net/minecraft_gamepedia/images/c/ca/Open_Barrel_%28U%29_JE1_BE1.png", 300,
            300, true, true, false)));
        this.importImage = new StackPane(new ImageView(
            new Image("https://static.wikia.nocookie.net/minecraft_gamepedia/images/e/e2/Hopper_%28D%29_JE8.png", 300,
                300, true, true, false)));
        this.createImage = new StackPane(new ImageView(
            new Image("https://static.wikia.nocookie.net/minecraft_gamepedia/images/b/b7/Crafting_Table_JE4_BE3.png",
                300, 300, true, true, false)));

        this.openText = new Label("Open");
        this.openText.setTextFill(Color.LIGHTGRAY);
        this.openText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.openImage.getChildren().add(this.openText);
        StackPane.setAlignment(this.openText, Pos.TOP_CENTER);
        StackPane.setMargin(this.openText, InsetsFactory.top(80));

        this.importText = new Label("Import");
        this.importText.setTextFill(Color.LIGHTGRAY);
        this.importText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.importImage.getChildren().add(this.importText);
        StackPane.setAlignment(this.importText, Pos.TOP_CENTER);
        StackPane.setMargin(this.importText, InsetsFactory.top(80));

        this.createText = new Label("Create");
        this.createText.setTextFill(Color.LIGHTGRAY);
        this.createText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.createImage.getChildren().add(this.createText);
        StackPane.setAlignment(this.createText, Pos.TOP_CENTER);
        StackPane.setMargin(this.createText, InsetsFactory.top(80));

        this.openButton = new BorderPane(this.openImage);
        this.importButton = new BorderPane(this.importImage);
        this.createButton = new BorderPane(this.createImage);
        
        this.selection = new Region();
        this.selection.setBorder(SELECTION_OUTLINE);

        this.openButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.openButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainStack.getChildren().add(this.selection);
                }

                this.selection.setTranslateX(-(this.importButton.getWidth() + 6));
                this.selection.setMaxWidth(this.openButton.getWidth());
                this.selection.setMaxHeight(this.openButton.getHeight());
                
                this.currentlySelected.set(this.openButton);
            }
        });

        this.importButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.importButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainStack.getChildren().add(this.selection);
                }

                this.selection.setTranslateX(0);
                this.selection.setMaxWidth(this.importButton.getWidth());
                this.selection.setMaxHeight(this.importButton.getHeight());
                
                this.currentlySelected.set(this.importButton);
            }
        });

        this.createButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.createButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainStack.getChildren().add(this.selection);
                }

                this.selection.setTranslateX(this.importButton.getWidth() + 6);
                this.selection.setMaxWidth(this.createButton.getWidth());
                this.selection.setMaxHeight(this.createButton.getHeight());
                
                this.currentlySelected.set(this.createButton);
            }
        });

        this.buttonHolder = new HBox(this.openButton, new Separator(Orientation.VERTICAL), this.importButton,
            new Separator(Orientation.VERTICAL), this.createButton);
        HBox.setHgrow(this.openButton, Priority.ALWAYS);
        HBox.setHgrow(this.importButton, Priority.ALWAYS);
        HBox.setHgrow(this.createButton, Priority.ALWAYS);
        this.buttonHolder.setStyle("-fx-background-color: #1B232C;");
        this.buttonHolder.setPickOnBounds(true);
        this.buttonHolder.setAlignment(Pos.CENTER);
        
        this.mainStack.getChildren().add(this.buttonHolder);
    }
}
