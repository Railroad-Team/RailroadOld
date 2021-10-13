package io.github.railroad.utility;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public final class ImageUtility {

    /**
     * Gets the icon image that is to be displayed for the file that is converted
     * from the swing {@link Icon}.
     *
     * @param file - The file to get the icon for.
     * @return The corresponding icon image.
     */
    public static Image getIconImage(final File file) {
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
}
