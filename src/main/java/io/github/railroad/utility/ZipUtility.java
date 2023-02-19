package io.github.railroad.utility;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class ZipUtility {
    public static void unzip(File path, File dest) {
        if (!dest.exists() && !dest.mkdirs())
            throw new IllegalStateException("Failed to create destination directory!");

        try (var stream = new ZipInputStream(new FileInputStream(path))) {
            ZipEntry entry = stream.getNextEntry();
            while (entry != null) {
                String extractPath = dest.getAbsolutePath() + File.separator + entry.getName();
                if (entry.isDirectory()) {
                    var directory = new File(extractPath);
                    if (!directory.exists() && !directory.mkdirs()) {
                        throw new IllegalStateException("Failed to create directory: " + extractPath);
                    }
                } else {
                    extractFileFromZip(stream, extractPath);
                }

                stream.closeEntry();
                entry = stream.getNextEntry();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to unzip file", exception);
        }
    }

    public static void unzip(Path path, Path dest) {
        unzip(path.toFile(), dest.toFile());
    }

    public static void unzip(String path, String dest) {
        unzip(new File(path), new File(dest));
    }

    public static void extractFileFromZip(ZipInputStream stream, String extractPath) {
        try (var outStream = new BufferedOutputStream(new FileOutputStream(extractPath))) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = stream.read(buffer)) != -1) {
                outStream.write(buffer, 0, read);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to extract file from zip", exception);
        }
    }

    public static void zipFile(File file, File dest) {
        try (var fileStream = new FileOutputStream(dest)) {
            try (var zipStream = new ZipOutputStream(fileStream)) {
                zipStream.putNextEntry(new ZipEntry(file.getName()));

                byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                zipStream.write(bytes, 0, bytes.length);
                zipStream.closeEntry();
            }
        } catch (FileNotFoundException exception) {
            throw new IllegalStateException("Failed to find file", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to zip file", exception);
        }
    }

    public static void zipFile(Path file, Path dest) {
        zipFile(file.toFile(), dest.toFile());
    }

    public static void zipFile(String file, String dest) {
        zipFile(new File(file), new File(dest));
    }

    public static void zipFiles(File dest, File... files) {
        try (var fileStream = new FileOutputStream(dest)) {
            try (var zipStream = new ZipOutputStream(fileStream)) {
                for (File file : files) {
                    zipStream.putNextEntry(new ZipEntry(file.getName()));

                    byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                    zipStream.write(bytes, 0, bytes.length);
                    zipStream.closeEntry();
                }
            }
        } catch (FileNotFoundException exception) {
            throw new IllegalStateException("Failed to find file", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to zip file", exception);
        }
    }

    public static void zipFiles(Path dest, Path... files) {
        zipFiles(dest.toFile(), Arrays.stream(files).map(Path::toFile).toArray(File[]::new));
    }

    public static void zipFiles(String dest, String... files) {
        zipFiles(new File(dest), Arrays.stream(files).map(File::new).toArray(File[]::new));
    }

    public static void zipDirectory(File directory, File dest) {
        try (var fileStream = new FileOutputStream(dest)) {
            try (var zipStream = new ZipOutputStream(fileStream)) {
                Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        try {
                            Path relativePath = directory.toPath().relativize(file);
                            zipStream.putNextEntry(new ZipEntry(relativePath.toString()));

                            byte[] bytes = Files.readAllBytes(file);
                            zipStream.write(bytes, 0, bytes.length);
                            zipStream.closeEntry();
                        } catch (IOException exception) {
                            throw new IllegalStateException("Failed to zip file", exception);
                        }

                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (FileNotFoundException exception) {
            throw new IllegalStateException("Failed to find file", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to zip file", exception);
        }
    }

    public static void zipDirectory(Path directory, Path dest) {
        zipDirectory(directory.toFile(), dest.toFile());
    }

    public static void zipDirectory(String directory, String dest) {
        zipDirectory(new File(directory), new File(dest));
    }

    public static void readZip(File file, Consumer<ZipEntry> entryConsumer) {
        try (var zip = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                entryConsumer.accept(entries.nextElement());
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read zip file", exception);
        }
    }

    public static void readZip(Path file, Consumer<ZipEntry> entryConsumer) {
        readZip(file.toFile(), entryConsumer);
    }

    public static void readZip(String file, Consumer<ZipEntry> entryConsumer) {
        readZip(new File(file), entryConsumer);
    }

    public static void readZipEntry(File file, String entryName, Consumer<InputStream> entryConsumer) {
        try (var zip = new ZipFile(file)) {
            ZipEntry entry = zip.getEntry(entryName);
            if (entry == null)
                throw new IllegalStateException("Failed to find entry in zip file");

            entryConsumer.accept(zip.getInputStream(entry));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read zip file", exception);
        }
    }

    public static void readZipEntry(Path file, String entryName, Consumer<InputStream> entryConsumer) {
        readZipEntry(file.toFile(), entryName, entryConsumer);
    }

    public static void readZipEntry(String file, String entryName, Consumer<InputStream> entryConsumer) {
        readZipEntry(new File(file), entryName, entryConsumer);
    }
}
