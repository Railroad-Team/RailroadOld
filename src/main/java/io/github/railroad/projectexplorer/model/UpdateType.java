package io.github.railroad.projectexplorer.model;

/**
 * Types of updates to the director model.
 */
public enum UpdateType {
    /** Indicates a new directory entry. */
    CREATION,
    
    /** Indicates removal of a directory entry. */
    DELETION,
    
    /** Indicates file modification. */
    MODIFICATION,
}