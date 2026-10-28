package service;

import model.Account;
import exception.BankException;

/**
 * DeadlockDemo — safely demonstrates opposite-order locking and deadlock prevention.
 * Uses consistent lock ordering (locking the lower account number first) to eliminate circular wait.
 */
public class DeadlockDemo {

    /**
     * Demonstrates deadlock vulnerability caused by opposite lock ordering.
     * Uses isolated daemon threads so it safely proves deadlock behavior without hanging forever.
     */
    public static void demonstrateOppositeLocking(Account a, Account b) {
        System.out.println("  [Deadlock Simulation] Thread 1 locks Resource A then waits for B; Thread 2 locks B then waits for A.");
        Object lockA = new Object();
        Object lockB = new Object();

        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                synchronized (lockB) {
                    // Unreachable under deadlock
                }
            }
        }, "Deadlock-Thread-1");
        t1.setDaemon(true);

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                synchronized (lockA) {
                    // Unreachable under deadlock
                }
            }
        }, "Deadlock-Thread-2");
        t2.setDaemon(true);

        t1.start();
        t2.start();

        try {
            t1.join(150);
            t2.join(150);
            if (t1.isAlive() && t2.isAlive()) {
                System.out.println("  -> Deadlock detected! Both threads blocked waiting for each other's lock (circular wait).");
            }
        } catch (InterruptedException ignored) {}
    }

    /**
     * Executes safe transfer between two accounts using consistent lock ordering.
     * Always acquires lock on the smaller accountNumber first to eliminate circular wait.
     */
    public static void safeTransfer(Account from, Account to, long amount) throws BankException {
        // Enforce consistent global lock ordering by account number
        Account firstLock = from.getAccountNumber().compareTo(to.getAccountNumber()) < 0 ? from : to;
        Account secondLock = (firstLock == from) ? to : from;

        synchronized (firstLock) {
            synchronized (secondLock) {
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }
}
