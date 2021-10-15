package io.github.railroad.config.entry;

import com.google.gson.annotations.Expose;

public class ProjectSettingsEntry {

	@Expose
	public String projectPath;
	
	public ProjectSettingsEntry(String projectPath) {
		this.projectPath = projectPath;
	}
	
}
