package service;

/**
 * UnsafeAccount — an isolated educational class demonstrating the mechanics of a race condition.
 * Deliberately performs non-atomic read-modify-write without synchronization.
 */
public class UnsafeAccount {

    private long balance;

    public UnsafeAccount(long openingBalance) {
        this.balance = openingBalance;
    }

    public void deposit(long amount) {
        // Non-atomic read-modify-write susceptible to thread interleaving
        long temp = balance;
        Thread.yield(); // encourages thread context-switch to demonstrate race condition
        balance = temp + amount;
    }

    public long getBalance() {
        return balance;
    }
}
