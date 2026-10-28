package service;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import model.*;
import model.annotation.Audit;
import util.ReportGenerator;
import util.StatePersister;
import util.StatementFormatter;
import util.TransactionLog;
import util.Validator;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BankService — central business logic service layer of MiniBank.
 * Wires the Repository, Validator, Exception handling, Persistence,
 * Concurrency, and Logging into cohesive banking operations.
 *
 * Practical 14 capstone.
 */
public class BankService {

    private final Repository<Account> repository;
    private final ConcurrentHashMap<String, Account> concurrentStore;
    private Path logPath = Path.of("logs/transactions.log");
    private Path logsDirectory = Path.of("logs");
    private Path dataFilePath = Path.of("data/accounts.dat");

    public BankService(Repository<Account> repository) {
        this.repository = repository != null ? repository : new Repository<>();
        this.concurrentStore = new ConcurrentHashMap<>();
    }

    public BankService() {
        this(new Repository<>());
    }

    public void setLogPath(Path logPath) {
        this.logPath = logPath;
    }

    public void setDataFilePath(Path dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    public void setLogsDirectory(Path logsDirectory) {
        this.logsDirectory = logsDirectory;
    }

    public Repository<Account> getRepository() {
        return repository;
    }

    public ConcurrentHashMap<String, Account> getConcurrentStore() {
        return concurrentStore;
    }

    /**
     * Reflectively audits method execution if annotated with @Audit.
     */
    private void auditExecution(String methodName, String details) {
        try {
            for (Method m : getClass().getDeclaredMethods()) {
                if (m.getName().equals(methodName) && m.isAnnotationPresent(Audit.class)) {
                    Audit audit = m.getAnnotation(Audit.class);
                    String actionName = audit.action().isEmpty() ? methodName : audit.action();
                    String logLine = "[AUDIT] " + actionName + " - " + details;
                    try {
                        TransactionLog.append(logPath, logLine);
                    } catch (IOException ignored) {}
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    /**
     * Opens a new account and persists it in the repository.
     */
    @Audit(action = "OPEN_ACCOUNT")
    public Account openAccount(String type, String ownerName, long openingBalance, long extraParam) throws BankException {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new InvalidAmountException("Account owner name cannot be empty.");
        }
        if (openingBalance < 0) {
            throw new InvalidAmountException("Opening balance cannot be negative.");
        }

        Account account;
        String normalizedType = type != null ? type.trim().toUpperCase() : "SAVINGS";
        switch (normalizedType) {
            case "CURRENT" -> {
                long overdraft = extraParam > 0 ? extraParam : 5000;
                account = new CurrentAccount(ownerName.trim(), openingBalance, overdraft);
            }
            case "FIXED", "FD", "FIXED_DEPOSIT" -> {
                account = new FixedDepositAccount(ownerName.trim(), openingBalance);
            }
            default -> {
                long minBal = extraParam > 0 ? extraParam : 1000;
                account = new SavingsAccount(ownerName.trim(), openingBalance, minBal);
            }
        }

        repository.save(account);
        concurrentStore.put(account.getAccountNumber(), account);

        auditExecution("openAccount", account.getAccountNumber() + " opened for " + ownerName);
        try {
            TransactionLog.append(logPath, "OPEN " + account.getAccountNumber() + " " + openingBalance);
        } catch (IOException ignored) {}

        return account;
    }

    /**
     * Deposits money into an account.
     */
    @Audit(action = "DEPOSIT")
    public void deposit(String accountNumber, long amount) throws BankException {
        Account account = getAccountOrThrow(accountNumber);
        account.deposit(amount);

        auditExecution("deposit", accountNumber + " deposited Rs. " + amount);
        try {
            TransactionLog.append(logPath, "DEPOSIT " + accountNumber + " " + amount);
        } catch (IOException ignored) {}
    }

    /**
     * Withdraws money from an account honoring subtype constraints.
     */
    @Audit(action = "WITHDRAW")
    public void withdraw(String accountNumber, long amount) throws BankException {
        Account account = getAccountOrThrow(accountNumber);
        account.withdraw(amount);

        auditExecution("withdraw", accountNumber + " withdrew Rs. " + amount);
        try {
            TransactionLog.append(logPath, "WITHDRAW " + accountNumber + " " + amount);
        } catch (IOException ignored) {}
    }

    /**
     * Transfers money atomically from one account to another using lock ordering.
     */
    @Audit(action = "TRANSFER")
    public void transfer(String fromAccNumber, String toAccNumber, long amount) throws BankException {
        Account from = getAccountOrThrow(fromAccNumber);
        Account to = getAccountOrThrow(toAccNumber);

        // Safe transfer with consistent lock ordering to prevent deadlock
        DeadlockDemo.safeTransfer(from, to, amount);

        auditExecution("transfer", "From " + fromAccNumber + " to " + toAccNumber + " Rs. " + amount);
        try {
            TransactionLog.append(logPath, "TRANSFER " + fromAccNumber + "->" + toAccNumber + " " + amount);
        } catch (IOException ignored) {}
    }

    /**
     * Builds and returns a formatted multi-line statement.
     */
    @Audit(action = "STATEMENT")
    public String statement(String accountNumber) throws BankException {
        Account account = getAccountOrThrow(accountNumber);
        return StatementFormatter.buildStatement(account);
    }

    /**
     * Lists all registered accounts.
     */
    public List<Account> listAccounts() {
        return repository.findAll();
    }

    /**
     * Supplementary: Searches accounts by owner name (case-insensitive substring match).
     */
    public List<Account> searchByOwner(String nameSubstring) {
        List<Account> matches = new ArrayList<>();
        if (nameSubstring == null || nameSubstring.isBlank()) {
            return matches;
        }
        String lower = nameSubstring.trim().toLowerCase();
        for (Account a : repository.findAll()) {
            if (a.getOwnerName().toLowerCase().contains(lower)) {
                matches.add(a);
            }
        }
        return matches;
    }

    /**
     * Executes a batch of concurrent transactions on thread pool.
     */
    public void processBatch(List<Runnable> tasks) {
        TransactionProcessor processor = new TransactionProcessor(4, concurrentStore);
        for (Runnable task : tasks) {
            processor.submit(task);
        }
        processor.stop();
    }

    /**
     * Generates an end-of-day summary report.
     */
    public String generateReport(Path reportFile) throws IOException {
        return ReportGenerator.generateReport(logsDirectory, reportFile);
    }

    /**
     * Saves all repository accounts to disk via StatePersister.
     */
    public void saveState() throws IOException {
        Account[] accounts = repository.findAll().toArray(new Account[0]);
        StatePersister.save(accounts, dataFilePath);
    }

    /**
     * Loads saved accounts from disk into the repository.
     */
    public int loadState() {
        try {
            Account[] accounts = StatePersister.load(dataFilePath);
            for (Account a : accounts) {
                if (a != null) {
                    repository.save(a);
                    concurrentStore.put(a.getAccountNumber(), a);
                }
            }
            return accounts.length;
        } catch (Exception e) {
            return 0;
        }
    }

    private Account getAccountOrThrow(String accountNumber) throws AccountNotFoundException {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new AccountNotFoundException("Account number cannot be empty.");
        }
        Account account = repository.findById(accountNumber.trim().toUpperCase());
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        return account;
    }
}
