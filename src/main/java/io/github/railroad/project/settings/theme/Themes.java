package io.github.railroad.project.settings.theme;

import io.github.railroad.util.helper.ColorHelper;

/**
 * A list of default themes
 * @author matyrobbrt
 *
 */
public class Themes {
	
	public static final Theme DARK_THEME = new Theme("DarkMode").withButtonColor(ColorHelper.fxColourToAwt(javafx.scene.paint.Color.DARKGRAY));
	public static final Theme WHITE_THEME = new Theme("WhiteMode");
}
