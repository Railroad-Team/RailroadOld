package io.github.railroad.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.EditableStyledDocument;
import org.fxmisc.richtext.model.StyledDocument;
import org.jetbrains.annotations.Nullable;

public class RailroadCodeArea extends CodeArea {

	private final List<TextInsertionListener> insertionListeners;
	private File file = null;

	public RailroadCodeArea() {
		this.insertionListeners = new ArrayList<>();
	}

	public RailroadCodeArea(final EditableStyledDocument<Collection<String>, String, Collection<String>> document) {
		super(document);
		this.insertionListeners = new ArrayList<>();
	}

	public RailroadCodeArea(final String text) {
		super(text);
		this.insertionListeners = new ArrayList<>();
	}

	public void addTextInsertionListener(final TextInsertionListener listener) {
		this.insertionListeners.add(listener);
	}

	@Nullable
	public File getFile() {
		return this.file;
	}

	public void removeTextInsertionListener(final TextInsertionListener listener) {
		this.insertionListeners.remove(listener);
	}

	@Override
	public void replace(final int start, final int end,
			final StyledDocument<Collection<String>, String, Collection<String>> replacement) {
		// notify all listeners
		for (final TextInsertionListener listener : this.insertionListeners) {
			listener.codeInserted(start, end, replacement.getText());
		}

		super.replace(start, end, replacement);
	}

	public void setFile(final File file) {
		this.file = file;
	}
}
