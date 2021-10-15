package io.github.railroad.util.helper;

import javafx.scene.paint.Color;

/**
 * 
 * @author matyrobbrt
 *
 */
public class ColorHelper {

	/**
	 * Converts a {@link javafx.scene.paint.Color} to a {@link java.awt.Color}
	 * @param fx the fx colour
	 * @return the awt colour
	 */
	public static java.awt.Color fxColourToAwt(Color fx) {
		return new java.awt.Color((float) fx.getRed(), (float) fx.getGreen(), (float) fx.getBlue(),
				(float) fx.getOpacity());
	}
	
	/**
	 * Converts a {@link java.awt.Color} to a HEX value
	 * @param awt the colour to convert
	 * @return
	 */
	public static String toHex(java.awt.Color awt) {
		String buf = Integer.toHexString(awt.getRGB());
		return "#"+buf.substring(buf.length()-6);
	}

}
