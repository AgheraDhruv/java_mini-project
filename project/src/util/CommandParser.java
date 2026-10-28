package util;

import model.Command;
import model.TransactionType;

/**
 * CommandParser — parses a text line into a Command object.
 * Expected format: "TYPE ACCOUNT_NUMBER AMOUNT"
 */
public class CommandParser {

    public static Command parse(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Command line cannot be empty.");
        }

        String[] parts = line.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                "Invalid command format. Expected: TYPE ACCOUNT_NUMBER AMOUNT (got " + parts.length + " parts).");
        }

        TransactionType type;
        try {
            type = TransactionType.valueOf(parts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown transaction type: " + parts[0]);
        }

        String accountNumber = parts[1].toUpperCase();

        long amount;
        try {
            amount = Long.parseLong(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount: " + parts[2]);
        }

        return new Command(type, accountNumber, amount);
    }
}
