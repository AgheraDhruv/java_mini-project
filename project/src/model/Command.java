package model;

/**
 * Command — an immutable command parsed from a user input line.
 */
public record Command(TransactionType type, String accountNumber, long amount) {}
