package io.github.railroad.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import io.github.railroad.objects.RailroadCodeArea;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class SimpleFileEditorController {

	private File loadedFileReference;

	private FileTime lastModifiedTime;
	// public Label statusMessage;
	// public ProgressBar progressBar;
	// public Button loadChangesButton;
	public RailroadCodeArea textArea;

	private ScheduledService<Boolean> createFileChangesCheckingService(final File file) {
		final ScheduledService<Boolean> scheduledService = new ScheduledService<>() {
			@Override
			protected Task<Boolean> createTask() {
				return new Task<>() {
					@Override
					protected Boolean call() throws Exception {
						final FileTime lastModifiedAsOfNow = Files.readAttributes(file.toPath(), BasicFileAttributes.class)
								.lastModifiedTime();
						return lastModifiedAsOfNow.compareTo(SimpleFileEditorController.this.lastModifiedTime) > 0;
					}
				};
			}
		};
		scheduledService.setPeriod(Duration.seconds(1));
		return scheduledService;
	}

	private Task<String> fileLoaderTask(final File fileToLoad) {
		// Create a task to load the file asynchronously
		final Task<String> loadFileTask = new Task<>() {
			@Override
			protected String call() throws Exception {
				final var reader = new BufferedReader(new FileReader(fileToLoad));
				// Use Files.lines() to calculate total lines - used for progress
				long lineCount;
				try (Stream<String> stream = Files.lines(fileToLoad.toPath())) {
					lineCount = stream.count();
				}
				// Load in all lines one by one into a StringBuilder separated by "\n" -
				// compatible with TextArea
				String line;
				final var totalFile = new StringBuilder();
				long linesLoaded = 0;
				while ((line = reader.readLine()) != null) {
					totalFile.append(line);
					totalFile.append("\n");
					updateProgress(++linesLoaded, lineCount);
				}
				reader.close();
				return totalFile.toString();
			}
		};
		// If successful, update the text area, display a success message and store the
		// loaded file reference
		loadFileTask.setOnSucceeded(workerStateEvent -> {
			try {
				this.textArea.replaceText(0, this.textArea.getText().length(), loadFileTask.get());
				// this.statusMessage.setText("File loaded: " + fileToLoad.getName());
				this.loadedFileReference = fileToLoad;
				this.lastModifiedTime = Files.readAttributes(fileToLoad.toPath(), BasicFileAttributes.class)
						.lastModifiedTime();
				this.textArea.setEditable(true);
				this.textArea.setFile(fileToLoad);
			} catch (InterruptedException | ExecutionException | IOException e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
				this.textArea.replaceText(0, this.textArea.getText().length(),
						"Could not load file from:\n " + (fileToLoad == null ? "N/A" : fileToLoad.getAbsolutePath()));
				this.textArea.setEditable(false);
				this.textArea.setFile(null);
			}
			if (this.loadedFileReference != null) {
				scheduleFileChecking(this.loadedFileReference);
			}
		});
		// If unsuccessful, set text area with error message and status message to
		// failed
		loadFileTask.setOnFailed(workerStateEvent -> {
			this.textArea.replaceText(0, this.textArea.getText().length(),
					"Could not load file from: " + (fileToLoad == null ? "N/A" : fileToLoad.getAbsolutePath()));
			this.textArea.setEditable(false);
			this.textArea.setFile(null);
			// this.statusMessage.setText("Failed to load file.");
			// this.progressBar.setProgress(0);
			// this.progressBar.progressProperty().bind(null);
			// this.progressBar.progressProperty().unbind();
			// this.loadChangesButton.setVisible(false);
		});
		return loadFileTask;
	}

	public void loadChanges() {
		loadFileToTextArea(this.loadedFileReference);
		// this.loadChangesButton.setVisible(false);
	}

	private void loadFileToTextArea(final File fileToLoad) {
		final Task<String> loadTask = fileLoaderTask(fileToLoad);
		// this.progressBar.progressProperty().bind(loadTask.progressProperty());
		loadTask.run();
	}

	private void notifyUserOfChanges() {
		// this.loadChangesButton.setVisible(true);
	}

	public File openFile() {
		final var fileChooser = new FileChooser();
		// set initial directory somewhere user will recognise
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		// let user select file
		final var fileToLoad = fileChooser.showOpenDialog(null);
		// if file has been chosen, load it using asynchronous method (define later)
		if (fileToLoad != null) {
			loadFileToTextArea(fileToLoad);
			return fileToLoad;
		}
		return null;
	}

	public void saveFile() {
		try {
			final var myWriter = new FileWriter(this.loadedFileReference);
			myWriter.write(this.textArea.getText());
			myWriter.close();
			this.lastModifiedTime = FileTime.fromMillis(System.currentTimeMillis() + 3000);
			System.out.println("Successfully wrote to the file.");
		} catch (final IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
		}
	}

	private void scheduleFileChecking(final File file) {
		final ScheduledService<Boolean> fileChangeCheckingService = createFileChangesCheckingService(file);
		fileChangeCheckingService.setOnSucceeded(workerStateEvent -> {
			if (fileChangeCheckingService.getLastValue() == null)
				return;
			if (fileChangeCheckingService.getLastValue()) {
				// no need to keep checking
				fileChangeCheckingService.cancel();
				notifyUserOfChanges();
			}
		});
		System.out.println("Starting Checking Service...");
		fileChangeCheckingService.start();
	}

	public void setFile(final File file) {
		this.textArea.setFile(file);
		this.loadedFileReference = file;
		loadChanges();
	}
}
