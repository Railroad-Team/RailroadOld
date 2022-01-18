package io.github.railroad.project.lang;

import static io.github.railroad.utility.Gsons.READING_GSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.JsonObject;

import io.github.railroad.Railroad;

/**
 * Lang Provider for Railroad
 *
 * @author matyrobbrt
 */
public class LangProvider {
    private static JsonObject selectedLang;
    private static JsonObject enUs;
    
    private LangProvider() {
    }
    
    /**
     * <strong>Only run at startup</strong>
     *
     * @param langauge
     */
    public static void cacheLang(String langauge) {
        BufferedReader bufferedReaderSelected = null;
        BufferedReader bufferedReaderEnUs = null;
        try {
            bufferedReaderSelected = new BufferedReader(
                    new InputStreamReader(Railroad.class.getResource("/lang/" + langauge + ".json").openStream()));
            final JsonObject finalDataSelected = READING_GSON.fromJson(bufferedReaderSelected, JsonObject.class);
            selectedLang = finalDataSelected;
            
            if (!"en_us".equalsIgnoreCase(langauge)) {
                bufferedReaderEnUs = new BufferedReader(
                        new InputStreamReader(Railroad.class.getResource("/lang/en_us.json").openStream()));
                enUs = READING_GSON.fromJson(bufferedReaderEnUs, JsonObject.class);
            } else {
                enUs = finalDataSelected;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieve a lang entry from the cached lang (or if it doesn't exist from the
     * <b>en_us</b>
     *
     * @param translationKey
     * @return
     */
    public static String fromLang(String translationKey) {
        if (selectedLang != null && selectedLang.has(translationKey))
            return selectedLang.get(translationKey).getAsString();
        if (enUs.has(translationKey))
            return enUs.get(translationKey).getAsString();
        return translationKey;
    }
    
}
