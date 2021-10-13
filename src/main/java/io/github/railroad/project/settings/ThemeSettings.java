package io.github.railroad.project.settings;

/**
 * @author TurtyWurty
 */
public class ThemeSettings {

    private boolean darkMode = true;

    public ThemeSettings() {
    }

    /**
     * @param isDarkMode - Whether or not to use dark mode.
     */
    public ThemeSettings(final boolean isDarkMode) {
        this.darkMode = isDarkMode;
    }

    /**
     * @return Whether or not the project is using dark mode.
     */
    public boolean isDarkMode() {
        return this.darkMode;
    }

    /**
     * @param darkMode - Whether or not to use dark mode.
     */
    public void setDarkMode(final boolean darkMode) {
        this.darkMode = darkMode;
        // TODO: Update stylesheet
    }
}
