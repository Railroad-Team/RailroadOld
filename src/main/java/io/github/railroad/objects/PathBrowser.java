package io.github.railroad.objects;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class PathBrowser extends HBox {
    private final MFXTextField pathField = new MFXTextField();
    private final MFXButton browseButton = new MFXButton("Browse");

    public PathBrowser(ChooserOptions options) {
        super(10);
        getChildren().addAll(this.pathField, this.browseButton);

        this.browseButton.setOnAction(ignored -> {
            if (options.directorySelection) {
                var directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle(options.title);
                directoryChooser.setInitialDirectory(options.initialDirectory.isBlank() ? null : new File(options.initialDirectory));
                var file = directoryChooser.showDialog(getScene().getWindow());
                if (file != null && file.exists()) {
                    this.pathField.setText(String.join(";", file.getAbsolutePath()));
                }
            } else {
                var fileChooser = new FileChooser();
                fileChooser.setTitle(options.title);
                fileChooser.setInitialDirectory(options.initialDirectory.isBlank() ? null : new File(options.initialDirectory));
                fileChooser.setInitialFileName(options.initialFileName);
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", options.extensions));
                if (options.multipleSelection) {
                    var files = fileChooser.showOpenMultipleDialog(getScene().getWindow());
                    if (files != null && !files.isEmpty() && files.stream().allMatch(File::exists)) {
                        this.pathField.setText(String.join(";", files.stream().map(File::getAbsolutePath).toList()));
                    }
                } else {
                    var file = fileChooser.showOpenDialog(getScene().getWindow());
                    if (file != null && file.exists()) {
                        this.pathField.setText(file.getAbsolutePath());
                    }
                }
            }
        });
    }

    public Optional<List<Path>> getSelectedPath() {
        String[] split = this.pathField.getText().split(";");
        if (split.length == 0 || split[0].isBlank()) {
            return Optional.empty();
        }

        List<Path> paths = Stream.of(split).map(Path::of).filter(Files::exists).toList();
        return paths.isEmpty() ? Optional.empty() : Optional.of(paths);
    }

    public boolean hasValidPath() {
        return !getSelectedPath().orElse(List.of()).isEmpty();
    }

    public boolean hasEmptyPath() {
        return this.pathField.getText().isBlank();
    }

    public MFXTextField getPathField() {
        return this.pathField;
    }

    public MFXButton getBrowseButton() {
        return this.browseButton;
    }

    public static class ChooserOptions {
        private String title;
        private String initialDirectory;
        private String initialFileName;
        private List<String> extensions;
        private boolean multipleSelection;
        private boolean directorySelection;

        public ChooserOptions() {
            this.title = "Choose a file";
            this.initialDirectory = System.getProperty("user.home");
            this.initialFileName = "";
            this.extensions = List.of("*");
            this.multipleSelection = false;
            this.directorySelection = false;
        }

        public ChooserOptions title(@NotNull String title) {
            this.title = title;
            return this;
        }

        public ChooserOptions initialDirectory(@NotNull String initialDirectory) {
            this.initialDirectory = initialDirectory;
            return this;
        }

        public ChooserOptions initialFileName(@NotNull String initialFileName) {
            this.initialFileName = initialFileName;
            return this;
        }

        public ChooserOptions extensions(@NotNull List<String> extensions) {
            this.extensions = extensions;
            return this;
        }

        public ChooserOptions multipleSelection() {
            this.multipleSelection = true;
            return this;
        }

        public ChooserOptions directorySelection() {
            this.directorySelection = true;
            return this;
        }

        @Override
        public String toString() {
            return "ChooserOptions{" +
                    "title='" + title + '\'' +
                    ", initialDirectory='" + initialDirectory + '\'' +
                    ", initialFileName='" + initialFileName + '\'' +
                    ", extensions=" + extensions +
                    ", multipleSelection=" + multipleSelection +
                    ", directorySelection=" + directorySelection +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChooserOptions that)) return false;
            return multipleSelection == that.multipleSelection && directorySelection == that.directorySelection && Objects.equals(title, that.title) && Objects.equals(initialDirectory, that.initialDirectory) && Objects.equals(initialFileName, that.initialFileName) && Objects.equals(extensions, that.extensions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, initialDirectory, initialFileName, extensions, multipleSelection, directorySelection);
        }
    }
}
