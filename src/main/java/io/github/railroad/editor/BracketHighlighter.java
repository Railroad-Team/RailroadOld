package io.github.railroad.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.github.railroad.objects.RailroadCodeArea;
import javafx.application.Platform;

/**
 * @author TurtyWurty
 */
public class BracketHighlighter {
    private static final List<String> CLEAR_STYLE = Collections.emptyList();
    private static final List<String> MATCH_STYLE = Collections.singletonList("match");
    private static final String BRACKET_PAIRS = "(){}[]<>";
    
    private final RailroadCodeArea codeArea;
    private final List<BracketPair> bracketPairs;
    
    /**
     * Parameterized constructor
     *
     * @param codeArea the code area
     */
    public BracketHighlighter(final RailroadCodeArea codeArea) {
        this.codeArea = codeArea;
        
        this.bracketPairs = new ArrayList<>();
        
        // listen for changes in text or caret position
        this.codeArea.addTextInsertionListener((start, end, text) -> clearBracket());
        this.codeArea.caretPositionProperty()
            .addListener((obs, oldVal, newVal) -> Platform.runLater(() -> highlightBracket(newVal)));
    }
    
    /**
     * Clear the existing highlighted bracket styles
     */
    public void clearBracket() {
        // get iterator of bracket pairs
        final Iterator<BracketPair> iterator = this.bracketPairs.iterator();
        
        // loop through bracket pairs and clear all
        while (iterator.hasNext()) {
            // get next bracket pair
            final BracketPair pair = iterator.next();
            
            // clear pair
            styleBrackets(pair, CLEAR_STYLE);
            
            // remove bracket pair from list
            iterator.remove();
        }
    }
    
    /**
     * Highlight the matching bracket at current caret position
     */
    public void highlightBracket() {
        this.highlightBracket(this.codeArea.getCaretPosition());
    }
    
    /**
     * Find the matching bracket location
     *
     * @param  index to start searching from
     * @return       null or position of matching bracket
     */
    private Integer getMatchingBracket(int index) {
        if (index < 0 || index >= this.codeArea.getLength())
            return null;
        
        final var initialBracket = this.codeArea.getText(index, index + 1).charAt(0);
        final int bracketTypePosition = BRACKET_PAIRS.indexOf(initialBracket); // "(){}[]<>"
        if (bracketTypePosition < 0)
            return null;
            
        // even numbered bracketTypePositions are opening brackets, and odd positions
        // are closing
        // if even (opening bracket) then step forwards, otherwise step backwards
        final int stepDirection = bracketTypePosition % 2 == 0 ? +1 : -1;
        
        // the matching bracket to look for, the opposite of initialBracket
        final var match = BRACKET_PAIRS.charAt(bracketTypePosition + stepDirection);
        
        index += stepDirection;
        var bracketCount = 1;
        
        while (index > -1 && index < this.codeArea.getLength()) {
            final var code = this.codeArea.getText(index, index + 1).charAt(0);
            if (code == initialBracket) {
                bracketCount++;
            } else if (code == match) {
                bracketCount--;
            }
            if (bracketCount == 0)
                return index;
            index += stepDirection;
        }
        
        return null;
    }
    
    /**
     * Highlight the matching bracket at new caret position
     *
     * @param newVal the new caret position
     */
    private void highlightBracket(int newVal) {
        // first clear existing bracket highlights
        clearBracket();
        
        // detect caret position both before and after bracket
        final String prevChar = newVal > 0 && newVal <= this.codeArea.getLength()
            ? this.codeArea.getText(newVal - 1, newVal)
            : "";
        if (BRACKET_PAIRS.contains(prevChar)) {
            newVal--;
        }
        
        // get other half of matching bracket
        final Integer other = getMatchingBracket(newVal);
        
        if (other != null) {
            // other half exists
            final var pair = new BracketPair(newVal, other);
            
            // highlight pair
            styleBrackets(pair, MATCH_STYLE);
            
            // add bracket pair to list
            this.bracketPairs.add(pair);
        }
    }
    
    /**
     * Set a list of styles for a position
     *
     * @param pos    the position
     * @param styles the style list to set
     */
    private void styleBracket(final int pos, final List<String> styles) {
        if (pos < this.codeArea.getLength()) {
            final String text = this.codeArea.getText(pos, pos + 1);
            if (BRACKET_PAIRS.contains(text)) {
                this.codeArea.setStyle(pos, pos + 1, styles);
            }
        }
    }
    
    /**
     * Set a list of styles to a pair of brackets
     *
     * @param pair   pair of brackets
     * @param styles the style list to set
     */
    private void styleBrackets(final BracketPair pair, final List<String> styles) {
        styleBracket(pair.start, styles);
        styleBracket(pair.end, styles);
    }
    
    /**
     * Class representing a pair of matching bracket indices
     */
    static record BracketPair(int start, int end) {
        @Override
        public String toString() {
            return "BracketPair{" + "start=" + this.start + ", end=" + this.end + '}';
        }
    }
}
