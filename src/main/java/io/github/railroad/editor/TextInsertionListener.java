package io.github.railroad.editor;

public interface TextInsertionListener {
	void codeInserted(int start, int end, String text);
}
