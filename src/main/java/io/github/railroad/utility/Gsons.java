package io.github.railroad.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Gsons {

    public static final Gson WRITING_GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting()
            .create();

    public static final Gson READING_GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static final Gson JSON_CREATING_GSON = new GsonBuilder().setPrettyPrinting().create();

    private Gsons() {
    }

}
