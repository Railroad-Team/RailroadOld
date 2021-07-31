package io.github.railroad;

import io.github.railroad.utility.WindowTools;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class Railroad extends Application {

	public DiscordEventHandlers discordHandlers;
	public DiscordRichPresence discordRichPresense;
	private Setup setup;

	private void setupDiscord() {
		this.discordHandlers = new DiscordEventHandlers.Builder()
				.setReadyEventHandler(user -> System.out.println(user.username + "#" + user.discriminator)).build();
		DiscordRPC.discordInitialize("853387211897700394", this.discordHandlers, true);
		DiscordRPC.discordRunCallbacks();
		this.discordRichPresense = new DiscordRichPresence.Builder("Working on Untitled Project")
				.setDetails("Making an amazing mod!").setBigImage("logo", "Railroad IDE")
				.setSmallImage("logo", "An IDE built for modders, made by modders.").setParty("", 0, 0)
				.setStartTimestamps(System.currentTimeMillis()).build();
		DiscordRPC.discordUpdatePresence(this.discordRichPresense);
	}

	// Test
	@Override
	public void start(final Stage primaryStage) throws Exception {
		this.setup = new Setup(true);

		final var scene = new Scene(this.setup.mainPane);
		scene.getStylesheets().add(Railroad.class.getResource("/default.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("Railroad IDE");
		primaryStage.setWidth(this.setup.primaryScreenBounds.getWidth());
		primaryStage.setHeight(this.setup.primaryScreenBounds.getHeight());
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(event -> {
			event.consume();
			WindowTools.displayQuitWindow(primaryStage);
		});
	}

	@Override
	public void stop() {
		this.setup.codeEditor.executor.shutdown();
		DiscordRPC.discordShutdown();
	}
}
