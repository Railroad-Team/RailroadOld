package io.github.railroad.projectexplorer.core;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class PathNode {
    private static final Comparator<Path> PATH_COMPARATOR = (p, q) -> {
        final boolean pd = Files.isDirectory(p);
        final boolean qd = Files.isDirectory(q);

        if (pd && !qd)
            return -1;
        if (!pd && qd)
            return 1;
        return p.getFileName().toString().compareToIgnoreCase(q.getFileName().toString());
    };

    private final Path path;
    private final boolean isDirectory;
    private final List<PathNode> children;
    private final FileTime lastModified;

    private PathNode(Path path, boolean isDirectory, List<PathNode> children, FileTime lastModified) {
        this.path = path;
        this.isDirectory = isDirectory;
        this.children = children;
        this.lastModified = lastModified;
    }

    public List<PathNode> getChildren() {
        return this.children;
    }

    public FileTime getLastModified() {
        return this.lastModified;
    }

    public Path getPath() {
        return this.path;
    }

    public boolean isDirectory() {
        return this.isDirectory;
    }

    public static PathNode directory(Path path, List<PathNode> children) {
        return new PathNode(path, true, children, null);
    }

    public static PathNode file(Path path, FileTime lastModified) {
        return new PathNode(path, false, Collections.emptyList(), lastModified);
    }

    public static PathNode getTree(Path root) throws IOException {
        try {
            if (!Files.isDirectory(root))
                return file(root, Files.getLastModifiedTime(root));
        } catch (final IOException e) {
            return null;
        }

        Path[] childPaths;

        try (Stream<Path> dirStream = Files.list(root)) {
            childPaths = dirStream.sorted(PATH_COMPARATOR).toArray(Path[]::new);
        } catch (final FileSystemException exception) {
            childPaths = new Path[0];
        }

        final List<PathNode> children = new ArrayList<>(childPaths.length);
        for (final Path path : childPaths) {
            System.out.println(path);
            children.add(getTree(path));
        }
        return directory(root, children);
    }
}