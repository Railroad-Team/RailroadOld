package io.github.railroad.project.pages.creation.mod.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.railroad.objects.PathBrowser;
import io.github.railroad.project.pages.OpenProject;
import io.github.railroad.project.pages.Page;
import io.github.railroad.project.pages.creation.mod.forge.tasks.DeleteUnusedFilesStep;
import io.github.railroad.project.pages.creation.mod.forge.tasks.DownloadMdkStep;
import io.github.railroad.project.pages.creation.mod.forge.tasks.ExtractMdkStep;
import io.github.railroad.project.task.Task;
import io.github.railroad.project.task.ui.TaskProgressSpinner;
import io.github.railroad.utility.Gsons;
import io.github.railroad.utility.helper.MappingHelper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ForgeModProject {
    private static final String FORGE_PROMOTIONS = "https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json";
    private static final String FORGE_MAVEN = "https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml";

    public static class Page1 extends Page {
        public final BorderPane mainPane;
        public final Label nameLabel, artifactIdLabel, groupIdLabel, versionLabel, authorLabel, useMixinsLabel;
        public final MFXTextField nameInput, artifactIdInput, groupIdInput, versionInput, authorInput;
        public final MFXCheckbox useMixinsCheckbox;
        public final VBox mainVertical;

        public Page1() {
            super(new BorderPane());

            this.mainPane = (BorderPane) getCore();

            this.nameLabel = new Label("Mod Name:");
            this.artifactIdLabel = new Label("Artifact ID (modid):");
            this.groupIdLabel = new Label("Group ID (main package):");
            this.versionLabel = new Label("Version");
            this.authorLabel = new Label("Author");
            this.useMixinsLabel = new Label("Uses Mixins?");

            this.nameLabel.setTextFill(Color.WHITESMOKE);
            this.artifactIdLabel.setTextFill(Color.WHITESMOKE);
            this.groupIdLabel.setTextFill(Color.WHITESMOKE);
            this.versionLabel.setTextFill(Color.WHITESMOKE);
            this.authorLabel.setTextFill(Color.WHITESMOKE);
            this.useMixinsLabel.setTextFill(Color.WHITESMOKE);

            final String defaultName = OpenProject.getRandomProjectName();
            final String defaultModID = defaultName.toLowerCase().replaceAll(" ", "").trim();
            final String defaultPackage = "com.yourname." + defaultModID;
            final String defaultVersion = "1.0-SNAPSHOT";
            final String defaultAuthor = System.getProperty("user.name");

            this.nameInput = new MFXTextField(defaultName, "Mod Name");
            this.artifactIdInput = new MFXTextField(defaultModID, "Mod ID");
            this.groupIdInput = new MFXTextField(defaultPackage, "Main Package");
            this.versionInput = new MFXTextField(defaultVersion, "Version");
            this.authorInput = new MFXTextField(defaultAuthor, "Author");
            this.useMixinsCheckbox = new MFXCheckbox();

            this.nameInput.setMinSize(200, 30);
            this.artifactIdInput.setMinSize(200, 30);
            this.groupIdInput.setMinSize(200, 30);
            this.versionInput.setMinSize(200, 30);
            this.authorInput.setMinSize(200, 30);
            this.useMixinsCheckbox.setMinSize(30, 30);

            this.mainVertical = new VBox(40, new VBox(10, this.nameLabel, this.nameInput),
                    new VBox(10, this.artifactIdLabel, this.artifactIdInput),
                    new VBox(10, this.groupIdLabel, this.groupIdInput),
                    new VBox(10, this.versionLabel, this.versionInput),
                    new VBox(10, this.authorLabel, this.authorInput),
                    new VBox(10, this.useMixinsLabel, this.useMixinsCheckbox));
            this.mainVertical.setAlignment(Pos.CENTER_LEFT);
            this.mainVertical.setPadding(InsetsFactory.left(100));

            this.mainPane.setCenter(this.mainVertical);
            this.mainPane.setStyle("-fx-background-color: #1B232C;");
        }

        public boolean isComplete() {
            boolean name = !this.nameInput.getText().isBlank();
            boolean artifactId = !this.artifactIdInput.getText().isBlank();
            boolean groupId = !this.groupIdInput.getText().isBlank();
            boolean version = !this.versionInput.getText().isBlank();
            boolean author = !this.authorInput.getText().isBlank();

            return name && artifactId && groupId && version && author;
        }
    }

    public static class Page2 extends Page {
        public final BorderPane mainPane;
        public final Label mcVersionLabel, forgeVersionLabel, mappingsLabel, mappingsVersionLabel;
        public final MFXComboBox<String> mcVersion, forgeVersion, mappings, mappingsVersion;
        public final VBox mainVertical;

        public final Page1 page1;

        public Page2(Page1 page1) {
            super(new BorderPane());

            this.mainPane = (BorderPane) getCore();

            this.mcVersionLabel = new Label("Minecraft Version:");
            this.forgeVersionLabel = new Label("Forge Version:");
            this.mappingsLabel = new Label("Mappings Channel:");
            this.mappingsVersionLabel = new Label("Mappings Version:");

            this.mcVersionLabel.setTextFill(Color.WHITESMOKE);
            this.forgeVersionLabel.setTextFill(Color.WHITESMOKE);
            this.mappingsLabel.setTextFill(Color.WHITESMOKE);
            this.mappingsVersionLabel.setTextFill(Color.WHITESMOKE);

            this.mcVersion = new MFXComboBox<>();
            this.forgeVersion = new MFXComboBox<>();
            this.mappings = new MFXComboBox<>();
            this.mappingsVersion = new MFXComboBox<>();

            loadMinecraftVersions(this.mcVersion.getItems());
            this.forgeVersion.setDisable(true);
            this.mappings.setDisable(true);
            this.mappingsVersion.setDisable(true);

            this.mcVersion.selectedItemProperty().addListener((observable, oldVal, newVal) -> {
                if (newVal != null) {
                    loadForgeVersions(this.forgeVersion.getItems(), newVal);
                    this.forgeVersion.setDisable(false);

                    MappingHelper.loadMappings(this.mappings.getItems(), newVal);
                    this.mappings.setDisable(false);
                }
            });

            this.mappings.selectedItemProperty().addListener((observable, oldVal, newVal) -> {
                if (newVal != null) {
                    MappingHelper.loadMappingsVersions(this.mappingsVersion.getItems(), this.mcVersion.getText(),
                            newVal);
                    this.mappingsVersion.setDisable(this.mappingsVersion.getItems().isEmpty());
                }
            });

            this.mcVersion.setPromptText("Minecraft Version");
            this.forgeVersion.setPromptText("Forge Version");
            this.mappings.setPromptText("Mappings Channel");
            this.mappingsVersion.setPromptText("Mappings Version");

            this.mcVersion.setMinSize(200, 30);
            this.forgeVersion.setMinSize(200, 30);
            this.mappings.setMinSize(200, 30);
            this.mappingsVersion.setMinSize(200, 30);

            this.mainVertical = new VBox(60, new VBox(10, this.mcVersionLabel, this.mcVersion),
                    new VBox(10, this.forgeVersionLabel, this.forgeVersion),
                    new VBox(10, this.mappingsLabel, this.mappings),
                    new VBox(10, this.mappingsVersionLabel, this.mappingsVersion));
            this.mainVertical.setAlignment(Pos.CENTER_LEFT);
            this.mainVertical.setPadding(InsetsFactory.left(100));

            this.mainPane.setCenter(this.mainVertical);
            this.mainPane.setStyle("-fx-background-color: #1B232C;");

            this.page1 = page1;
        }

        private static void loadForgeVersions(ObservableList<String> options, String mcVersion) {
            options.clear();

            try {
                final URLConnection connection = new URL(FORGE_MAVEN).openConnection();
                final JSONObject xmlJson = XML.toJSONObject(
                        IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8));
                final JsonObject json = Gsons.READING_GSON.fromJson(xmlJson.toString(), JsonObject.class);
                final JsonObject versionObj = json.getAsJsonObject("metadata").getAsJsonObject("versioning")
                        .getAsJsonObject("versions");
                final JsonArray versions = versionObj.getAsJsonArray("version");
                for (final JsonElement element : versions) {
                    final String version = element.getAsString();
                    if (version.startsWith(mcVersion + "-")) {
                        options.add(version.replaceAll(mcVersion + "-", "").replaceAll("-" + mcVersion, ""));
                    }
                }
            } catch (final IOException exception) {
                throw new IllegalStateException("Unable to read forge versions!", exception);
            }
        }

        public static void loadMinecraftVersions(ObservableList<String> options) {
            options.clear();

            try {
                final URLConnection connection = new URL(FORGE_PROMOTIONS).openConnection();
                final JsonObject response = Gsons.READING_GSON.fromJson(
                        new InputStreamReader(connection.getInputStream()), JsonObject.class);
                final JsonObject promos = response.getAsJsonObject("promos");

                final List<String> versions = new ArrayList<>();
                promos.entrySet().forEach(entry -> {
                    final String version = entry.getKey().replace("-latest", "").replace("-recommended", "")
                            .replace("_pre4", "");
                    if (!versions.contains(version)) {
                        versions.add(version);
                    }
                });

                Collections.reverse(versions);

                options.addAll(versions);
            } catch (final IOException exception) {
                throw new IllegalStateException("Unable to load Minecraft Versions!", exception);
            }
        }

        public boolean isComplete() {
            boolean mcVersion = this.mcVersion.getSelectedItem() != null && !this.mcVersion.getText().isBlank();
            boolean forgeVersion = this.forgeVersion.getSelectedItem() != null && !this.forgeVersion.getText()
                    .isBlank();
            boolean mappings = this.mappings.getSelectedItem() != null && !this.mappings.getText().isBlank();
            boolean mappingsVersion = this.mappingsVersion.getSelectedItem() != null && !this.mappingsVersion.getText()
                    .isBlank();

            return mcVersion && forgeVersion && mappings && mappingsVersion;
        }
    }

    public static class Page3 extends Page {
        private final BorderPane mainPane;
        private final MFXButton startButton;
        private final PathBrowser pathBrowser;
        private final MFXTextField folderName;
        private final Label errorLabel;
        private final TaskProgressSpinner progressSpinner;

        public final Page2 page2;

        public Page3(Page2 page2) {
            super(new BorderPane());

            this.mainPane = (BorderPane) getCore();
            this.page2 = page2;

            this.startButton = new MFXButton("Start");
            this.startButton.setButtonType(ButtonType.RAISED);
            this.startButton.setRippleColor(Color.WHITE);

            this.pathBrowser = new PathBrowser(
                    new PathBrowser.ChooserOptions()
                            .title("Choose Project Location")
                            .directorySelection());

            this.pathBrowser.getPathField().setMinSize(200, 30);
            this.pathBrowser.getBrowseButton().setMinSize(200, 30);
            this.pathBrowser.setAlignment(Pos.CENTER);

            this.folderName = new MFXTextField("mod", "Folder Name");
            this.folderName.setMinSize(200, 30);

            this.errorLabel = new Label("Please fill out all fields!");
            this.errorLabel.setTextFill(Color.RED);
            this.errorLabel.setVisible(false);

            Supplier<Path> pathSupplier = () -> this.pathBrowser.getSelectedPath(false)
                    .map(paths -> paths.get(0))
                    .map(path -> path.resolve(this.folderName.getText()))
                    .orElse(null);

            var task = new Task("Setup MDK");
            task.addStep(new DownloadMdkStep(page2.mcVersion::getText, page2.forgeVersion::getText, pathSupplier));
            task.addStep(new ExtractMdkStep(pathSupplier));
            task.addStep(new DeleteUnusedFilesStep(pathSupplier));

            this.progressSpinner = new TaskProgressSpinner(task);
            this.progressSpinner.setVisible(false);

            var content = new VBox(20, this.pathBrowser, this.startButton, this.folderName, this.errorLabel, this.progressSpinner);
            content.setAlignment(Pos.CENTER);

            this.mainPane.setCenter(content);
            this.mainPane.setStyle("-fx-background-color: #1B232C;");

            this.startButton.setOnAction(ignored -> {
                if (this.pathBrowser.hasEmptyPath()) {
                    this.errorLabel.setText("Please select a path for your mod folder!");
                    this.errorLabel.setVisible(true);
                    Platform.runLater(() -> this.pathBrowser.getPathField().requestFocus());
                    return;
                }

                if (!this.pathBrowser.hasValidPath(false)) {
                    this.errorLabel.setText("Please select a valid path for your mod folder!");
                    this.errorLabel.setVisible(true);
                    Platform.runLater(() -> this.pathBrowser.getPathField().requestFocus());
                    return;
                }

                // move to next page
                this.errorLabel.setVisible(false);
                try {
                    Path path = this.pathBrowser.getSelectedPath(false).map(paths -> paths.get(0)).orElse(null);
                    if(path == null)
                        throw new IOException("Path is provided is invalid!");

                    if (Files.notExists(path))
                        Files.createDirectories(path);

                    Path modFolder = path.resolve(this.folderName.getText());
                    if (Files.notExists(modFolder))
                        Files.createDirectories(modFolder);

                    // open folder in explorer
                    switch (System.getProperty("os.name").toLowerCase()) {
                        case "windows" -> Runtime.getRuntime().exec("explorer.exe " + modFolder.toAbsolutePath());
                        case "linux" -> Runtime.getRuntime().exec("xdg-open " + modFolder.toAbsolutePath());
                        case "mac" -> Runtime.getRuntime().exec("open " + modFolder.toAbsolutePath());
                    }

                    this.progressSpinner.setVisible(true);
                    this.progressSpinner.start();
                } catch(IOException exception) {
                    this.errorLabel.setText("Unable to create mod folder! " + exception.getLocalizedMessage());
                    this.errorLabel.setVisible(true);
                    return;
                }
            });
        }
    }
}