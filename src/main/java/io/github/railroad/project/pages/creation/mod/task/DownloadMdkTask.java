package io.github.railroad.project.pages.creation.mod.task;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.railroad.project.pages.creation.mod.ForgeModProject;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DownloadMdkTask extends Task {
    private static final String MDK_URL = "https://maven.minecraftforge.net/net/minecraftforge/forge/%s-%s/forge-%s-%s-mdk.zip";

    private final String minecraftVersion;
    private final String forgeVersion;
    private final MFXProgressSpinner progressSpinner;

    private String mdkURL;
    private BigInteger downloadSize;

    public DownloadMdkTask(String minecraftVersion, String forgeVersion, MFXProgressSpinner progressSpinner) {
        super("Download MDK", "Downloading MDK...");

        this.minecraftVersion = minecraftVersion;
        this.forgeVersion = forgeVersion;
        this.progressSpinner = progressSpinner;

        this.mdkURL = getMDKUrl();
    }

    @Override
    public Collection<BiDirectionalRunnable> getProcesses() {
        List<BiDirectionalRunnable> processes = new ArrayList<>();
        processes.add(new BiDirectionalRunnable(this::getDownloadSize, this::resetDownloadSize));
        processes.add(new BiDirectionalRunnable(this::downloadMDK, this::deleteMDK));
        return processes;
    }

    private String getMDKUrl() {
        return MDK_URL.formatted(this.minecraftVersion, this.forgeVersion, this.minecraftVersion, this.forgeVersion);
    }

    private BigInteger getDownloadSize() {
        try {
            URLConnection connection = new URL(this.mdkURL).openConnection();
            return new BigInteger(connection.getHeaderField("Content-Length"));
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
            return BigInteger.ZERO;
        }
    }

    private void resetDownloadSize() {
        this.downloadSize = BigInteger.ZERO;
    }

    private void downloadMDK() {
        try {
            AsyncHttpClient asyncHttpClient = Dsl.asyncHttpClient();
            asyncHttpClient.prepareGet(this.mdkURL).execute(
                    new ForgeModProject.Page3.MdkDownloadHandler(new FileOutputStream("../forge.zip"),
                            this.downloadSize, this.progressSpinner));
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void deleteMDK() {
        try {
            Path path = Paths.get("../forge.zip");
            if (Files.exists(path)) Files.delete(path);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }
}
