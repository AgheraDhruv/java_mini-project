package util;

import model.Account;

/**
 * StatementFormatter — builds a readable multi-line account statement.
 * Uses StringBuilder to assemble the output efficiently.
 */
public class StatementFormatter {

    public static String buildStatement(Account account) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== Account Statement ==========\n");
        sb.append("Account Number : ").append(account.getAccountNumber()).append('\n');
        sb.append("Owner Name     : ").append(account.getOwnerName()).append('\n');
        sb.append("Balance        : Rs. ").append(account.getBalance()).append('\n');
        sb.append("Status         : ").append(account.isActive() ? "Active" : "Inactive").append('\n');
        sb.append("=======================================");
        return sb.toString();
    }
}
