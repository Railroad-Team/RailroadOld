package io.github.railroad.utility.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.railroad.utility.Gsons;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.XML;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class MappingHelper {
    private static final String YARN_MAVEN = "https://maven.fabricmc.net/net/fabricmc/yarn/maven-metadata.xml";
    private static final String PARCHMENT_MAVEN = "https://ldtteam.jfrog.io/artifactory/parchmentmc-public/org/parchmentmc/data/parchment-%s/maven-metadata.xml";

    public static Map<String, Pair<String, Optional<String>>> getMCPVersions() {
        final Map<String, Pair<String, Optional<String>>> versions = new HashMap<>();

        try {
            Path path = Paths.get("src/main/resources", "mcp_mappings.json");
            System.out.println(path.toAbsolutePath());
            String content = Files.readString(path, StandardCharsets.UTF_8);
            final JsonObject response = Gsons.READING_GSON.fromJson(content, JsonObject.class);
            final JsonArray versionsArray = response.getAsJsonArray("versions");
            for (final JsonElement element : versionsArray) {
                final JsonObject versionObj = element.getAsJsonObject();
                String version = versionObj.get("version").getAsString();
                String snapshot = versionObj.get("snapshot").getAsString();
                String stable = versionObj.has("stable") ? versionObj.get("stable").getAsString() : null;
                versions.put(version, new Pair<>(snapshot, Optional.ofNullable(stable)));
            }
        } catch (final IOException exception) {
            throw new IllegalStateException("Unable to read MCP versions!", exception);
        }

        return versions;
    }

    public static Collection<String> getMCPVersions(String minecraftVersion) {
        Map<String, Pair<String, Optional<String>>> versions = getMCPVersions();
        List<String> results = new ArrayList<>();

        if (versions.containsKey(minecraftVersion)) {
            Pair<String, Optional<String>> pair = versions.get(minecraftVersion);
            results.add("snapshot-" + pair.getKey());
            pair.getValue().ifPresent(stable -> results.add("stable-" + stable));
        } else {
            for (Map.Entry<String, Pair<String, Optional<String>>> entry : versions.entrySet()) {
                String version = entry.getKey();
                if (version.endsWith("*")) {
                    version = version.substring(0, version.length() - 1);
                    if (minecraftVersion.startsWith(version)) {
                        Pair<String, Optional<String>> pair = entry.getValue();
                        results.add("snapshot-" + pair.getKey());
                        pair.getValue().ifPresent(stable -> results.add("stable-" + stable));
                    } else if (minecraftVersion.startsWith(version.substring(0, version.length() - 1))) {
                        Pair<String, Optional<String>> pair = entry.getValue();
                        results.add("snapshot-" + pair.getKey());
                        pair.getValue().ifPresent(stable -> results.add("stable-" + stable));
                    }
                }
            }
        }

        return results;
    }

    public static Map<String, Collection<String>> getYarnVersions() {
        final Map<String, Collection<String>> versions = new HashMap<>();

        try {
            final URLConnection connection = new URL(YARN_MAVEN).openConnection();
            final String xmlJsonStr = XML.toJSONObject(
                    IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8)).toString(1);
            final JsonObject xmlJson = Gsons.READING_GSON.fromJson(xmlJsonStr, JsonObject.class);
            final JsonObject versioning = xmlJson.getAsJsonObject("metadata").getAsJsonObject("versioning");
            final JsonArray versionsArray = versioning.getAsJsonObject("versions").getAsJsonArray("version");

            for (final JsonElement element : versionsArray) {
                final String version = element.getAsString();
                if (Pattern.matches("\\d+w\\d+\\w\\.\\d+", version)) continue;

                if (!Pattern.matches("\\d+\\.\\d+(\\.\\d+)*\\+build\\.\\d+", version)) continue;

                String mcVersion = version.substring(0, version.indexOf('+'));
                if (!versions.containsKey(mcVersion)) {
                    versions.put(mcVersion, new ArrayList<>());
                }

                versions.get(mcVersion).add(version);
            }
        } catch (final IOException exception) {
            throw new IllegalStateException("Unable to read Yarn versions!", exception);
        }

        Map<String, Collection<String>> sortedVersions = new HashMap<>();
        versions.forEach(
                (key, value) -> sortedVersions.put(key, value.stream().sorted(Comparator.reverseOrder()).toList()));
        return sortedVersions;
    }

    public static Collection<String> getYarnVersions(String minecraftVersion) {
        Map<String, Collection<String>> versions = getYarnVersions();

        List<String> results = new ArrayList<>();

        if (versions.containsKey(minecraftVersion)) {
            results.addAll(versions.get(minecraftVersion));
        }

        return results;
    }

    public static Collection<String> getParchmentVersions(String minecraftVersion) {
        List<String> results = new ArrayList<>();
        try {
            String url = PARCHMENT_MAVEN.formatted(minecraftVersion);
            var file = new File(minecraftVersion + "-parchment.xml");
            if (!file.exists()) {
                FileUtils.copyURLToFile(new URL(url), file);
            }

            // TODO: Figure out wtf is happening here
            final String xmlJsonStr = XML.toJSONObject(Files.readString(file.toPath(), StandardCharsets.UTF_8))
                    .toString(1);
            final JsonObject xmlJson = Gsons.READING_GSON.fromJson(xmlJsonStr, JsonObject.class);
            final JsonObject versioning = xmlJson.getAsJsonObject("metadata").getAsJsonObject("versioning");
            final JsonArray versionsArray = versioning.getAsJsonObject("versions").getAsJsonArray("version");
            for (final JsonElement element : versionsArray) {
                final String version = element.getAsString();
                if (!Pattern.matches("\\d+\\.\\d+(\\.\\d+)?", version)) continue;

                results.add(version);
            }

        } catch (final IOException exception) {
            throw new IllegalStateException("Unable to read parchment versions!", exception);
        }

        return results;
    }

    public static void loadMappings(ObservableList<String> options, String mcVersion) {
        options.clear();

        final String[] parts = mcVersion.split("\\.");
        try {
            final int minor = Integer.parseInt(parts[1]);
            if (minor >= 14) {
                options.add("Yarn");
            }

            if (minor > 16) {
                options.addAll("Mojmap", "Parchment");
            } else if (minor < 16) {
                options.add("MCP");
            } else {
                final int subMinor = Integer.parseInt(parts[2]);
                if (subMinor == 5) {
                    options.addAll("MCP", "Mojmap", "Parchment");
                } else {
                    options.add("MCP");
                }
            }
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            throw new IllegalStateException("There was an error calculating Mappings Channels!", exception);
        }
    }

    public static void loadMappingsVersions(ObservableList<String> options, String mcVersion, String mappingsChannel) {
        options.clear();

        if ("MCP".equalsIgnoreCase(mappingsChannel)) {
            options.addAll(getMCPVersions(mcVersion));
        } else if ("Mojmap".equalsIgnoreCase(mappingsChannel)) {
            options.add(mcVersion);
        } else if ("Parchment".equals(mappingsChannel)) {
            options.addAll(getParchmentVersions(mcVersion));
        } else if ("Yarn".equalsIgnoreCase(mappingsChannel)) {
            options.addAll(getYarnVersions(mcVersion));
        }
    }
}
