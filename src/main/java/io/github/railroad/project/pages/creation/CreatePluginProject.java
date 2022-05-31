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

public class CreatePluginProject extends Page {
    private static final Border SELECTION_OUTLINE = new Border(
        new BorderStroke(Color.CORNFLOWERBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));

    public final StackPane spigotImage, paperImage, bukkitImage, spongeImage, bungeecordImage, velocityImage;
    public final Label spigotText, paperText, bukkitText, spongeText, bungeecordText, velocityText;
    public final BorderPane spigotButton, paperButton, bukkitButton, spongeButton, bungeecordButton, velocityButton;
    public final StackPane mainPane;
    public final AtomicReference<BorderPane> currentlySelected = new AtomicReference<>();
    public final Region selection;
    public final HBox buttonHolder;
    
    public CreatePluginProject() {
        super(new StackPane());
        
        this.mainPane = (StackPane) getCore();
        this.spigotImage = new StackPane(new ImageView(
            new Image("https://avatars.githubusercontent.com/u/4350249?s=150", 150, 150, true, true, false)));
        this.paperImage = new StackPane(new ImageView(
            new Image("https://avatars.githubusercontent.com/u/7608950?s=150", 150, 150, true, true, false)));
        this.bukkitImage = new StackPane(new ImageView(
            new Image("https://avatars.githubusercontent.com/u/544609?s=150", 150, 150, true, true, false)));
        this.spongeImage = new StackPane(new ImageView(
            new Image("https://avatars.githubusercontent.com/u/8683473?s=150", 150, 150, true, true, false)));
        this.bungeecordImage = new StackPane(
            new ImageView(new Image("https://i.imgur.com/MAg2r2J.png", 150, 150, true, true, false)));
        this.velocityImage = new StackPane(new ImageView(new Image(
            "data:image/png;base64,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",
            150, 150, true, true, false)));

        this.spigotText = new Label("Spigot");
        this.spigotText.setTextFill(Color.LIGHTGRAY);
        this.spigotText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.spigotImage.getChildren().add(this.spigotText);
        StackPane.setAlignment(this.spigotText, Pos.TOP_CENTER);
        StackPane.setMargin(this.spigotText, InsetsFactory.top(80));

        this.paperText = new Label("Paper");
        this.paperText.setTextFill(Color.LIGHTGRAY);
        this.paperText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.paperImage.getChildren().add(this.paperText);
        StackPane.setAlignment(this.paperText, Pos.TOP_CENTER);
        StackPane.setMargin(this.paperText, InsetsFactory.top(80));
        
        this.bukkitText = new Label("Bukkit");
        this.bukkitText.setTextFill(Color.LIGHTGRAY);
        this.bukkitText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.bukkitImage.getChildren().add(this.bukkitText);
        StackPane.setAlignment(this.bukkitText, Pos.TOP_CENTER);
        StackPane.setMargin(this.bukkitText, InsetsFactory.top(80));
        
        this.spongeText = new Label("Sponge");
        this.spongeText.setTextFill(Color.LIGHTGRAY);
        this.spongeText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.spongeImage.getChildren().add(this.spongeText);
        StackPane.setAlignment(this.spongeText, Pos.TOP_CENTER);
        StackPane.setMargin(this.spongeText, InsetsFactory.top(80));

        this.bungeecordText = new Label("Bungeecord");
        this.bungeecordText.setTextFill(Color.LIGHTGRAY);
        this.bungeecordText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.bungeecordImage.getChildren().add(this.bungeecordText);
        StackPane.setAlignment(this.bungeecordText, Pos.TOP_CENTER);
        StackPane.setMargin(this.bungeecordText, InsetsFactory.top(80));

        this.velocityText = new Label("Velocity");
        this.velocityText.setTextFill(Color.LIGHTGRAY);
        this.velocityText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.velocityImage.getChildren().add(this.velocityText);
        StackPane.setAlignment(this.velocityText, Pos.TOP_CENTER);
        StackPane.setMargin(this.velocityText, InsetsFactory.top(80));

        this.spigotButton = new BorderPane(this.spigotImage);
        this.paperButton = new BorderPane(this.paperImage);
        this.bukkitButton = new BorderPane(this.bukkitImage);
        this.spongeButton = new BorderPane(this.spongeImage);
        this.bungeecordButton = new BorderPane(this.bungeecordImage);
        this.velocityButton = new BorderPane(this.velocityImage);

        this.selection = new Region();
        this.selection.setBorder(SELECTION_OUTLINE);

        this.spigotButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.spigotButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }

