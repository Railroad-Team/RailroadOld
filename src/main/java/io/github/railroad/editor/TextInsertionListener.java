package io.github.railroad.editor;

/**
 * @author TurtyWurty
 */
public interface TextInsertionListener {
    void codeInserted(int start, int end, String text);
}
