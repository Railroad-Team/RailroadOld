package io.github.railroad;

import io.github.railroad.project.settings.theme.Themes;
import io.github.railroad.utility.WindowTools;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

/**
 * @author TurtyWurty
 */
public class Railroad extends Application {

    public DiscordEventHandlers discordHandlers;
    public DiscordRichPresence discordRichPresence;
    private Setup setup;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.setup = new Setup(Themes.DARK_THEME, "en_us");

        setupDiscord();

        final var scene = new Scene(this.setup.mainPane);
        scene.getStylesheets().add(Railroad.class.getResource("/default.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Railroad IDE");
        primaryStage.setWidth(this.setup.primaryScreenBounds.getWidth());
        primaryStage.setHeight(this.setup.primaryScreenBounds.getHeight());
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            WindowTools.displayQuitWindow(primaryStage);
        });
        primaryStage.show();
    }

    @Override
    public void stop() {
        this.setup.codeEditor.executor.shutdown();
        this.setup.liveDirs.dispose();
        DiscordRPC.discordShutdown();
    }

    /**
     * Sets up the Discord Rich Presense <br>
     * <br>
     * TODO: Update presense for project and current file name.
     */
    private void setupDiscord() {
        this.discordHandlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(user -> System.out.println(user.username + "#" + user.discriminator))
                .build();
        DiscordRPC.discordInitialize("853387211897700394", this.discordHandlers, true);
        DiscordRPC.discordRunCallbacks();
        this.discordRichPresence = new DiscordRichPresence.Builder("Working on Untitled Project")
                .setDetails("Making an amazing mod!").setBigImage("logo", "Railroad IDE")
                .setSmallImage("logo", "An IDE built for modders, made by modders.").setParty("", 0, 0)
                .setStartTimestamps(System.currentTimeMillis()).build();
        DiscordRPC.discordUpdatePresence(this.discordRichPresence);
    }
}
