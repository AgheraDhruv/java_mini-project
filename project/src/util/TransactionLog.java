package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

/**
 * TransactionLog — appends transaction entries to log files using Java NIO Files API.
 */
public class TransactionLog {

    /**
     * Appends a log line to a specified path using CREATE and APPEND options.
     */
    public static void append(Path file, String line) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        Files.write(file, Collections.singletonList(line), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
