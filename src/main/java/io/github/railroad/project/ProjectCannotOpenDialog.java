package io.github.railroad.project;

import static io.github.railroad.project.lang.LangProvider.fromLang;

import io.github.palexdev.materialfx.controls.MFXStageDialog;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Window;

public class ProjectCannotOpenDialog extends MFXStageDialog {
    private final Project project;

    public ProjectCannotOpenDialog(Project project, Window owner) {
        super(DialogType.WARNING, fromLang("project.cannotOpenDialog.title"),
                fromLang("project.cannotOpenDialog.replaceLabel"));

        this.project = project;

        setOwner(owner);
        setModality(Modality.APPLICATION_MODAL);
        setScrimBackground(true);
        setCenterInOwner(true);
        setAllowDrag(false);
        setAnimate(true);
        addButtons();
    }

    private void addButtons() {
        final var center = (StackPane) getDialog().getCenter();
        center.getChildren().removeIf(node -> node instanceof final Label label
                && label.getText().equalsIgnoreCase(fromLang("project.cannotOpenDialog.replaceLabel")));

        final var content = new Label(fromLang("project.cannotOpenDialog.description.text0") + ":");
        content.getStyleClass().setAll("content-label");
        final var hyperlink0 = new Hyperlink(fromLang("project.cannotOpenDialog.description.hyperlink0"));
        final var hyperlink1 = new Hyperlink(fromLang("project.cannotOpenDialog.description.hyperlink1"));
        final var hyperlink2 = new Hyperlink(fromLang("project.cannotOpenDialog.description.hyperlink2"));

        content.setMinHeight(Region.USE_PREF_SIZE);
        content.setPrefWidth(getDialog().getPrefWidth() * 0.9);
        content.setMaxWidth(getDialog().getMaxWidth());
        content.setWrapText(true);
        content.setMaxWidth(350);
        content.setTextAlignment(TextAlignment.CENTER);

        final var innerVBox = new VBox(10f, hyperlink0, hyperlink1, hyperlink2);
        innerVBox.setAlignment(Pos.CENTER);
        final var vbox = new VBox(20f, content, innerVBox);
        vbox.setAlignment(Pos.CENTER);
        center.getChildren().add(vbox);

        StackPane.setAlignment(vbox, Pos.TOP_CENTER);
        StackPane.setMargin(vbox, new Insets(40, 20, 20, 20));
    }
}
