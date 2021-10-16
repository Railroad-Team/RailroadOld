package io.github.railroad.project.settings.theme;

import static io.github.railroad.utility.helper.ColorHelper.fxColourToAwt;

import static javafx.scene.paint.Color.*;

/**
 * A list of default themes
 * 
 * @author matyrobbrt
 *
 */
public class Themes {

	public static final Theme DARK_THEME = new Theme("DarkMode").withButtonColor(fxColourToAwt(DARKGRAY))
			.withBackgroundColor(fxColourToAwt(DIMGRAY))
			.withTextColor(fxColourToAwt(ALICEBLUE));

	public static final Theme WHITE_THEME = new Theme("WhiteMode").withButtonColor(fxColourToAwt(ANTIQUEWHITE))
			.withBackgroundColor(fxColourToAwt(FLORALWHITE))
			.withTextColor(fxColourToAwt(BLACK));
}
