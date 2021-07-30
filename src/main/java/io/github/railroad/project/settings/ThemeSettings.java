package io.github.railroad.project.settings;

public class ThemeSettings {

	private boolean darkMode = true;

	public ThemeSettings() {
	}

	public ThemeSettings(final boolean isDarkMode) {
		this.darkMode = isDarkMode;
	}

	public boolean isDarkMode() {
		return this.darkMode;
	}

	public void setDarkMode(final boolean darkMode) {
		this.darkMode = darkMode;
		// TODO: Update stylesheet
	}
}
