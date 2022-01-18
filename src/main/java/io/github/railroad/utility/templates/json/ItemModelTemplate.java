package io.github.railroad.utility.templates.json;

import java.io.File;

import io.github.railroad.menu.json.item.JsonFileMenuItem;
import io.github.railroad.project.Project;
import io.github.railroad.project.lang.LangProvider;
import io.github.railroad.utility.helper.ColorHelper;
import io.github.railroad.utility.templates.json.model.item.ItemGeneratedModel;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

/**
 * @author matyrobbrt
 */
public class ItemModelTemplate extends JsonFileMenuItem {

    public final Project project;

    private ChoiceBox cb = new ChoiceBox<>();

    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private File selectedDirectory;
    private TextField pathArea = new TextField();
    private TextField fileName = new TextField();
    private Label fileNameLabel = new Label(" " + LangProvider.fromLang("textArea.fileName") + ": ");
    private Label modelType = new Label(" " + fromLang("modelTypeField"));
    private Label pathLabel = new Label(" " + LangProvider.fromLang("textArea.selectPath") + ": ");
    private VBox vbox = new VBox();

    public ItemModelTemplate(Project project) {
        super(LangProvider.fromLang("menuBar.json.itemModel"));
        this.project = project;
        setOnAction(this::executeClick);
    }

    private void clearCache() {
        this.cb = new ChoiceBox<>();
        this.vbox = new VBox();
        this.pathArea.setText("");
        this.fileName.setText("");
        this.directoryChooser.setInitialDirectory(this.project.getProjectFolder());
    }

    @SuppressWarnings("unchecked")
    private void executeClick(ActionEvent event) {
        clearCache();

        this.cb.getItems().add("Item Generated");

        final var browse = new Button(LangProvider.fromLang("buttons.browse"));
        browse.setOnAction(e -> {
            this.selectedDirectory = this.directoryChooser.showDialog(this.stage);
            this.pathArea.setText(this.selectedDirectory.getPath());
        });
        browse.setStyle("-fx-background-color: " + ColorHelper.toHex(this.project.getTheme().getButtonColor()));

        final var next = new Button(LangProvider.fromLang("buttons.next"));
        next.setOnAction(this::nextButtonClick);
        next.setStyle("-fx-background-color: " + ColorHelper.toHex(this.project.getTheme().getButtonColor()));

        final var cancel = new Button(LangProvider.fromLang("buttons.cancel"));
        cancel.setOnAction(e -> this.stage.close());
        cancel.setStyle("-fx-background-color: " + ColorHelper.toHex(this.project.getTheme().getButtonColor()));

        this.pathArea.autosize();
        this.pathArea.deselect();

        this.vbox.getChildren().add(new HBox(this.modelType, this.cb));
        this.vbox.getChildren().add(new HBox(this.fileNameLabel, this.fileName));
        this.vbox.getChildren().add(new HBox(this.pathLabel, this.pathArea, browse));
        this.vbox.getChildren().add(new HBox(next, cancel));

        final var scene = new Scene(this.vbox);
        this.stage.setScene(scene);

        this.stage.setWidth(450);
        // stage.sizeToScene();
        this.stage.centerOnScreen();
        this.stage.requestFocus();
        this.stage.showAndWait();
    }

    private void nextButtonClick(ActionEvent event) {
        this.stage.close();
        if (this.cb.getSelectionModel().getSelectedIndex() == 0) {
            new ItemGeneratedModel(this.project, this.fileName.getText()).openWindow();
            clearCache();
        }
    }

}