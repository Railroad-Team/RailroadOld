package io.github.railroad.projectexplorer.utility;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

/**
 * Simple API for asynchronous file-system operations. The operations are
 * analogous to those of {@link IOFacility}, except that every
 * filesystem-changing operation takes an extra argument&mdash;the initiator of
 * the change.
 *
 * @param <I> type of the initiator of I/O actions
 */
public interface InitiatorTrackingIOFacility {

    CompletionStage<Void> createDirectory(Path dir, Path initiator);

    CompletionStage<Void> createFile(Path file, Path initiator);

    CompletionStage<Void> delete(Path fileOrDir, Path initiator);

    CompletionStage<Void> deleteTree(Path root, Path initiator);

    CompletionStage<byte[]> loadBinaryFile(Path file);

    CompletionStage<String> loadTextFile(Path file, Charset charset);

    default CompletionStage<String> loadUTF8File(Path file) {
        return loadTextFile(file, StandardCharsets.UTF_8);
    }

    CompletionStage<Void> saveBinaryFile(Path file, byte[] content, Path initiator);

    CompletionStage<Void> saveTextFile(Path file, String content, Charset charset, Path initiator);

    default CompletionStage<Void> saveUTF8File(Path file, String content, Path initiator) {
        return saveTextFile(file, content, StandardCharsets.UTF_8, initiator);
    }

    /**
     * Returns an IOFacility that delegates all operations to this I/O facility with
     * the preset initiator of changes.
     */
    default IOFacility withInitiator(Path initiator) {
        final InitiatorTrackingIOFacility self = this;

        return new IOFacility() {

            @Override
            public CompletionStage<Void> createDirectory(Path dir) {
                return self.createDirectory(dir, initiator);
            }

            @Override
            public CompletionStage<Void> createFile(Path file) {
                return self.createFile(file, initiator);
            }

            @Override
            public CompletionStage<Void> delete(Path fileOrDir) {
                return self.delete(fileOrDir, initiator);
            }

            @Override
            public CompletionStage<Void> deleteTree(Path root) {
                return self.deleteTree(root, initiator);
            }

            @Override
            public CompletionStage<byte[]> loadBinaryFile(Path file) {
                return self.loadBinaryFile(file);
            }

            @Override
            public CompletionStage<String> loadTextFile(Path file, Charset charset) {
                return self.loadTextFile(file, charset);
            }

            @Override
            public CompletionStage<Void> saveBinaryFile(Path file, byte[] content) {
                return self.saveBinaryFile(file, content, initiator);
            }

            @Override
            public CompletionStage<Void> saveTextFile(Path file, String content, Charset charset) {
                return self.saveTextFile(file, content, charset, initiator);
            }
        };
    }
}