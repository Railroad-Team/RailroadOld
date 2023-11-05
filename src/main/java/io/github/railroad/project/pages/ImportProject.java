package io.github.railroad.project.pages;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;

import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import io.github.railroad.utility.BufferedImageTranscoder;
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

public class ImportProject extends Page {
    private static final Border SELECTION_OUTLINE = new Border(
        new BorderStroke(Color.CORNFLOWERBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));

    public final StackPane mainPane;
    public final StackPane eclipseImage, vscodeImage, intellijImage, gradleImage, githubImage;
    public final Label eclipseText, vscodeText, intellijText, gradleText, githubText;
    public final BorderPane eclipseButton, vscodeButton, intellijButton, gradleButton, githubButton;
    public final AtomicReference<BorderPane> currentlySelected = new AtomicReference<>();
    public final Region selection;
    public final HBox buttonHolder;
    
    public ImportProject() {
        super(new StackPane());
        
        this.mainPane = (StackPane) getCore();

        this.eclipseImage = new StackPane(loadSVG(
            "https://raw.githubusercontent.com/gilbarbara/logos/master/logos/eclipse-icon.svg", 200, 200, true, true));
        this.intellijImage = new StackPane(loadSVG(
            "https://raw.githubusercontent.com/gilbarbara/logos/master/logos/intellij-idea.svg", 200, 200, true, true));
        this.vscodeImage = new StackPane(
            loadSVG("https://raw.githubusercontent.com/gilbarbara/logos/master/logos/visual-studio-code.svg", 200, 200,
                true, true));
        this.gradleImage = new StackPane(loadSVG(
            "https://raw.githubusercontent.com/gilbarbara/logos/master/logos/gradle.svg", 200, 200, true, true));
        this.githubImage = new StackPane(loadSVG(
            "https://raw.githubusercontent.com/gilbarbara/logos/master/logos/github-icon.svg", 200, 200, true, true));
        
        this.eclipseText = new Label("Eclipse");
        this.eclipseText.setTextFill(Color.LIGHTGRAY);
        this.eclipseText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.eclipseImage.getChildren().add(this.eclipseText);
        StackPane.setAlignment(this.eclipseText, Pos.TOP_CENTER);
        StackPane.setMargin(this.eclipseText, InsetsFactory.top(80));

        this.intellijText = new Label("IntelliJ");
        this.intellijText.setTextFill(Color.LIGHTGRAY);
        this.intellijText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.intellijImage.getChildren().add(this.intellijText);
        StackPane.setAlignment(this.intellijText, Pos.TOP_CENTER);
        StackPane.setMargin(this.intellijText, InsetsFactory.top(80));
        
        this.vscodeText = new Label("VSCode");
        this.vscodeText.setTextFill(Color.LIGHTGRAY);
        this.vscodeText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.vscodeImage.getChildren().add(this.vscodeText);
        StackPane.setAlignment(this.vscodeText, Pos.TOP_CENTER);
        StackPane.setMargin(this.vscodeText, InsetsFactory.top(80));

        this.gradleText = new Label("Gradle");
        this.gradleText.setTextFill(Color.LIGHTGRAY);
        this.gradleText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.gradleImage.getChildren().add(this.gradleText);
        StackPane.setAlignment(this.gradleText, Pos.TOP_CENTER);
        StackPane.setMargin(this.gradleText, InsetsFactory.top(80));
        
        this.githubText = new Label("GitHub");
        this.githubText.setTextFill(Color.LIGHTGRAY);
        this.githubText.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
        this.githubImage.getChildren().add(this.githubText);
        StackPane.setAlignment(this.githubText, Pos.TOP_CENTER);
        StackPane.setMargin(this.githubText, InsetsFactory.top(80));
        
        this.eclipseButton = new BorderPane(this.eclipseImage);
        this.intellijButton = new BorderPane(this.intellijImage);
        this.vscodeButton = new BorderPane(this.vscodeImage);
        this.gradleButton = new BorderPane(this.gradleImage);
        this.githubButton = new BorderPane(this.githubImage);

        this.selection = new Region();
        this.selection.setBorder(SELECTION_OUTLINE);
        
        this.eclipseButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.eclipseButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }
                
