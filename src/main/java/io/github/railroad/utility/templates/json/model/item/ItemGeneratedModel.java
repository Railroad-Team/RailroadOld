package io.github.railroad.utility.templates.json.model.item;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

import io.github.railroad.project.Project;
import io.github.railroad.project.lang.LangProvider;
import io.github.railroad.utility.Gsons;
import io.github.railroad.utility.templates.json.JsonTemplate;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ItemGeneratedModel extends JsonTemplate {
    private TextField layer0Texture = new TextField();
    private Label layer0Label = new Label(fromLang("layer0"));

    public ItemGeneratedModel(@NotNull Project project, String fileName) {
        super(project, fileName);
    }

    @Override
    public void openWindow() {
        clearCache();
        this.layer0Label.setPadding(new Insets(3));

        final var createBtn = new Button(LangProvider.fromLang("buttons.create"));

        createBtn.setOnAction(click -> {
            final var jsonObject = new JsonObject();
            jsonObject.addProperty("parent", "item/generated");

            final var texturesObj = new JsonObject();
            texturesObj.addProperty("layer0", this.layer0Texture.getText());

            jsonObject.add("textures", texturesObj);

            final var file = new File(this.project.getProjectFolder(), this.fileName + ".json");

            if (file.exists()) {
                final var alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("File already exists");
                alert.show();
            }

            try (var writer = new FileWriter(file)) {
                Gsons.JSON_CREATING_GSON.toJson(jsonObject, writer);
                this.stage.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });

        final var vbox = new VBox(5, this.layer0Label, this.layer0Texture, createBtn);
        final var scene = new Scene(vbox);

        this.stage.setScene(scene);
        this.stage.showAndWait();
    }

    @Override
    protected void clearCache() {
        super.clearCache();
        this.layer0Texture = new TextField();
    }
}
