package io.github.railroad.project.settings.theme;

import java.awt.Color;

/**
 * Class for creating a theme to be used in colour formatting
 * 
 * @author matyrobbrt
 *
 */
public class Theme {

	private String name;
	private Color buttonColour;

	public Theme(String name) {
		this.name = name;
	}

	/**
	 * The colour of buttons
	 * 
	 * @param buttonColour
	 * @return
	 */
	public Theme withButtonColor(Color buttonColour) {
		this.buttonColour = buttonColour;
		return this;
	}

	public Color getButtonColor() {
		return buttonColour;
	}

	public String getName() {
		return name;
	}

	/**
	 * Checks if the theme is dark
	 * 
	 * @return if the theme is equal to {@link Themes#DARK_THEME}
	 */
	public boolean isDarkTheme() {
		return this == Themes.DARK_THEME;
	}

}