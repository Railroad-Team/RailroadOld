package io.github.railroad.objects;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.filefilter.DirectoryFileFilter;

import io.github.railroad.project.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProjectExplorer {

    private static <T extends String> ExplorerTreeItem<T> createNode(final T rootFolder) {
        return new ExplorerTreeItem<>(rootFolder, new File(rootFolder).getName());
    }

    @SuppressWarnings("unchecked")
    public <T extends String> TreeView<T> createProjectExplorer(final Project project) {
        final var root = createNode(project.getProjectFolder().getPath());
        root.setExpanded(true);
        return new TreeView<>((ExplorerTreeItem<T>) root);
    }

    public static class ExplorerTreeItem<T extends String> extends TreeItem<T> {
        private boolean isLeaf;

        private boolean isFirstTimeChildren = true;

        private boolean isFirstTimeLeaf = true;
        private final String fullName, actualName;

        @SuppressWarnings("unchecked")
        public ExplorerTreeItem(final String fullName, final String actualName) {
            this.fullName = fullName;
            this.actualName = actualName;
            setValue((T) this.actualName);
        }

        private static Image getIconImage(final File file) {
            final var swingImage = getLargeIcon(file);
            if (swingImage == null)
                return null;
            final var bImg = new BufferedImage(swingImage.getIconWidth(), swingImage.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            final Graphics2D graphics = bImg.createGraphics();
            swingImage.paintIcon(new Canvas(), graphics, 0, 0);
            // graphics.drawImage(bImg, 0, 0, null);
            graphics.dispose();

            return SwingFXUtils.toFXImage(bImg, null);
        }

        private static Icon getLargeIcon(final File file) {
            if (file != null)
                return FileSystemView.getFileSystemView().getSystemIcon(file);
            return null;
        }

        public String getActualName() {
            return this.actualName;
        }

        @Override
        public ObservableList<TreeItem<T>> getChildren() {
            if (this.isFirstTimeChildren) {
                this.isFirstTimeChildren = false;

                // First getChildren() call, so we actually go off and
                // determine the children of the File contained in this ExplorerTreeItem.
                super.getChildren().setAll(buildChildren(this));
            }
            return super.getChildren();
        }

        public String getFullName() {
            return this.fullName;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean isLeaf() {
            setValue((T) this.actualName);
            if (this.isFirstTimeLeaf) {
                this.isFirstTimeLeaf = false;
                final var fileStr = this.fullName;
                final var file = new File(fileStr);
                final var image = getIconImage(file);
                if (image != null) {
                    setGraphic(new ImageView(image));
                }

                this.isLeaf = file.isFile() || file.isDirectory() && file.listFiles().length <= 0;
            }

            return this.isLeaf;
        }

        @SuppressWarnings("unchecked")
        private ObservableList<ExplorerTreeItem<T>> buildChildren(final ExplorerTreeItem<T> treeItem) {
            final var fileStr = treeItem.fullName;
            final var file = new File(fileStr);
            if (!file.exists())
                return FXCollections.emptyObservableList();
            final var image = getIconImage(file);
            if (image != null) {
                treeItem.setGraphic(new ImageView(image));
            }

            setValue((T) this.actualName);
            if (file.isDirectory()) {
                final List<File> directoryList = Arrays
                        .asList(file.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY));
                final List<File> fileList = Stream.of(file.listFiles()).filter(File::isFile)
                        .collect(Collectors.toList());

                fileList.addAll(0, directoryList);

                final T[] files = (T[]) fileList.stream().map(File::getPath).toList().toArray(new String[0]);

                if (files != null) {
                    final ObservableList<ExplorerTreeItem<T>> children = FXCollections.observableArrayList();
                    for (final T childFile : files) {
                        children.add(createNode(childFile));
                    }

                    return children;
                }
            }

            return FXCollections.emptyObservableList();
        }
    }

    public static class FolderFileComparator implements Comparator<File> {

        @Override
        public int compare(final File o1, final File o2) {
            return o1.isDirectory() ? -1 : 1;
        }
    }
}
