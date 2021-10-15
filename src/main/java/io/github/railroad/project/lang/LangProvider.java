package io.github.railroad.project.lang;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import io.github.railroad.Railroad;

/**
 * Lang Provider for Railroad
 * @author matyrobbrt
 *
 */
public class LangProvider {

	private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

	private static JsonObject SELECTED_LANG;

	private static JsonObject EN_US;

	public static void cacheLang(String langauge) {
		BufferedReader bufferedReaderSelected = null;
		BufferedReader bufferedReaderEnUs = null;
		try {
			bufferedReaderSelected = new BufferedReader(
					new FileReader(Railroad.class.getResource("/lang/" + langauge + ".json").getPath().toString()));
			JsonObject finalDataSelected = GSON.fromJson(bufferedReaderSelected, JsonObject.class);
			SELECTED_LANG = finalDataSelected;

			if (!langauge.equalsIgnoreCase("en_us")) {
				bufferedReaderEnUs = new BufferedReader(
						new FileReader(Railroad.class.getResource("/lang/en_us.json").getPath().toString()));
				JsonObject finalDataEnUs = GSON.fromJson(bufferedReaderEnUs, JsonObject.class);
				EN_US = finalDataEnUs;
			} else
				EN_US = finalDataSelected;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve a lang entry from the cached lang (or if it doesn't exist from the <b>en_us</b>
	 * @param translationKey
	 * @return
	 */
	public static String fromLang(String translationKey) {
		if (SELECTED_LANG != null && SELECTED_LANG.has(translationKey))
			return SELECTED_LANG.get(translationKey).getAsString();
		else if (EN_US.has(translationKey))
			return EN_US.get(translationKey).getAsString();
		return translationKey;
	}

}
