package io.github.railroad;

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import io.github.railroad.config.JsonConfigs;
import io.github.railroad.project.Project;
import io.github.railroad.project.lang.LangProvider;
import io.github.railroad.project.settings.theme.Themes;
import io.github.railroad.utility.WindowTools;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

/**
 * @author TurtyWurty
 */

// TODO: Confirmation prompt when closing the IDE
public class Railroad extends Application {
    public static final String RAILROAD_CONFIG_FOLDER = System.getProperty("user.home") + "/.railroad/";
    
    private static Project project;
    private DiscordEventHandlers discordHandlers;
    private DiscordRichPresence discordRichPresence;
    
    private IDESetup IDESetup;
    
    @Override
    public void start(final Stage primaryStage) throws Exception {
        SvgImageLoaderFactory.install();
        LangProvider.cacheLang("en_us");
        JsonConfigs.register();

        project = new Project(Themes.DARK_THEME);
        project.onReady(() -> {
            this.IDESetup = new IDESetup(project, "en_us");
            final var scene = new Scene(this.IDESetup.mainPane);
            scene.getStylesheets().add(Railroad.class.getResource("/default.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Railroad IDE");
            primaryStage.getIcons().add(new Image(Railroad.class.getResourceAsStream("/logo.png")));
            primaryStage.setWidth(this.IDESetup.primaryScreenBounds.getWidth());
            primaryStage.setHeight(this.IDESetup.primaryScreenBounds.getHeight());
            primaryStage.centerOnScreen();
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                WindowTools.displayQuitWindow(primaryStage, project.getTheme());
            });
            primaryStage.show();

            scene.setOnKeyPressed(this.IDESetup::handleKeyPress);

            setupDiscord(project);
        });
    }
    
    @Override
    public void stop() {
        this.IDESetup.codeEditor.executor.shutdown();
        this.IDESetup.liveDirs.dispose();
        DiscordRPC.discordShutdown();
    }
    
    /**
     * Sets up the Discord Rich Presence <br>
     */
    private void setupDiscord(Project project) {
        this.discordHandlers = new DiscordEventHandlers.Builder()
            .setReadyEventHandler(user -> System.out.println(user.username + "#" + user.discriminator)).build();
        DiscordRPC.discordInitialize("853387211897700394", this.discordHandlers, true);
        DiscordRPC.discordRunCallbacks();
        this.discordRichPresence = new DiscordRichPresence.Builder("Working on " + project.getProjectName())
            .setDetails("Making an amazing mod!").setBigImage("logo", "Railroad IDE")
            .setSmallImage("logo", "An IDE built for modders, made by modders.").setParty("", 0, 0)
            .setStartTimestamps(System.currentTimeMillis()).build();
        DiscordRPC.discordUpdatePresence(this.discordRichPresence);
    }
    
    public static void resetDiscordPresence() {
        if (project != null) {
            final DiscordRichPresence newPresence = new DiscordRichPresence.Builder(
                "Working on " + project.getProjectName()).setDetails("Making an amazing mod!")
                    .setBigImage("logo", "Railroad IDE")
                    .setSmallImage("logo", "An IDE built for modders, made by modders.").setParty("", 0, 0)
                    .setStartTimestamps(System.currentTimeMillis()).build();
            DiscordRPC.discordUpdatePresence(newPresence);
        }
    }
}
