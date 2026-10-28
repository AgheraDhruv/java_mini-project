package service;

import model.Account;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TransactionProcessor — manages transaction execution on a fixed thread pool.
 * Supports concurrent account operations over a thread-safe ConcurrentHashMap store.
 */
public class TransactionProcessor {

    private final ExecutorService executor;
    private final ConcurrentHashMap<String, Account> concurrentAccounts;

    public TransactionProcessor(int threads, ConcurrentHashMap<String, Account> sharedAccounts) {
        this.executor = Executors.newFixedThreadPool(threads);
        this.concurrentAccounts = sharedAccounts != null ? sharedAccounts : new ConcurrentHashMap<>();
    }

    public TransactionProcessor(int threads) {
        this(threads, new ConcurrentHashMap<>());
    }

    public TransactionProcessor() {
        this(4, new ConcurrentHashMap<>());
    }

    public ConcurrentHashMap<String, Account> getConcurrentAccounts() {
        return concurrentAccounts;
    }

    /**
     * Submits a transaction task to the thread pool for asynchronous execution.
     */
    public void submit(Runnable task) {
        executor.execute(task);
    }

    /**
     * Initiates an orderly shutdown and waits for running tasks to terminate.
     */
    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
