package io.github.railroad.project.settings.theme;

import java.awt.Color;

/**
 * Class for creating a theme to be used in colour formatting
 *
 * @author matyrobbrt
 */
public class Theme {
    private String name;
    private Color buttonColour, backgroundColour, textColour;

    public Theme(String name) {
        this.name = name;
    }

    public Color getBackgroundColor() {
        return this.backgroundColour;
    }

    public Color getButtonColor() {
        return this.buttonColour;
    }

    public String getName() {
        return this.name;
    }

    public Color getTextColor() {
        return this.textColour;
    }

    /**
     * Checks if the theme is dark
     *
     * @return if the theme is equal to {@link Themes#DARK_THEME}
     */
    public boolean isDarkTheme() {
        return this == Themes.DARK_THEME;
    }

    public Theme withBackgroundColor(Color backgroundColour) {
        this.backgroundColour = backgroundColour;
        return this;
    }

    /**
     * The colour of buttons
     *
     * @param  buttonColour
     * @return
     */
    public Theme withButtonColor(Color buttonColour) {
        this.buttonColour = buttonColour;
        return this;
    }

    public Theme withTextColor(Color textColour) {
        this.textColour = textColour;
        return this;
    }
}
