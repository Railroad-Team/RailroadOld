package io.github.railroad.utility;

import java.io.File;
import java.util.Comparator;

public class ClosestDirectoryComparator<Type extends File> implements Comparator<Type> {

    @Override
    public int compare(Type file0, Type file1) {
        return file0.getAbsolutePath().replace('\\', '/').split("/").length < file1.getAbsolutePath()
                .replace('\\', '/').split("/").length ? 1 : 0;
    }
}
