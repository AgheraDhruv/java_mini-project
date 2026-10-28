import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.AccountWorker;
import service.BankService;
import util.StatePersister;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MiniBankTest — comprehensive JUnit 5 unit test suite for MiniBank.
 * Tests deposit increases, exception handling on withdrawal over limit,
 * concurrent deposit correctness, and state save/reload persistence.
 *
 * Practical 14 Milestone 3 requirement.
 */
public class MiniBankTest {

    private BankService bankService;

    @BeforeEach
    void setUp() {
        bankService = new BankService();
    }

    @Test
    @DisplayName("Deposit increases account balance correctly")
    void testDepositIncreasesBalance() throws BankException {
        Account acc = bankService.openAccount("SAVINGS", "Riya", 5000, 1000);
        assertEquals(5000, acc.getBalance());

        bankService.deposit(acc.getAccountNumber(), 2500);
        assertEquals(7500, acc.getBalance(), "Balance should be increased by 2500");
    }

    @Test
    @DisplayName("Withdrawal over minimum balance throws InsufficientFundsException")
    void testWithdrawOverLimitThrowsException() throws BankException {
        // SavingsAccount opening balance 5000, minBalance 1000.
        // Attempting to withdraw 4500 leaves 500 which is < 1000 minBalance.
        Account acc = bankService.openAccount("SAVINGS", "Arjun", 5000, 1000);

        InsufficientFundsException thrown = assertThrows(
                InsufficientFundsException.class,
                () -> bankService.withdraw(acc.getAccountNumber(), 4500),
                "Expected withdraw to throw InsufficientFundsException due to min balance constraint"
        );

        assertTrue(thrown.getMessage().contains("insufficient funds"));
        assertEquals(5000, acc.getBalance(), "Balance must remain unchanged after failed withdrawal");
    }

    @Test
    @DisplayName("Non-positive amounts throw InvalidAmountException")
    void testNonPositiveAmountThrowsException() throws BankException {
        Account acc = bankService.openAccount("CURRENT", "Meera", 2000, 5000);

        assertThrows(InvalidAmountException.class, () -> bankService.deposit(acc.getAccountNumber(), 0));
        assertThrows(InvalidAmountException.class, () -> bankService.deposit(acc.getAccountNumber(), -100));
        assertThrows(InvalidAmountException.class, () -> bankService.withdraw(acc.getAccountNumber(), -50));
    }

    @Test
    @DisplayName("Concurrent deposits across multiple threads maintain correct total balance")
    void testConcurrentDepositsCorrectness() throws InterruptedException {
        Account safeAccount = new SavingsAccount("Concurrent User", 0, 0);
        int threadCount = 10;
        int depositsPerThread = 1000;
        long amountPerDeposit = 1;
        long expectedTotal = threadCount * depositsPerThread * amountPerDeposit; // 10,000

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new AccountWorker(safeAccount, depositsPerThread, amountPerDeposit));
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        assertEquals(expectedTotal, safeAccount.getBalance(),
                "Synchronized deposits across 10 threads must equal exact total without race conditions");
    }

    @Test
    @DisplayName("State persistence saves and reloads accounts unchanged")
    void testStatePersistenceSaveAndLoad() throws IOException, ClassNotFoundException, BankException {
        Path tempFile = Files.createTempFile("minibank_test_state", ".dat");
        try {
            Account original = new SavingsAccount("Persistence Test", 8500, 1500);
            Account[] accountsToSave = { original };

            StatePersister.save(accountsToSave, tempFile);
            Account[] restoredAccounts = StatePersister.load(tempFile);

            assertNotNull(restoredAccounts);
            assertEquals(1, restoredAccounts.length);
            Account restored = restoredAccounts[0];

            assertEquals(original.getAccountNumber(), restored.getAccountNumber());
            assertEquals(original.getOwnerName(), restored.getOwnerName());
            assertEquals(original.getBalance(), restored.getBalance());
            assertEquals(original.isActive(), restored.isActive());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
