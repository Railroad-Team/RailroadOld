package io.github.railroad.project.pages.creation.mod.forge.tasks;

import io.github.railroad.project.task.Task;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class DownloadMdkStep extends Task.Step {
    public DownloadMdkStep(Supplier<String> minecraftVersion, Supplier<String> forgeVersion, Supplier<Path> directory) {
        super("Downloading MDK", 100, () -> {
            try {
                final String mdkUrl = String.format(
                        "https://maven.minecraftforge.net/net/minecraftforge/forge/%s-%s/forge-%s-%s-mdk.zip",
                        minecraftVersion.get(), forgeVersion.get(), minecraftVersion.get(), forgeVersion.get());

                Path path = directory.get();
                if(path == null)
                    throw new IOException("Path is provided is invalid!");

                if (Files.notExists(path))
                    Files.createDirectories(path);

                final Path mdk = path.resolve("mdk.zip");
                final URLConnection connection = new URL(mdkUrl).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();

                Files.copy(connection.getInputStream(), mdk);
            } catch (final IOException exception) {
                throw new IllegalStateException("Unable to download MDK!", exception);
            }
        }, () -> {
            try {
                final Path path = directory.get();
                if(path == null)
                    throw new IOException("Path is provided is invalid!");

                if (Files.exists(path))
                    Files.delete(path);
            } catch (final IOException exception) {
                throw new IllegalStateException("Unable to delete MDK!", exception);
            }
        });
    }
}
