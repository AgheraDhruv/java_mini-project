package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * ReportGenerator — parses transaction logs using DirectoryStream and NIO Buffers
 * to compute total deposits, total withdrawals, and net balance changes.
 */
public class ReportGenerator {

    /**
     * Walks the log folder, reads entries line-by-line, and outputs an end-of-day summary report.
     *
     * @param logsDir directory holding .log files
     * @param reportFile output file where summary report is written
     * @return the report content as a String
     */
    public static String generateReport(Path logsDir, Path reportFile) throws IOException {
        long totalDeposits = 0;
        long totalWithdrawals = 0;
        int fileCount = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("        MINIBANK END-OF-DAY REPORT       \n");
        sb.append("=========================================\n");

        if (Files.exists(logsDir) && Files.isDirectory(logsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir)) {
                for (Path entry : stream) {
                    // Filter: only non-empty .log files
                    if (!entry.getFileName().toString().endsWith(".log") || Files.size(entry) == 0) {
                        continue;
                    }
                    fileCount++;
                    BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                    sb.append("Log: ").append(entry.getFileName())
                            .append(" | Size: ").append(attrs.size()).append(" B")
                            .append(" | Modified: ").append(attrs.lastModifiedTime()).append("\n");

                    try (BufferedReader reader = Files.newBufferedReader(entry, StandardCharsets.UTF_8)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.isBlank()) continue;
                            String[] parts = line.trim().split("\\s+");
                            if (parts.length >= 3) {
                                String op = parts[0].toUpperCase();
                                try {
                                    long amt = Long.parseLong(parts[2]);
                                    if ("DEPOSIT".equals(op)) {
                                        totalDeposits += amt;
                                    } else if ("WITHDRAW".equals(op)) {
                                        totalWithdrawals += amt;
                                    }
                                } catch (NumberFormatException ignored) {}
                            }
                        }
                    }
                }
            }
        }

        long netChange = totalDeposits - totalWithdrawals;
        sb.append("-----------------------------------------\n");
        sb.append("Total Log Files Processed : ").append(fileCount).append("\n");
        sb.append("Total Deposits            : Rs. ").append(totalDeposits).append("\n");
        sb.append("Total Withdrawals         : Rs. ").append(totalWithdrawals).append("\n");
        sb.append("Net Balance Change        : Rs. ").append(netChange).append("\n");
        sb.append("=========================================\n");

        String reportText = sb.toString();
        if (reportFile != null) {
            if (reportFile.getParent() != null) {
                Files.createDirectories(reportFile.getParent());
            }
            Files.writeString(reportFile, reportText, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        return reportText;
    }
}
