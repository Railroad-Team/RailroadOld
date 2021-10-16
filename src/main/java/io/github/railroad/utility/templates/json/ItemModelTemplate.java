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
 * 
 * @author matyrobbrt
 *
 */
public class ItemModelTemplate extends JsonFileMenuItem {

	public final Project project;
	
	public ItemModelTemplate(Project project) {
		super(LangProvider.fromLang("menuBar.json.itemModel"));
		this.project = project;
		this.setOnAction(this::executeClick);
	}
	
	@SuppressWarnings("rawtypes")
	private ChoiceBox cb = new ChoiceBox<>();
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	private File selectedDirectory;
	private TextField pathArea = new TextField();
	private TextField fileName = new TextField();
	private Label fileNameLabel = new Label(" " + LangProvider.fromLang("textArea.fileName") + ": ");
	private Label modelType = new Label(" " + fromLang("modelTypeField"));
	private Label pathLabel = new Label(" " + LangProvider.fromLang("textArea.selectPath") + ": ");

	private VBox vbox = new VBox();

	@SuppressWarnings("unchecked")
	private void executeClick(ActionEvent event) {
		clearCache();

		cb.getItems().add("Item Generated");

		var browse = new Button(LangProvider.fromLang("buttons.browse"));
		browse.setOnAction(e -> {
			selectedDirectory = directoryChooser.showDialog(stage);
			pathArea.setText(selectedDirectory.getPath());
		});
		browse.setStyle("-fx-background-color: " + ColorHelper.toHex(this.project.getTheme().getButtonColor()));

		var next = new Button(LangProvider.fromLang("buttons.next"));
		next.setOnAction(this::nextButtonClick);
		next.setStyle("-fx-background-color: " + ColorHelper.toHex(this.project.getTheme().getButtonColor()));
		
		var cancel = new Button(LangProvider.fromLang("buttons.cancel"));
		cancel.setOnAction(e -> stage.close());
		cancel.setStyle("-fx-background-color: " + ColorHelper.toHex(this.project.getTheme().getButtonColor()));

		pathArea.autosize();
		pathArea.deselect();

		vbox.getChildren().add(new HBox(modelType, cb));
		vbox.getChildren().add(new HBox(fileNameLabel, fileName));
		vbox.getChildren().add(new HBox(pathLabel, pathArea, browse));
		vbox.getChildren().add(new HBox(next, cancel));

		final var scene = new Scene(vbox);
		stage.setScene(scene);

		stage.setWidth(450);
		// stage.sizeToScene();
		stage.centerOnScreen();
		stage.requestFocus();
		stage.showAndWait();
	}

	private void nextButtonClick(ActionEvent event) {
		stage.close();
		if (cb.getSelectionModel().getSelectedIndex() == 0) {
			new ItemGeneratedModel(project, fileName.getText()).openWindow();
			clearCache();
		}
	}

	private void clearCache() {
		cb = new ChoiceBox<>();
		vbox = new VBox();
		pathArea.setText("");
		fileName.setText("");
		directoryChooser.setInitialDirectory(this.project.getProjectFolder());
	}

	
}