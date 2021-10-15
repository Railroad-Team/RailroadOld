package io.github.railroad.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import io.github.railroad.Railroad;
import io.github.railroad.config.entry.ProjectSettingsEntry;

public class RailroadConfigJson {

	private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting()
			.create();
	public static final String CONFIG_PATH = Railroad.RAILROAD_CONFIG_FOLDER + "railroad-config.json";

	@Expose
	private ProjectSettingsEntry projectSettings;

	public RailroadConfigJson(ProjectSettingsEntry projectSettings) {
		this.projectSettings = projectSettings;
	}

	public ProjectSettingsEntry getProjectSettings() {
		return this.projectSettings;
	}

	public void writeConfig(RailroadConfigJson config) {
		File dir = new File(CONFIG_PATH);
		if (!dir.exists() && !dir.mkdirs())
			return;
		try (FileWriter writer = new FileWriter(new File(CONFIG_PATH));) {
			GSON.toJson(config, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public RailroadConfigJson getConfig() {
		File dir = new File(CONFIG_PATH);
		if (!dir.exists() && !dir.mkdirs())
			return null;
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(CONFIG_PATH)));) {
			RailroadConfigJson finalData = GSON.fromJson(bufferedReader, RailroadConfigJson.class);
			return finalData;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
