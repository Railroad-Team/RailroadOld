package io.github.railroad.project;

import static io.github.railroad.project.lang.LangProvider.fromLang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.javafx.tk.Toolkit;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.enums.StepperToggleState;
import io.github.palexdev.materialfx.utils.BindingUtils;
import io.github.railroad.Railroad;
import io.github.railroad.project.lang.LangProvider;
import io.github.railroad.project.settings.theme.Theme;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.stage.Window;

/**
 * @author TurtyWurty
 */
public class Project {
	/**
	 * How to use the config
	 * 
	 * before showing the folder selector add this
	 
	if (JsonConfigs.GENERAL_CONFIG.getConfig() != null && JsonConfigs.GENERAL_CONFIG.getConfig().getProjectSettings().projectPath != null) {
		this.projectFolder = new File(JsonConfigs.GENERAL_CONFIG.getConfig().getProjectSettings().projectPath);
	} else {
		implement the selector here.
			
		in order to save the path to the config.. execute this code:
			
		JsonConfigs.GENERAL_CONFIG.writeConfig(new RailroadConfigJson(new ProjectSettingsEntry(this.projectFolder.getPath().toString()));
			
	}
		
		i dont see a reason to format this. i just threw it here so you know how to use the <b>temporary</b> solution
	 */

	private final Theme theme;

