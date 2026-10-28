package model;

/**
 * MenuOption — the actions MiniBank offers.
 * Practical 1: OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT.
 * Practical 14: STATEMENT, LIST_ACCOUNTS, BATCH_PROCESS, REPORT.
 */
public enum MenuOption {
    OPEN_ACCOUNT,
    DEPOSIT,
    WITHDRAW,
    TRANSFER,
    STATEMENT,
    LIST_ACCOUNTS,
    BATCH_PROCESS,
    REPORT,
    EXIT;

    public String label() {
        String raw = name().replace('_', ' ');
        StringBuilder sb = new StringBuilder();
        for (String word : raw.split(" ")) {
            if (!sb.isEmpty()) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
