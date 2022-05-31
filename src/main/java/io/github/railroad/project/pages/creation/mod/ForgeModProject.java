package io.github.railroad.project.pages.creation.mod;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.XML;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.railroad.project.pages.OpenProject;
import io.github.railroad.project.pages.Page;
import io.github.railroad.utility.Gsons;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ForgeModProject {
    public static class Page1 extends Page {
        public final BorderPane mainPane;
        public final Label nameLabel, artifactIdLabel, groupIdLabel, versionLabel;
        public final MFXTextField nameInput, artifactIdInput, groupIdInput, versionInput;
        public final VBox mainVertical;
        
        public Page1() {
            super(new BorderPane());
            
            this.mainPane = (BorderPane) getCore();
            
            this.nameLabel = new Label("Mod Name:");
            this.artifactIdLabel = new Label("Artifact ID (modid):");
            this.groupIdLabel = new Label("Group ID (main package):");
            this.versionLabel = new Label("Version");

            this.nameLabel.setTextFill(Color.WHITESMOKE);
            this.artifactIdLabel.setTextFill(Color.WHITESMOKE);
            this.groupIdLabel.setTextFill(Color.WHITESMOKE);
            this.versionLabel.setTextFill(Color.WHITESMOKE);
            
            final String defaultName = OpenProject.getRandomProjectName();
            final String defaultModID = defaultName.toLowerCase().replaceAll("\s+", "").trim();
            final String defaultPackage = "com.yourname." + defaultModID;
            final String defaultVersion = "1.0-SNAPSHOT";
            
            this.nameInput = new MFXTextField("", "Mod Name");
            this.artifactIdInput = new MFXTextField("", "Mod ID");
            this.groupIdInput = new MFXTextField("", "Main Package");
            this.versionInput = new MFXTextField("", "Version");

            this.nameInput.setMinSize(200, 30);
            this.artifactIdInput.setMinSize(200, 30);
            this.groupIdInput.setMinSize(200, 30);
            this.versionInput.setMinSize(200, 30);

            this.mainVertical = new VBox(60, new VBox(10, this.nameLabel, this.nameInput),
                new VBox(10, this.artifactIdLabel, this.artifactIdInput),
                new VBox(10, this.groupIdLabel, this.groupIdInput), new VBox(10, this.versionLabel, this.versionInput));
            this.mainVertical.setAlignment(Pos.CENTER_LEFT);
            this.mainVertical.setPadding(InsetsFactory.left(100));

            this.mainPane.setCenter(this.mainVertical);
            this.mainPane.setStyle("-fx-background-color: #1B232C;");
        }
        
        public boolean isComplete() {
            return true;
        }
    }

    public static class Page2 extends Page {
        private static final String PROMS = "https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json";
        private static final String MAVEN = "https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml";

        public final BorderPane mainPane;
        public final Label mcVersionLabel, forgeVersionLabel, mappingsLabel, mappingsVersionLabel;
        public final MFXComboBox<String> mcVersion, forgeVersion, mappings, mappingsVersion;
        public final VBox mainVertical;
        
        public Page2() {
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
                    
                    loadMappings(this.mappings.getItems(), newVal);
                    this.mappings.setDisable(false);
                }
            });

            this.mappingsVersion.selectedItemProperty().addListener((observable, oldVal, newVal) -> {
                if (newVal != null) {
                    loadMappingsVersions(this.mappingsVersion.getItems(), this.mcVersion.getText(), newVal);
                    this.mappingsVersion.setDisable(false);
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
        }

        public boolean isComplete() {
            return true;
        }
        
        private static void loadForgeVersions(ObservableList<String> options, String mcVersion) {
            options.clear();

            try {
                final URLConnection connection = new URL(MAVEN).openConnection();
                final JSONObject xmlJson = XML
                    .toJSONObject(IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8));
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
        
        private static void loadMappings(ObservableList<String> options, String mcVersion) {
            options.clear();

            final String[] parts = mcVersion.split("\\.");
            try {
                final Integer minor = Integer.parseInt(parts[1]);
                if (minor >= 14) {
                    options.add("Yarn");
                }

                if (minor > 16) {
                    options.addAll("Mojmap", "Parchment");
                } else if (minor < 16) {
                    options.add("MCP");
                } else if (minor == 16) {
                    final Integer subMinor = Integer.parseInt(parts[2]);
                    if (subMinor == 5) {
                        options.addAll("MCP", "Mojmap", "Parchment");
                    } else {
                        options.add("MCP");
                    }
                }
            } catch (NumberFormatException | IndexOutOfBoundsException exception) {
                throw new IllegalStateException("There was an error calculating Mappings Channels!", exception);
            }
        }
        
        private static void loadMappingsVersions(ObservableList<String> options, String mcVersion,
            String mappingsChannel) {
            options.clear();

            try {
                if ("MCP".equals(mappingsChannel)) {

                } else if ("Mojmap".equals(mappingsChannel)) {
                    options.add(mcVersion);
                } else if ("Parchment".equals(mappingsChannel)) {

                } else if ("Yarn".equals(mappingsChannel)) {

                }
            } catch (final IOException exception) {
                throw new IllegalStateException("", exception);
            }
        }
        
        private static void loadMinecraftVersions(ObservableList<String> options) {
            options.clear();
            
            try {
                final URLConnection connection = new URL(PROMS).openConnection();
                final JsonObject response = Gsons.READING_GSON
                    .fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);
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

                versions.forEach(options::add);
            } catch (final IOException exception) {
                throw new IllegalStateException("Unable to load Minecraft Versions!", exception);
            }
        }
    }
}