    public static final DocumentBuilderFactory XML_PARSER = DocumentBuilderFactory.newInstance();
    static {
        try {
            XML_PARSER.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            XML_PARSER.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            XML_PARSER.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    private final Theme theme;

    private File projectFolder;

    /**
     * Creates the Project Settings and displays the initial "Project Directory
     * Chooser".
     *
     * @param themeSettings - The {@link Theme} to apply.
     */
    public Project(final Theme themeSettings) {
        this.theme = themeSettings;
        createWindow();
    }

    private static boolean canCreate(Path folder) {
        return true;
    }

    private static boolean canImport(Path folder) {
        return true;
    }

    private static Pair<Boolean, ProjectInfo> canOpen(Path folder) {
        final var defaultRet = Pair.of(false, (ProjectInfo) null);
        final Collection<File> railroadFiles = FileUtils.listFiles(folder.toFile(),
                new String[] { ".railroad" }, true);
        if (railroadFiles.isEmpty())
            return defaultRet;

        final List<File> results = railroadFiles
                .stream().filter(file -> file.getName()
                        .equalsIgnoreCase("project.railroad"))
                .sorted((file0, file1) -> file0.getAbsolutePath().replace('\\', '/').split("/").length < file1
                        .getAbsolutePath().replace('\\', '/').split("/").length ? 1 : 0)
                .toList();

        if (results.isEmpty())
            return defaultRet;

        final File projectFile = results.get(0);
        final var content = new StringBuilder();
        try {
            FileUtils.readLines(projectFile, StandardCharsets.UTF_8).forEach(content::append);
        } catch (final IOException e) {
            return defaultRet;
        }

        if (content.toString().isBlank())
            return defaultRet;

        final ProjectInfo.Builder projectInfoBuilder = null;
        boolean valid = true;
        try {
            while (valid) {
                final var documentBuilder = XML_PARSER.newDocumentBuilder();
                final Document document = documentBuilder.parse(projectFile);
                document.getDocumentElement().normalize();
                if (!document.getDocumentElement().getNodeName().equalsIgnoreCase("modinfo")) {
                    valid = false;
                    break;
                }

                // projectInfoBuilder = ProjectInfo.Builder.create(modType, projectName);
                final NodeList nodes = document.getDocumentElement().getChildNodes();
                for (int nodeIndex = 0; nodeIndex < nodes.getLength(); nodeIndex++) {
                    final Node node = nodes.item(nodeIndex);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {

                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            valid = false;
        }

        return Pair.of(valid, projectInfoBuilder.build());
    }

    public Stage createWindow() {
        final var window = new Stage();

        final var stepper = createStepper(window);
        final var pane = new BorderPane(stepper);
        final var scene = new Scene(pane);
        scene.getStylesheets().add(Railroad.class.getResource("/default.css").toExternalForm());
        window.setScene(scene);
        window.setOnCloseRequest(event -> {
            Toolkit.getToolkit().exitAllNestedEventLoops();
            System.exit(0);
            window.close();
        });

        window.getIcons().add(new Image(Railroad.class.getResource("/thumbnail.png").toString(), 256, 256,
                true, true, true));
        window.setMinWidth(800);
        window.setMinHeight(600);
        window.setWidth(800);
        window.setHeight(600);
        window.setScene(scene);
        window.setTitle(fromLang("project.selectWindowTitle"));
        window.requestFocus();
        window.showAndWait();
        return window;
    }

	/**
	 * @return The Folder used for this {@link Project}.
	 */
	public File getProjectFolder() {
		return this.projectFolder;
	}

	/**
	 * @return The {@link Theme} used for this {@link Project}.
	 */
	public Theme getTheme() {
		return this.theme;
	}

	public String getProjectName() {
		return getProjectFolder().getName();
	}

    private MFXStepper createStepper(Window window) {
        final var openCreateIcon = new FontIcon(MaterialDesignF.FOLDER_OPEN);
        openCreateIcon.setIconSize(32);
        openCreateIcon.setIconColor(Color.web("#64b5f6"));
        final var openCreateLabel = new Label(fromLang("project.openCreateLabel"));

        final var openCreateToggleGroup = new ToggleGroup();
        final var openRadioBtn = new MFXRadioButton(fromLang("project.openButton"));
        openRadioBtn.setToggleGroup(openCreateToggleGroup);
        final var importRadioBtn = new MFXRadioButton(fromLang("project.importButton"));
        importRadioBtn.setToggleGroup(openCreateToggleGroup);
        final var createRadioBtn = new MFXRadioButton(fromLang("project.createButton"));
        createRadioBtn.setToggleGroup(openCreateToggleGroup);
        final var buttonHBox = new HBox(10f, openRadioBtn, importRadioBtn, createRadioBtn);
        buttonHBox.setAlignment(Pos.CENTER);

        final var openCreateVBox = new VBox(20f, openCreateLabel, buttonHBox);
        openCreateVBox.setAlignment(Pos.CENTER);
        final var openCreateToggle = new MFXStepperToggle(fromLang("project.openCreateSection"),
                openCreateIcon, openCreateVBox);
        openCreateToggle.getValidator()
                .add(BindingUtils.toProperty(openRadioBtn.selectedProperty()
                        .or(createRadioBtn.selectedProperty()).or(importRadioBtn.selectedProperty())),
                        fromLang("project.openCreateError"));

        final var configurationIcon = new FontIcon(MaterialDesignF.FOLDER_COG);
        configurationIcon.setIconSize(32);
        configurationIcon.setIconColor(Color.web("#ffca28"));

        final var configurationVBox = new VBox(10f);
        configurationVBox.setAlignment(Pos.CENTER);

        // Open
        final var openLabel = new Label(fromLang("project.openLabel"));
        final var openFolderText = new MFXTextField(
                System.getProperty("user.home").replace('\\', '/') + "/Documents");
        openFolderText.getProperties().put("CurrentText", openFolderText.textProperty().getValueSafe());
        openFolderText.setPrefWidth(200f);
        openFolderText.textProperty().addListener(listener -> {
            final String text = openFolderText.textProperty().getValueSafe();
            if (!text.equalsIgnoreCase((String) openFolderText.getProperties().get("CurrentText"))) {
                openFolderText.getProperties().put("CurrentText",
                        openFolderText.textProperty().getValueSafe());
                openFolderText.setText(text.replace('\\', '/'));
            }
        });

        openFolderText.getValidator().add(BindingUtils.toProperty(new BooleanBinding() {
            @Override
            protected boolean computeValue() {
                final String text = openFolderText.textProperty().getValueSafe();
                return Files.exists(Path.of(text));
            }
        }), fromLang("project.validDirectory"));

        final var dirChooser = new DirectoryChooser();
        dirChooser.setTitle(LangProvider.fromLang("project.chooseFolder"));
        dirChooser.setInitialDirectory(
                new File(System.getProperty("user.home").replace('\\', '/') + "/Documents"));

        final var openFolderButton = new MFXButton(fromLang("project.openFolderButton"));
        openFolderButton.setTextFill(Color.BLACK);
        openFolderButton.setOnAction(event -> {
            event.consume();
            final File file = dirChooser.showDialog(window);
            openFolderText.setText(file.getPath().replace('\\', '/'));
        });

        final var openHBox = new HBox(20f, openFolderText, openFolderButton);
        openHBox.setAlignment(Pos.CENTER);

        final var canOpen = BindingUtils.toProperty(new BooleanBinding() {
            @Override
            protected boolean computeValue() {
                return canOpen(Path.of(openFolderText.textProperty().getValueSafe())).getLeft();
            }
        });

        // Create

        final var configurationToggle = new MFXStepperToggle(fromLang("project.configurationSection"),
                configurationIcon, configurationVBox);
        configurationToggle.stateProperty().addListener(listener -> {
            if (configurationToggle.getState() == StepperToggleState.SELECTED) {
                if (openRadioBtn.isSelected()) {
                    configurationVBox.getChildren().clear();
                    configurationVBox.getChildren().addAll(openLabel, openHBox);
                } else if (createRadioBtn.isSelected()) {
                    configurationVBox.getChildren().clear();
                    configurationVBox.getChildren().addAll();
                }
            }
        });

        configurationToggle.getValidator().addDependencies(openFolderText.getValidator());
        configurationToggle.getValidator().add(canOpen, fromLang("project.cannotOpenFolder"));
        configurationToggle.stateProperty().addListener(listener -> {
            if (!configurationToggle.isValid() && configurationToggle.isShowErrorIcon()
                    && configurationToggle.getState() == StepperToggleState.ERROR
                    && openRadioBtn.isSelected()) {
                final var cannotOpenDialog = new ProjectCannotOpenDialog(this, window);
                cannotOpenDialog.show();
            }
        });

        final var toggles = List.of(openCreateToggle, configurationToggle);
        return new MFXStepper(toggles);
    }
}