                this.selection.setTranslateX(
                    -(this.bukkitButton.getWidth() / 2 + this.paperButton.getWidth() + this.spigotButton.getWidth())
                        - 30);
                this.selection.setMaxWidth(this.spigotButton.getWidth());
                this.selection.setMaxHeight(this.spigotButton.getHeight());

                this.currentlySelected.set(this.spigotButton);
            }
        });

        this.paperButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.paperButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }

                this.selection.setTranslateX(-(this.bukkitButton.getWidth() / 2 + this.paperButton.getWidth()) - 24);
                this.selection.setMaxWidth(this.paperButton.getWidth());
                this.selection.setMaxHeight(this.paperButton.getHeight());

                this.currentlySelected.set(this.paperButton);
            }
        });
        
        this.bukkitButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.bukkitButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }

                this.selection.setTranslateX(-this.bukkitButton.getWidth() / 2 - 18);
                this.selection.setMaxWidth(this.bukkitButton.getWidth());
                this.selection.setMaxHeight(this.bukkitButton.getHeight());

                this.currentlySelected.set(this.bukkitButton);
            }
        });
        
        this.spongeButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.spongeButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }

                this.selection.setTranslateX(this.spongeButton.getWidth() / 2 - 12);
                this.selection.setMaxWidth(this.spongeButton.getWidth());
                this.selection.setMaxHeight(this.spongeButton.getHeight());

                this.currentlySelected.set(this.spongeButton);
            }
        });

        this.bungeecordButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.bungeecordButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }

                this.selection.setTranslateX(this.spongeButton.getWidth() / 2 + this.bungeecordButton.getWidth() - 22);
                this.selection.setMaxWidth(this.bungeecordButton.getWidth());
                this.selection.setMaxHeight(this.bungeecordButton.getHeight());

                this.currentlySelected.set(this.bungeecordButton);
            }
        });

        this.velocityButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.velocityButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }

                this.selection.setTranslateX((this.spongeButton.getWidth() + 6) / 2 + this.bungeecordButton.getWidth()
                    + this.velocityButton.getWidth() - 3);
                this.selection.setMaxWidth(this.velocityButton.getWidth());
                this.selection.setMaxHeight(this.velocityButton.getHeight());

                this.currentlySelected.set(this.velocityButton);
            }
        });

        this.buttonHolder = new HBox(this.spigotButton, new Separator(Orientation.VERTICAL), this.paperButton,
            new Separator(Orientation.VERTICAL), this.bukkitButton, new Separator(Orientation.VERTICAL),
            this.spongeButton, new Separator(Orientation.VERTICAL), this.bungeecordButton,
            new Separator(Orientation.VERTICAL), this.velocityButton);
        HBox.setHgrow(this.spigotButton, Priority.ALWAYS);
        HBox.setHgrow(this.paperButton, Priority.ALWAYS);
        HBox.setHgrow(this.bukkitButton, Priority.ALWAYS);
        HBox.setHgrow(this.spongeButton, Priority.ALWAYS);
        HBox.setHgrow(this.bungeecordButton, Priority.ALWAYS);
        HBox.setHgrow(this.velocityButton, Priority.ALWAYS);
        this.buttonHolder.setStyle("-fx-background-color: #1B232C;");
        this.buttonHolder.setPickOnBounds(true);
        this.buttonHolder.setAlignment(Pos.CENTER);

        this.mainPane.getChildren().add(this.buttonHolder);
    }
}
