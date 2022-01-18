package io.github.railroad.utility.helper;

import javafx.scene.paint.Color;

/**
 * @author matyrobbrt
 */
public class ColorHelper {

    private ColorHelper() {
        throw new IllegalAccessError("Attempted to construct utility class!");
    }

    /**
     * Converts a {@link java.awt.Color} to a {@link Color}
     *
     * @param awt the awt colour
     * @return the fx colour
     */
    public static Color awtColourToFx(java.awt.Color awt) {
        return Color.rgb(awt.getRed(), awt.getGreen(), awt.getBlue(), awt.getAlpha() / 255.0);
    }

    /**
     * Converts a {@link javafx.scene.paint.Color} to a {@link java.awt.Color}
     *
     * @param fx the fx colour
     * @return the awt colour
     */
    public static java.awt.Color fxColourToAwt(Color fx) {
        return new java.awt.Color((float) fx.getRed(), (float) fx.getGreen(), (float) fx.getBlue(),
                (float) fx.getOpacity());
    }

    /**
     * Converts a {@link java.awt.Color} to a HEX value
     *
     * @param awt the colour to convert
     * @return
     */
    public static String toHex(java.awt.Color awt) {
        String buf = Integer.toHexString(awt.getRGB());
        return "#" + buf.substring(buf.length() - 6);
    }

}