                this.selection.setTranslateX(-(this.intellijButton.getWidth() + 6 + this.eclipseButton.getWidth() + 6));
                this.selection.setMaxWidth(this.eclipseButton.getWidth());
                this.selection.setMaxHeight(this.eclipseButton.getHeight());

                this.currentlySelected.set(this.eclipseButton);
            }
        });

        this.intellijButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.intellijButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }
                
                this.selection.setTranslateX(-(this.eclipseButton.getWidth() + 6));
                this.selection.setMaxWidth(this.intellijButton.getWidth());
                this.selection.setMaxHeight(this.intellijButton.getHeight());

                this.currentlySelected.set(this.intellijButton);
            }
        });
        
        this.vscodeButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.vscodeButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }
                
                this.selection.setTranslateX(0);
                this.selection.setMaxWidth(this.vscodeButton.getWidth());
                this.selection.setMaxHeight(this.vscodeButton.getHeight());

                this.currentlySelected.set(this.vscodeButton);
            }
        });
        
        this.gradleButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.gradleButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }
                
                this.selection.setTranslateX(this.vscodeButton.getWidth() + 6);
                this.selection.setMaxWidth(this.gradleButton.getWidth());
                this.selection.setMaxHeight(this.gradleButton.getHeight());

                this.currentlySelected.set(this.gradleButton);
            }
        });
        
        this.githubButton.setOnMouseClicked(event -> {
            if (this.currentlySelected.get() != this.githubButton) {
                if (this.currentlySelected.get() == null) {
                    this.mainPane.getChildren().add(this.selection);
                }
                
                this.selection.setTranslateX(this.vscodeButton.getWidth() + 6 + this.gradleButton.getWidth() + 6);
                this.selection.setMaxWidth(this.githubButton.getWidth());
                this.selection.setMaxHeight(this.githubButton.getHeight());

                this.currentlySelected.set(this.githubButton);
            }
        });
        
        this.buttonHolder = new HBox(this.eclipseButton, new Separator(Orientation.VERTICAL), this.intellijButton,
            new Separator(Orientation.VERTICAL), this.vscodeButton, new Separator(Orientation.VERTICAL),
            this.gradleButton, new Separator(Orientation.VERTICAL), this.githubButton);
        HBox.setHgrow(this.eclipseButton, Priority.ALWAYS);
        HBox.setHgrow(this.intellijButton, Priority.ALWAYS);
        HBox.setHgrow(this.vscodeButton, Priority.ALWAYS);
        HBox.setHgrow(this.gradleButton, Priority.ALWAYS);
        HBox.setHgrow(this.githubButton, Priority.ALWAYS);
        this.buttonHolder.setStyle("-fx-background-color: #1B232C;");
        this.buttonHolder.setPickOnBounds(true);
        this.buttonHolder.setAlignment(Pos.CENTER);

        this.mainPane.getChildren().add(this.buttonHolder);
    }
    
    public static ImageView loadSVG(String url, int width, int height, boolean preserveRatio, boolean smooth) {
        final var transcoder = new BufferedImageTranscoder();
        try {
            final var input = new TranscoderInput(url);
            transcoder.transcode(input, null);

            final Image img = SwingFXUtils.toFXImage(transcoder.getImage(), null);
            final var view = new ImageView(img);
            view.setPreserveRatio(preserveRatio);
            view.setSmooth(smooth);
            view.setFitWidth(width);
            view.setFitHeight(height);
            
            return view;
        } catch (final TranscoderException exception) {
            exception.printStackTrace();
            throw new IllegalStateException("Unable to load icons!", exception);
        }
    }
}
