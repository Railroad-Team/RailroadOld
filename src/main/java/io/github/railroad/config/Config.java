package io.github.railroad.config;

import static io.github.railroad.utility.Gsons.WRITING_GSON;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.annotations.Expose;

import io.github.railroad.Railroad;

/**
 * Class for creating configs (json)
 * 
 * <h2>Creating a config</h2>
 * In order to create a config we will create a class extending this class. In that 
 * class we will override the {@link #reset} and {@link #getName} methods.<br>
 * 
 * 1. In the {@link #getName} method we will return the file name of our config
 * <strong>without .json at the end</strong>. <br>
 * 2. We will create the fields of our config by annotating any field in the class we created
 * with {@link Expose}. As the javadoc says, anything annotated with Expose will be included
 * in the json file.<br>
 * 3. In the {@link #reset} method we will reset our config. This field will generate the default
 * file when {@link #generateConfig} is called. <br><br>
 * 
 * <h2>How to register a config</h2>
 * The configs are registered in in {@link JsonConfigs} as it follows: <br>
 *
 * 1. Create a static (NOT FINAL) field in {@link JsonConfigs} representing the config <br>
 * 
 * 2. Register the config in the {@link JsonConfigs#register} like this:
 * <pre>
 * 		<code>
 * randomConfig = (RandomConfig) new RandomConfig.readConfig();
 * 		</code>
 * </pre>
 * 
 * As it can be seen above the constructor of any config class should be empty. <br>
 * 
 * Now the config can be referenced from anywhere through the static field from {@link JsonConfigs} <br>
 * 
 * 
 * <h2>Modifying the config during runtime</h2>
 * In order to modify the config file during runtime, there should be a method that 
 * replaces the content of one of the config fields (anything annotated with {@link Expose}).
 * After the field is modified the config should be written to the disk using {@link #writeConfig()}
 * 
 * 
 * <br>
 * @author matyrobbrt
 *
 */
public abstract class Config {

	protected String root = Railroad.RAILROAD_CONFIG_FOLDER;
	protected String extension = ".json";

	/**
	 * Resets and generated the configs. Here 2 things can happen: <br>
	 * 
	 * 1. the Json is reset (if it already exists) <br>
	 * 2. the Json is made, with the default values (from {@link #reset}) (if it doesn't exist)
	 */
	public void generateConfig() {
		this.reset();

		try {
			this.writeConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the actual config {@link File}
	 * @return
	 */
	private File getConfigFile() {
		return new File(this.root + this.getName() + this.extension);
	}

	/**
	 * The name of the config (file name)<br>
	 * <strong>IMPORTANT:</strong> do not add .json to the name as it already exists
	 * @return
	 */
	public abstract String getName();

	/**
	 * Used for registering configs. Reads and caches the config values
	 * @return
	 */
	public Config readConfig() {
		try {
			return WRITING_GSON.fromJson(new FileReader(this.getConfigFile()), this.getClass());
		} catch (FileNotFoundException e) {
			this.generateConfig();
		}

		return this;
	}

	/**
	 * Resets the config (this method will be called by {@link #generateConfig}
	 */
	protected abstract void reset();

	/**
	 * Writes the config with the current values
	 * @throws IOException
	 */
	public void writeConfig() throws IOException {
		File dir = new File(this.root);
		if (!dir.exists() && !dir.mkdirs())
			return;
		if (!this.getConfigFile().exists() && !this.getConfigFile().createNewFile())
			return;
		FileWriter writer = new FileWriter(this.getConfigFile());
		WRITING_GSON.toJson(this, writer);
		writer.flush();
		writer.close();
	}

}
