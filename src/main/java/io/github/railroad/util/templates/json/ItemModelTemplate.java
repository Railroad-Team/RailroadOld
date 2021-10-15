package io.github.railroad.util.templates.json;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.railroad.project.Project;
import io.github.railroad.project.lang.LangProvider;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ItemModelTemplate extends JsonTemplate {

	@SuppressWarnings("unused")
	private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public File directory;
	public String fileName;

	public ItemModelTemplate(File directory, String fileName) {
		this.directory = directory;
		this.fileName = fileName;
	}

	// Universal Items
	private final Stage stage = new Stage();
	@SuppressWarnings("rawtypes")
	private ChoiceBox cb = new ChoiceBox<>();

	private VBox vbox = new VBox(cb);

	// Specific Items

	// Block Parent
	private TextField blockParentField = new TextField();
	private VBox blockItemVBox = new VBox();

	@SuppressWarnings("unchecked")
	@Override
	public void openWindow(Project project) {
		clearCache();
		clearSelectionSpecificCache();

		cb.getItems().add("Block Parent Model");
		
		cb.onActionProperty().addListener(listener -> updateWindow());
		
		vbox.getChildren().add(new HBox(new Label(LangProvider.fromLang("menuItem.json.typeTextField")), cb));
		
		var hbox = new HBox(cb);

		var scene = new Scene(hbox, 100, 200);
		stage.setScene(scene);

		stage.setWidth(450);
		stage.centerOnScreen();
		stage.requestFocus();
		stage.showAndWait();
	}

	private void updateWindow() {
		//clearSelectionSpecificCache();

		if (cb.getSelectionModel().getSelectedIndex() == 1) {
			blockItemVBox.getChildren().add(new HBox(new Label(" " + getLang("parent") + ": "), blockParentField));
			vbox.getChildren().add(blockItemVBox);
		}
	}

	private void clearSelectionSpecificCache() {
		blockParentField.setText("");
		blockItemVBox = new VBox();
		vbox.getChildren().remove(blockItemVBox);
	}

	private void clearCache() {
		cb = new ChoiceBox<>();
		vbox = new VBox();
	}

}
