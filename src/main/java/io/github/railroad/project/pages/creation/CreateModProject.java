package io.github.railroad.project.pages.creation;

import java.util.concurrent.atomic.AtomicReference;

import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.railroad.project.pages.Page;
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

public class CreateModProject extends Page {
    private static final Border SELECTION_OUTLINE = new Border(
        new BorderStroke(Color.CORNFLOWERBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));
    
    public final StackPane forgeImage, fabricImage, quiltImage, architecturyImage;
    public final Label forgeText, fabricText, quiltText, architecturyText;
    public final BorderPane forgeButton, fabricButton, quiltButton, architecturyButton;
    public final StackPane mainPane;
    public final AtomicReference<BorderPane> currentlySelected = new AtomicReference<>();
    public final Region selection;
    public final HBox buttonHolder;

    public CreateModProject() {
        super(new StackPane());

        this.mainPane = (StackPane) getCore();
        this.forgeImage = new StackPane(new ImageView(
            new Image("https://avatars0.githubusercontent.com/u/1390178?s=250", 250, 250, true, true, false)));
        this.fabricImage = new StackPane(new ImageView(
            new Image("https://avatars.githubusercontent.com/u/21025855?s=250", 250, 250, true, true, false)));
        this.quiltImage = new StackPane(new ImageView(
            new Image("https://avatars.githubusercontent.com/u/78571508?s=250", 250, 250, true, true, false)));
        this.architecturyImage = new StackPane(new ImageView(
            new Image("https://avatars.githubusercontent.com/u/74373305?s=250", 250, 250, true, true, false)));
        
        this.forgeText = new Label("Forge");
        this.forgeText.setTextFill(Color.LIGHTGRAY);
        this.forgeText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.forgeImage.getChildren().add(this.forgeText);
        StackPane.setAlignment(this.forgeText, Pos.TOP_CENTER);
        StackPane.setMargin(this.forgeText, InsetsFactory.top(80));
        
        this.fabricText = new Label("Fabric");
        this.fabricText.setTextFill(Color.LIGHTGRAY);
        this.fabricText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.fabricImage.getChildren().add(this.fabricText);
        StackPane.setAlignment(this.fabricText, Pos.TOP_CENTER);
        StackPane.setMargin(this.fabricText, InsetsFactory.top(80));

        this.quiltText = new Label("Quilt");
        this.quiltText.setTextFill(Color.LIGHTGRAY);
        this.quiltText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.quiltImage.getChildren().add(this.quiltText);
        StackPane.setAlignment(this.quiltText, Pos.TOP_CENTER);
        StackPane.setMargin(this.quiltText, InsetsFactory.top(80));

        this.architecturyText = new Label("Architectury");
        this.architecturyText.setTextFill(Color.LIGHTGRAY);
        this.architecturyText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.architecturyImage.getChildren().add(this.architecturyText);
        StackPane.setAlignment(this.architecturyText, Pos.TOP_CENTER);
        StackPane.setMargin(this.architecturyText, InsetsFactory.top(80));
        
        this.forgeButton = new BorderPane(this.forgeImage);
        this.fabricButton = new BorderPane(this.fabricImage);
        this.quiltButton = new BorderPane(this.quiltImage);
        this.architecturyButton = new BorderPane(this.architecturyImage);
        
        this.selection = new Region();
        this.selection.setBorder(SELECTION_OUTLINE);
        
        this.forgeButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.forgeButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }
                
                this.selection
                    .setTranslateX(-((this.forgeButton.getWidth() + 6) / 2 + this.fabricButton.getWidth() + 5));
                this.selection.setMaxWidth(this.forgeButton.getWidth());
                this.selection.setMaxHeight(this.forgeButton.getHeight());
                
                this.currentlySelected.set(this.forgeButton);
            }
        });
        
        this.fabricButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.fabricButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }
                
                this.selection.setTranslateX(-(this.fabricButton.getWidth() + 6) / 2 + 1);
                this.selection.setMaxWidth(this.fabricButton.getWidth());
                this.selection.setMaxHeight(this.fabricButton.getHeight());
                
                this.currentlySelected.set(this.fabricButton);
            }
        });

        this.quiltButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.quiltButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }
                
                this.selection.setTranslateX((this.quiltButton.getWidth() + 7) / 2);
                this.selection.setMaxWidth(this.quiltButton.getWidth());
                this.selection.setMaxHeight(this.quiltButton.getHeight());
                
                this.currentlySelected.set(this.quiltButton);
            }
        });

        this.architecturyButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.architecturyButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }
                
                this.selection
                    .setTranslateX((this.quiltButton.getWidth() + 6) / 2 + this.architecturyButton.getWidth() + 6);
                this.selection.setMaxWidth(this.architecturyButton.getWidth() + 1);
                this.selection.setMaxHeight(this.architecturyButton.getHeight());
                
                this.currentlySelected.set(this.architecturyButton);
            }
        });
        
        this.buttonHolder = new HBox(this.forgeButton, new Separator(Orientation.VERTICAL), this.fabricButton,
            new Separator(Orientation.VERTICAL), this.quiltButton, new Separator(Orientation.VERTICAL),
            this.architecturyButton);
        HBox.setHgrow(this.forgeButton, Priority.ALWAYS);
        HBox.setHgrow(this.fabricButton, Priority.ALWAYS);
        HBox.setHgrow(this.quiltButton, Priority.ALWAYS);
        HBox.setHgrow(this.architecturyButton, Priority.ALWAYS);
        this.buttonHolder.setStyle("-fx-background-color: #1B232C;");
        this.buttonHolder.setPickOnBounds(true);
        this.buttonHolder.setAlignment(Pos.CENTER);
        
        this.mainPane.getChildren().add(this.buttonHolder);
    }
}
