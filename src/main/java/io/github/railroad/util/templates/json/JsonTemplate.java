package io.github.railroad.util.templates.json;

import java.io.File;

import io.github.railroad.project.Project;
import io.github.railroad.project.lang.LangProvider;

/**
 * Class used for creating JsonTemplates for the json generator
 * @author matyrobbrt
 *
 */
public abstract class JsonTemplate {
	
	public abstract void openWindow(Project project);
	
	public void writeDirectory(File dir) {
		if (!dir.exists())
			dir.mkdirs();
	}
	
	public String getLang(String key) {
		return LangProvider.fromLang("jsonGenerator." + key);
	}
}
