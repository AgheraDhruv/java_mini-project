package service;

import model.Account;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AccountStore — manages accounts using the Java Collections Framework.
 * Uses HashMap<String, Account> keyed by accountNumber, ArrayList<String> for transaction history,
 * Comparator for sorting by balance descending, and ConcurrentHashMap for multi-threaded processing.
 */
public class AccountStore {

    private final Map<String, Account> accounts = new HashMap<>();
    private final List<String> transactionHistory = new ArrayList<>();
    private final ConcurrentHashMap<String, Account> concurrentStore = new ConcurrentHashMap<>();

    /**
     * Adds an account to both HashMap and thread-safe ConcurrentHashMap.
     */
    public void add(Account account) {
        if (account != null) {
            accounts.put(account.getAccountNumber(), account);
            concurrentStore.put(account.getAccountNumber(), account);
            transactionHistory.add("Added account: " + account.getAccountNumber() + " (" + account.getOwnerName() + ")");
        }
    }

    /**
     * Looks up an account by its unique account number.
     */
    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    /**
     * Returns a list of all accounts.
     */
    public List<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    /**
     * Returns an unmodifiable view of the transaction history.
     */
    public List<String> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    /**
     * Logs a transaction message into the history list.
     */
    public void logTransaction(String entry) {
        transactionHistory.add(entry);
    }

    /**
     * Returns all accounts sorted by balance in descending order using a Comparator.
     * (E.g. 9000, 5000, 2000 as required by the practical spec).
     */
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> list = new ArrayList<>(accounts.values());
        list.sort((a1, a2) -> Long.compare(a2.getBalance(), a1.getBalance()));
        return list;
    }

    /**
     * Supplementary: Returns all accounts with balance strictly above the threshold.
     */
    public List<Account> findAccountsAboveBalance(long minThreshold) {
        List<Account> result = new ArrayList<>();
        for (Account a : accounts.values()) {
            if (a.getBalance() > minThreshold) {
                result.add(a);
            }
        }
        return result;
    }

    /**
     * Returns the thread-safe ConcurrentHashMap used by concurrent processors.
     */
    public ConcurrentHashMap<String, Account> getConcurrentStore() {
        return concurrentStore;
    }
}
