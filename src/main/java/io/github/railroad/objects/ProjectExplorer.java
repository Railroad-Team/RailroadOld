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

/**
 * @author TurtyWurty
 */
public class ProjectExplorer {

	public static class ExplorerTreeItem extends TreeItem<String> {
		/**
		 * Gets the icon image that is to be displayed for the file that is converted
		 * from the swing {@link Icon}.
		 *
		 * @param file - The file to get the icon for.
		 * @return The corresponding icon image.
		 */
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

		/**
		 * Gets the icon image that is to be displayed for the file.
		 *
		 * @param file - The file to get the icon for.
		 * @return The corresponding icon image.
		 */
		private static Icon getLargeIcon(final File file) {
			if (file != null)
				return FileSystemView.getFileSystemView().getSystemIcon(file);
			return null;
		}

		private boolean isLeaf;
		private boolean isFirstTimeChildren = true;
		private boolean isFirstTimeLeaf = true;

		private final String fullName, actualName;

		/**
		 * Creates the {@link TreeItem} that is used in the {@link TreeView}.
		 *
		 * @param fullName   - The file path to this item.
		 * @param actualName - The displayed name of this item.
		 */
		public ExplorerTreeItem(final String fullName, final String actualName) {
			this.fullName = fullName;
			this.actualName = actualName;
			setValue(this.actualName);
		}

		/**
		 * Creates all of the child nodes from this node and displays them.
		 *
		 * @param treeItem - The item to build the children off of.
		 * @return A list of the children.
		 */
		private ObservableList<ExplorerTreeItem> buildChildren(final ExplorerTreeItem treeItem) {
			final var fileStr = treeItem.fullName;
			final var file = new File(fileStr);
			if (!file.exists())
				return FXCollections.emptyObservableList();
			final var image = getIconImage(file);
			if (image != null) {
				treeItem.setGraphic(new ImageView(image));
			}

			setValue(this.actualName);
			if (file.isDirectory()) {
				final List<File> directoryList = Arrays.asList(file.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY));
				final List<File> fileList = Stream.of(file.listFiles()).filter(File::isFile).toList();

				fileList.addAll(0, directoryList);

				final String[] files = fileList.stream().map(File::getPath).toList().toArray(new String[0]);

				if (files != null) {
					final ObservableList<ExplorerTreeItem> children = FXCollections.observableArrayList();
					for (final String childFile : files) {
						children.add(createNode(childFile));
					}

					return children;
				}
			}

			return FXCollections.emptyObservableList();
		}

		/**
		 * @return The name to be displayed for this item
		 */
		public String getActualName() {
			return this.actualName;
		}

		/**
		 * @return The list of children from this item.
		 */
		@Override
		public ObservableList<TreeItem<String>> getChildren() {
			if (this.isFirstTimeChildren) {
				this.isFirstTimeChildren = false;

				// First getChildren() call, so we actually go off and
				// determine the children of the File contained in this ExplorerTreeItem.
				super.getChildren().setAll(buildChildren(this));
			}
			return super.getChildren();
		}

		/**
		 * @return The file path to this item.
		 */
		public String getFullName() {
			return this.fullName;
		}

		/**
		 * @return Whether or not this item is at the end of a branch; For example if it
		 *         is a file or if it is an empty directory.
		 */
		@Override
		public boolean isLeaf() {
			setValue(this.actualName);
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
	}

	/**
	 * The Comparator that is used to sort the files and folders to start with
	 * folders first.
	 */
	public static class FolderFileComparator implements Comparator<File> {

		@Override
		public int compare(final File o1, final File o2) {
			return o1.isDirectory() ? -1 : 1;
		}
	}

	/**
	 * @param rootFolder - The folder to create the node for.
	 * @return The created node.
	 */
	private static ExplorerTreeItem createNode(final String rootFolder) {
		return new ExplorerTreeItem(rootFolder, new File(rootFolder).getName());
	}

	/**
	 * @param project - The project to obtain the starting directory from.
	 * @return The {@link TreeView} that is used to represent the
	 *         {@link ProjectExplorer}.
	 */
	public TreeView<String> createProjectExplorer(final Project project) {
		final var root = createNode(project.getProjectFolder().getPath());
		root.setExpanded(true);
		return new TreeView<>(root);
	}
}
