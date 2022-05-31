package io.github.railroad.project.pages;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import com.github.dhiraj072.randomwordgenerator.RandomWordGenerator;
import com.github.dhiraj072.randomwordgenerator.datamuse.DataMuseRequest;
import com.github.dhiraj072.randomwordgenerator.exceptions.DataMuseException;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

@SuppressWarnings("deprecation")
public class OpenProject extends Page {
    public final BorderPane mainPane;
    public final ListView<Label> recent;
    public final HBox centerSplit;
    public final BorderPane leftContent, rightContent;
    public final VBox leftVBox, rightVertiPanel;
    public final HBox rightHorizPanel;
    public final Label directoryLabel;
    public final MFXTextField directoryField;
    public final MFXButton browseButton;
    public final DirectoryChooser dirChooser;
    public final MFXRadioButton leftRadio, rightRadio;
    public final ToggleGroup toggles;
    public final Label validationLabel;
    
    public OpenProject() {
        super(new BorderPane());
        
        final boolean leftEnabled = true;
        
        this.leftRadio = new MFXRadioButton();
        this.rightRadio = new MFXRadioButton();
        this.toggles = new ToggleGroup();
        this.toggles.getToggles().addAll(this.leftRadio, this.rightRadio);
        this.toggles.selectToggle(this.leftRadio);
        
        this.mainPane = (BorderPane) getCore();
        this.recent = new ListView<>();
        this.recent.setId("recentlyOpened");
        this.recent.setMaxSize(300, 450);
        // testPopulation();
        
        final var selectButton = new MFXButton("Select");
        this.leftVBox = new VBox(10, this.recent, selectButton);
        this.leftVBox.setAlignment(Pos.CENTER);
        
        this.directoryLabel = new Label("Select the Starting Folder");
        this.directoryLabel.setTextFill(Color.WHITESMOKE);
        this.directoryLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16pt;");
        
        this.directoryField = new MFXTextField("", System.getProperty("user.home"));
        this.directoryField.setMinWidth(250);
        this.directoryField.setMinHeight(30);
        
        this.validationLabel = new Label("");
        this.validationLabel.setVisible(false);
        this.validationLabel.setTextFill(Color.web("#ef6e6b"));
        this.validationLabel.setTranslateY(25);
        
        final Constraint existsConstraint = Constraint.Builder.build().setSeverity(Severity.ERROR)
            .setMessage("Directory does not exist!")
            .setCondition(Bindings.createBooleanBinding(
                () -> new File(this.directoryField.textProperty().get()).exists(), this.directoryLabel.textProperty()))
            .get();

        this.directoryField.getValidator().constraint(existsConstraint);
        this.directoryField.getValidator().validProperty().addListener((observable, oldVal, newVal) -> {
            if (Boolean.TRUE.equals(newVal)) {
                this.validationLabel.setVisible(false);
                this.directoryField.pseudoClassStateChanged(PseudoClass.getPseudoClass("invalid"), false);
            }
        });
        
        this.directoryField.textProperty().addListener((observable, oldVal, newVal) -> {
            final List<Constraint> constraints = this.directoryField.validate();
            if (!constraints.isEmpty()) {
                System.out.println(new File(newVal).exists());
                this.directoryField.pseudoClassStateChanged(PseudoClass.getPseudoClass("invalid"), true);
                this.validationLabel.setText(constraints.get(0).getMessage());
                this.validationLabel.setVisible(true);
            }
        });
        
        this.dirChooser = new DirectoryChooser();
        this.dirChooser.setTitle("Pick a folder");
        this.dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        this.browseButton = new MFXButton("Browse...");
        this.browseButton.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                final File file = this.dirChooser.showDialog(this.browseButton.getScene().getWindow());
                if (file != null) {
                    this.directoryField.setText(file.getAbsolutePath());
                }
            }
        });
        
        this.rightHorizPanel = new HBox(10, new StackPane(this.directoryField, this.validationLabel),
            this.browseButton);
        this.rightHorizPanel.setAlignment(Pos.CENTER);
        
        this.rightVertiPanel = new VBox(10, this.directoryLabel, this.rightHorizPanel);
        this.rightVertiPanel.setAlignment(Pos.CENTER);
        
        this.leftContent = new BorderPane(this.leftVBox);
        this.leftContent.setMinWidth(594);
        this.rightContent = new BorderPane(this.rightVertiPanel);
        this.rightContent.setMinWidth(594);
        BorderPane.setAlignment(this.rightVertiPanel, Pos.CENTER);
        
        this.leftContent.setTop(this.leftRadio);
        this.rightContent.setTop(this.rightRadio);
        
        this.rightVertiPanel.setDisable(leftEnabled);
        this.toggles.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (oldVal.equals(this.leftRadio) && leftEnabled) {
                this.leftVBox.setDisable(true);
                this.rightVertiPanel.setDisable(false);
            } else if (oldVal.equals(this.rightRadio)) {
                this.leftVBox.setDisable(false);
                this.rightVertiPanel.setDisable(true);
            }
        });
        
        this.centerSplit = new HBox(this.leftContent, new Separator(Orientation.VERTICAL), this.rightContent);
        this.centerSplit.setAlignment(Pos.CENTER);
        this.mainPane.setCenter(this.centerSplit);
        
        this.mainPane.setStyle("-fx-background-color: #1B232C;");
    }
    
    private void testPopulation() {
        final var adjectiveRequest = new DataMuseRequest();
        adjectiveRequest.topics("Adjectives");
        final var nounRequest = new DataMuseRequest();
        nounRequest.topics("Nouns");
        
        for (int i = 0; i < 20; i++) {
            try {
                this.recent.getItems()
                    .add(new Label(WordUtils.capitalize(RandomWordGenerator.getRandomWord(adjectiveRequest))
                        + WordUtils.capitalize(RandomWordGenerator.getRandomWord(nounRequest))));
            } catch (final DataMuseException exception) {
                exception.printStackTrace();
            }
        }
    }
    
    public static String getRandomProjectName() {
        final var adjectiveRequest = new DataMuseRequest();
        adjectiveRequest.topics("Adjectives");
        final var nounRequest = new DataMuseRequest();
        nounRequest.topics("Nouns");
        
        try {
            return WordUtils.capitalize(RandomWordGenerator.getRandomWord(adjectiveRequest)) + " "
                + WordUtils.capitalize(RandomWordGenerator.getRandomWord(nounRequest));
        } catch (final DataMuseException exception) {
            exception.printStackTrace();
            return "";
        }
    }
}
