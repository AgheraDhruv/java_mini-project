import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import model.*;
import util.*;
import service.*;
import exception.*;

// Practical 6: Static import to use Validator methods directly
import static util.Validator.*;

/**
 * MiniBank — the complete Java console banking application.
 *
 * Practical 1: Menu shell with enum, record, switch expression.
 * Practical 2: Customer and Account classes with encapsulation.
 * Practical 3: Object methods (toString, equals, hashCode), nested Address, clone.
 * Practical 4: Input validation, command parsing, statement formatting.
 * Practical 5: Inheritance and polymorphism (Savings, Current, FixedDeposit).
 * Practical 6: Interfaces (Transactable, InterestBearing, Premium),
 *              lambdas (WithdrawRule), package organization, static import.
 * Practical 7: Custom annotations (@Id, @Positive, @MaxLength) and reflection validation.
 * Practical 8: Custom exception hierarchy, atomic transfers, try-catch-finally, try-with-resources.
 * Practical 9: Multithreading, Runnable workers, race conditions, method synchronization.
 * Practical 10: Transaction processor (Executors pool), producer-consumer buffer (wait/notify),
 *               deadlock simulation and deadlock prevention via consistent lock ordering.
 * Practical 11: File persistence (Object serialization), transaction logging (NIO),
 *               and report generation using DirectoryStream and attributes.
 * Practical 12: Collections Framework (HashMap AccountStore, ArrayList history,
 *               Comparator sortedByBalance descending, Comparable<Account>, ConcurrentHashMap).
 * Practical 13: Generic Repository<T> with reflection-based @Id key lookup,
 *               @SafeVarargs saveAll, and bounded generic method <N extends Number> average.
 * Practical 14: Complete MiniBank integration with BankService, interactive menu wiring,
 *               load on startup and save on exit, batch processing, and EOD reporting.
 *
 * @author Bhavy Pan (25CE098)
 */
public class MiniBank {

    private static final BankService bankService = new BankService();

    public static void main(String[] args) {
        // Bank information header
        BankInfo bank = new BankInfo("MiniBank", "CHARUSAT Branch");
        System.out.println("==========================================");
        System.out.println("  Welcome to " + bank.name());
        System.out.println("  Branch : " + bank.branch());
        System.out.println("==========================================");

        // Load saved state on start-up (Practical 11 / Practical 14)
        int loadedCount = bankService.loadState();
        if (loadedCount > 0) {
            System.out.println("[Startup] Loaded " + loadedCount + " saved accounts from storage.");
        }

        // Run practical demonstration suite
        demonstrateAccountsAndCustomers();
        demonstrateObjectMethods();
        demonstrateValidatorsAndParsing();
        demonstrateInheritanceAndPolymorphism();
        demonstrateInterfacesAndLambdas();
        demonstrateAnnotationsAndReflection();
        demonstrateExceptionHandling();
        demonstrateMultithreadingAndSynchronization();
        demonstrateTransactionProcessingAndDeadlock();
        demonstratePersistenceAndReports();
        demonstrateCollectionsAndSorting();
        demonstrateGenericsAndRepository();

        // --- Interactive Menu Loop connected to BankService ---
        Scanner scanner = new Scanner(System.in);
        MenuOption[] options = MenuOption.values();
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("========== MiniBank Operations Menu ==========");
            for (int i = 0; i < options.length; i++) {
                System.out.println("  " + (i + 1) + ". " + options[i].label());
            }
            System.out.print("Enter your choice (1-" + options.length + "): ");

            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and " + options.length + ".");
                continue;
            }

            if (choice < 1 || choice > options.length) {
                System.out.println("Invalid choice. Please enter a number between 1 and " + options.length + ".");
                continue;
            }

            MenuOption selected = options[choice - 1];

            // Practical 14: Menu options wired directly to BankService operations
            switch (selected) {
                case OPEN_ACCOUNT -> handleOpenAccount(scanner);
                case DEPOSIT -> handleDeposit(scanner);
                case WITHDRAW -> handleWithdraw(scanner);
                case TRANSFER -> handleTransfer(scanner);
                case STATEMENT -> handleStatement(scanner);
                case LIST_ACCOUNTS -> handleListAccounts();
                case BATCH_PROCESS -> handleBatchProcess();
                case REPORT -> handleReport();
                case EXIT -> {
                    try {
                        bankService.saveState();
                        System.out.println("[Shutdown] All account data saved to disk.");
                    } catch (IOException e) {
                        System.err.println("Error saving accounts on exit: " + e.getMessage());
                    }
                    running = false;
                }
            }
        }

        System.out.println();
        System.out.println("Thank you for banking with " + bank.name() + ". Goodbye!");
        scanner.close();
    }

    private static void handleOpenAccount(Scanner scanner) {
        System.out.print("Enter Account Type (Savings / Current / Fixed): ");
        String type = scanner.nextLine().trim();

        System.out.print("Enter Customer Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Opening Balance (Rs): ");
        long balance;
        try {
            balance = Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: Balance must be a valid whole number.");
            return;
        }

        long extra = 0;
        if ("CURRENT".equalsIgnoreCase(type)) {
            System.out.print("Enter Overdraft Limit (Rs, default 5000): ");
            String extraStr = scanner.nextLine().trim();
            extra = extraStr.isEmpty() ? 5000 : Long.parseLong(extraStr);
        } else if ("SAVINGS".equalsIgnoreCase(type) || type.isEmpty()) {
            System.out.print("Enter Minimum Balance (Rs, default 1000): ");
            String extraStr = scanner.nextLine().trim();
            extra = extraStr.isEmpty() ? 1000 : Long.parseLong(extraStr);
        }

        try {
            Account acc = bankService.openAccount(type, name, balance, extra);
            System.out.println("Account opened successfully! Account Number: " + acc.getAccountNumber());
            System.out.println("Details: " + acc);
        } catch (BankException e) {
            System.out.println("Error opening account: " + e.getMessage());
        }
    }

    private static void handleDeposit(Scanner scanner) {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        System.out.print("Enter Deposit Amount (Rs): ");
        try {
            long amount = Long.parseLong(scanner.nextLine().trim());
            bankService.deposit(accNum, amount);
            System.out.println("Successfully deposited Rs. " + amount + " into " + accNum.toUpperCase());
        } catch (NumberFormatException e) {
            System.out.println("Error: Amount must be a valid whole number.");
        } catch (BankException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    private static void handleWithdraw(Scanner scanner) {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        System.out.print("Enter Withdrawal Amount (Rs): ");
        try {
            long amount = Long.parseLong(scanner.nextLine().trim());
            bankService.withdraw(accNum, amount);
            System.out.println("Successfully withdrew Rs. " + amount + " from " + accNum.toUpperCase());
        } catch (NumberFormatException e) {
            System.out.println("Error: Amount must be a valid whole number.");
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal rejected: Short by Rs. " + e.getShortfall());
        } catch (BankException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    private static void handleTransfer(Scanner scanner) {
        System.out.print("Enter Source Account Number: ");
        String from = scanner.nextLine().trim();
        System.out.print("Enter Destination Account Number: ");
        String to = scanner.nextLine().trim();
        System.out.print("Enter Transfer Amount (Rs): ");
        try {
            long amount = Long.parseLong(scanner.nextLine().trim());
            bankService.transfer(from, to, amount);
            System.out.println("Successfully transferred Rs. " + amount + " from "
                    + from.toUpperCase() + " to " + to.toUpperCase());
        } catch (NumberFormatException e) {
            System.out.println("Error: Amount must be a valid whole number.");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer rejected: Short by Rs. " + e.getShortfall());
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    private static void handleStatement(Scanner scanner) {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        try {
            String stmt = bankService.statement(accNum);
            System.out.println(stmt);
        } catch (BankException e) {
            System.out.println("Statement lookup error: " + e.getMessage());
        }
    }

    private static void handleListAccounts() {
        List<Account> accounts = bankService.listAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts currently registered.");
            return;
        }
        System.out.println("--- Registered Accounts (" + accounts.size() + ") ---");
        for (Account a : accounts) {
            System.out.println("  " + a);
        }
    }

    private static void handleBatchProcess() {
        System.out.println("Submitting transaction batch to 4-thread pool...");
        List<Account> accounts = bankService.listAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts available for batch processing. Open an account first.");
            return;
        }
        Account target = accounts.get(0);
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(() -> {
                try {
                    target.deposit(50);
                } catch (BankException ignored) {}
            });
        }
        bankService.processBatch(tasks);
        System.out.println("Batch processing completed. Current balance of " + target.getAccountNumber()
                + " = Rs. " + target.getBalance());
    }

    private static void handleReport() {
        try {
            Path reportPath = Path.of("logs/eod_report.txt");
            String report = bankService.generateReport(reportPath);
            System.out.println(report);
        } catch (IOException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    // =========================================================================
    // Progressive Lab Verification Demos (Practicals 2 - 13)
    // =========================================================================

    private static void demonstrateAccountsAndCustomers() {
        System.out.println();
        System.out.println("--- Practical 2: Account & Customer Demo ---");

        Customer c1 = new Customer("Riya", "riya@example.com", "9876543210");
        System.out.println("Customer: " + c1.getName() + " (ID: " + c1.getCustomerId() + ")");

        Account[] accounts = {
            new SavingsAccount("Riya", 5000, 1000),
            new CurrentAccount("Arjun", 3000, 2000),
            new SavingsAccount("Meera")
        };

        boolean w1 = false;
        boolean w2 = false;
        try {
            accounts[0].deposit(2000);
            w1 = accounts[0].withdraw(3000);
            w2 = accounts[0].withdraw(10000);
        } catch (BankException e) {
            w2 = false;
        }

        try {
            accounts[1].deposit(1500);
            accounts[2].deposit(800);
        } catch (BankException ignored) {}

        System.out.println();
        for (Account a : accounts) {
            System.out.println(a.getAccountNumber() + " | " + a.getOwnerName()
                    + " | Balance: " + a.getBalance());
        }
        System.out.println("Withdraw 3000 from " + accounts[0].getAccountNumber() + ": " + w1);
        System.out.println("Withdraw 10000 from " + accounts[0].getAccountNumber() + ": " + w2);
        System.out.println("--- End of Practical 2 Demo ---");
    }

    private static void demonstrateObjectMethods() {
        System.out.println();
        System.out.println("--- Practical 3: Object Methods Demo ---");

        Account a1 = new SavingsAccount("Riya", 4000, 1000);
        System.out.println("toString: " + a1);

        Account a1Dup = new SavingsAccount(a1.getAccountNumber(), "Riya Dup", 4000, 1000);
        Account a2 = new CurrentAccount("Arjun", 2000, 3000);
        System.out.println("a1.equals(a1): " + a1.equals(a1));
        System.out.println("a1.equals(a1Dup) [same account number]: " + a1.equals(a1Dup));
        System.out.println("a1.equals(a2): " + a1.equals(a2));

        Object obj = a1;
        System.out.println("obj instanceof Account: " + (obj instanceof Account));

        Customer c = new Customer("Riya", "riya@example.com", "9876543210");
        Customer.Address addr = new Customer.Address("12 MG Road", "Anand", "388001");
        c.setAddress(addr);
        System.out.println("Customer: " + c);
        System.out.println("Address city: " + c.getAddress().getCity());

        Customer cloned = c.clone();
        System.out.println("Cloned customer: " + cloned);
        System.out.println("Original == Clone: " + (c == cloned));
        System.out.println("Original name equals clone: " + c.getName().equals(cloned.getName()));

        System.out.println("--- End of Practical 3 Demo ---");
    }

    private static void demonstrateValidatorsAndParsing() {
        System.out.println();
        System.out.println("--- Practical 4: Validators & Parsing Demo ---");

        System.out.println("isValidMobile(\"9876543210\"): " + isValidMobile("9876543210"));
        System.out.println("isValidMobile(\"12345\"):      " + isValidMobile("12345"));
        System.out.println("isValidEmail(\"abc@xyz.com\"): " + isValidEmail("abc@xyz.com"));
        System.out.println("isValidEmail(\"notanemail\"):  " + isValidEmail("notanemail"));
        System.out.println("isValidPan(\"ABCDE1234F\"):   " + isValidPan("ABCDE1234F"));
        System.out.println("isValidPan(\"12345\"):         " + isValidPan("12345"));
        System.out.println("isValidIfsc(\"SBIN0001234\"): " + isValidIfsc("SBIN0001234"));
        System.out.println("isValidIfsc(\"INVALID\"):      " + isValidIfsc("INVALID"));

        Command cmd = CommandParser.parse("DEPOSIT AC0001 500");
        System.out.println("Parsed command: type=" + cmd.type()
                + ", accountNumber=" + cmd.accountNumber()
                + ", amount=" + cmd.amount());

        try {
            CommandParser.parse("INVALID");
        } catch (IllegalArgumentException e) {
            System.out.println("Parse error (expected): " + e.getMessage());
        }

        Account acc = new SavingsAccount("Riya", 7000, 1000);
        System.out.println();
        System.out.println(StatementFormatter.buildStatement(acc));

        System.out.println("--- End of Practical 4 Demo ---");
    }

    private static void demonstrateInheritanceAndPolymorphism() {
        System.out.println();
        System.out.println("--- Practical 5: Inheritance & Polymorphism Demo ---");

        Account[] accounts = {
            new SavingsAccount("Riya", 5000, 1000),
            new CurrentAccount("Arjun", 2000, 5000),
            new FixedDepositAccount("Meera", 10000)
        };

        for (Account a : accounts) {
            System.out.println(a.getAccountNumber() + " | " + a.getOwnerName()
                    + " | Interest Rate: " + a.interestRate() + "%");

            if (a instanceof SavingsAccount sa) {
                System.out.println("   -> Subtype: SavingsAccount (Min Balance: Rs. " + sa.getMinBalance() + ")");
            } else if (a instanceof CurrentAccount ca) {
                System.out.println("   -> Subtype: CurrentAccount (Overdraft Limit: Rs. " + ca.getOverdraftLimit() + ")");
            } else if (a instanceof FixedDepositAccount) {
                System.out.println("   -> Subtype: FixedDepositAccount (Withdrawals Locked)");
            }
        }

        System.out.println("\nTesting subtype withdrawal rules:");
        try {
            System.out.println("Savings withdraw 4000: " + accounts[0].withdraw(4000)
                    + " (New Balance: " + accounts[0].getBalance() + ")");
        } catch (BankException e) {
            System.out.println("Savings withdraw 4000 caught: " + e.getMessage());
        }

        try {
            System.out.println("Savings withdraw 500:  " + accounts[0].withdraw(500)
                    + " (New Balance: " + accounts[0].getBalance() + ")");
        } catch (BankException e) {
            System.out.println("Savings withdraw 500 caught: " + e.getMessage());
        }

        try {
            System.out.println("Current withdraw 5000: " + accounts[1].withdraw(5000)
                    + " (New Balance: " + accounts[1].getBalance() + ")");
        } catch (BankException e) {
            System.out.println("Current withdraw 5000 caught: " + e.getMessage());
        }

        try {
            System.out.println("Current withdraw 3000: " + accounts[1].withdraw(3000)
                    + " (New Balance: " + accounts[1].getBalance() + ")");
        } catch (BankException e) {
            System.out.println("Current withdraw 3000 caught: " + e.getMessage());
        }

        try {
            System.out.println("FD withdraw 1000:      " + accounts[2].withdraw(1000)
                    + " (New Balance: " + accounts[2].getBalance() + ")");
        } catch (BankException e) {
            System.out.println("FD withdraw 1000 caught: " + e.getMessage());
        }

        System.out.println("--- End of Practical 5 Demo ---");
    }

    private static void demonstrateInterfacesAndLambdas() {
        System.out.println();
        System.out.println("--- Practical 6: Interfaces, Lambdas & Packages Demo ---");

        SavingsAccount sa = new SavingsAccount("Riya", 50000, 1000);
        CurrentAccount ca = new CurrentAccount("Arjun", 100000, 20000);

        System.out.println(sa.getOwnerName() + " (Savings @ " + sa.interestRate() + "%) Yearly Interest: Rs. "
                + sa.yearlyInterest() + " | Monthly: Rs. " + sa.monthlyInterest());

        WithdrawRule anonRule = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return amount <= 25000;
            }
        };

        WithdrawRule lambdaRule = (account, amount) -> amount <= 25000;

        System.out.println("AnonRule allow(20000):   " + anonRule.allow(sa, 20000));
        System.out.println("AnonRule allow(30000):   " + anonRule.allow(sa, 30000));
        System.out.println("LambdaRule allow(20000): " + lambdaRule.allow(sa, 20000));
        System.out.println("LambdaRule allow(30000): " + lambdaRule.allow(sa, 30000));

        System.out.println("Is SavingsAccount Premium? " + (sa instanceof Premium));
        System.out.println("Is CurrentAccount Premium? " + (ca instanceof Premium));

        AccountService.printAccountSummary(sa);

        System.out.println("--- End of Practical 6 Demo ---");
    }

    private static void demonstrateAnnotationsAndReflection() {
        System.out.println();
        System.out.println("--- Practical 7: Annotations & Reflection Demo ---");

        Account invalidAccount = new SavingsAccount("Riya", -100, 1000);
        String[] invalidErrors = AnnotationValidator.validate(invalidAccount);
        System.out.println("Validating account with balance -100:");
        for (String err : invalidErrors) {
            System.out.println("  Validation Error: " + err);
        }

        Account validAccount = new SavingsAccount("Riya", 5000, 1000);
        String[] validErrors = AnnotationValidator.validate(validAccount);
        System.out.println("Validating account with balance 5000: Errors count = " + validErrors.length);

        Account longNameAccount = new SavingsAccount("VeryLongOwnerNameExceedingTwentyCharacters", 5000, 1000);
        String[] maxLengthErrors = AnnotationValidator.validate(longNameAccount);
        System.out.println("Validating account with long owner name (>20 chars):");
        for (String err : maxLengthErrors) {
            System.out.println("  Validation Error: " + err);
        }

        System.out.println("--- End of Practical 7 Demo ---");
    }

    private static void demonstrateExceptionHandling() {
        System.out.println();
        System.out.println("--- Practical 8: Exception Handling Demo (Milestone 1) ---");

        Account acc = new SavingsAccount("Riya", 1000, 0);

        try {
            System.out.println("Attempting withdraw(5000) from balance " + acc.getBalance() + "...");
            acc.withdraw(5000);
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: short by " + e.getShortfall() + " (Balance: " + acc.getBalance() + ")");
        } catch (BankException e) {
            System.out.println("Generic bank failure: " + e.getMessage());
        }

        try {
            System.out.println("Attempting deposit(-200)...");
            acc.deposit(-200);
        } catch (InvalidAmountException e) {
            System.out.println("Caught InvalidAmountException: " + e.getMessage());
        }

        Account source = new SavingsAccount("Riya", 5000, 0);
        Account target = new SavingsAccount("Arjun", 2000, 0);
        System.out.println("\nInitial: Source Rs. " + source.getBalance() + ", Target Rs. " + target.getBalance());

        try {
            System.out.println("Executing transfer(1500) from Source to Target...");
            source.transfer(target, 1500);
            System.out.println("Transfer successful!");
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } finally {
            System.out.println("Finally block executed. Current Balances: Source="
                    + source.getBalance() + ", Target=" + target.getBalance());
        }

        try {
            System.out.println("\nExecuting transfer(20000) exceeding Source balance...");
            source.transfer(target, 20000);
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer rejected: short by " + e.getShortfall());
        } catch (BankException e) {
            System.out.println("Transfer error: " + e.getMessage());
        } finally {
            System.out.println("Integrity Check: Source=" + source.getBalance() + ", Target=" + target.getBalance());
        }

        System.out.println("\nDemonstrating try-with-resources:");
        try (TransactionSession session = new TransactionSession("TX-8801")) {
            System.out.println("Operating inside try-with-resources block...");
            source.withdraw(500);
            System.out.println("Withdrawn Rs. 500 in session " + session.getSessionId());
        } catch (BankException e) {
            System.out.println("Session error: " + e.getMessage());
        }

        System.out.println("--- End of Practical 8 Demo ---");
    }

    private static void demonstrateMultithreadingAndSynchronization() {
        System.out.println();
        System.out.println("--- Practical 9: Multithreading & Synchronization Demo ---");

        int threadCount = 10;
        int depositsPerThread = 1000;
        long amountPerDeposit = 1;
        long expectedTotal = threadCount * depositsPerThread * amountPerDeposit;

        System.out.println("Phase 1: Demonstrating Unsynchronized Race Condition on UnsafeAccount...");
        UnsafeAccount unsafeAccount = new UnsafeAccount(0);
        Thread[] unsafeThreads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            unsafeThreads[i] = new Thread(() -> {
                for (int j = 0; j < depositsPerThread; j++) {
                    unsafeAccount.deposit(amountPerDeposit);
                }
            }, "Unsafe-Thread-" + (i + 1));
            unsafeThreads[i].start();
        }

        for (Thread t : unsafeThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Expected Balance: " + expectedTotal);
        System.out.println("Actual Unsynchronized Balance: " + unsafeAccount.getBalance()
                + " (Notice: data loss due to race condition!)");

        System.out.println("\nPhase 2: Running 10 Concurrent AccountWorker Threads on Synchronized Account...");
        Account safeAccount = new SavingsAccount("Concurrent Test", 0, 0);
        Thread[] safeThreads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            AccountWorker worker = new AccountWorker(safeAccount, depositsPerThread, amountPerDeposit);
            safeThreads[i] = new Thread(worker, "Safe-Worker-" + (i + 1));
            safeThreads[i].start();
        }

        for (Thread t : safeThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Expected Balance: " + expectedTotal);
        System.out.println("Actual Synchronized Balance: " + safeAccount.getBalance()
                + " (Exact match every run — thread safe!)");

        System.out.println("--- End of Practical 9 Demo ---");
    }

    private static void demonstrateTransactionProcessingAndDeadlock() {
        System.out.println();
        System.out.println("--- Practical 10: Transaction Processor & Deadlock Demo ---");

        System.out.println("Phase 1: Batch Processing on Managed Thread Pool (size 4)...");
        Account batchAccount = new SavingsAccount("Batch User", 5000, 0);
        TransactionProcessor processor = new TransactionProcessor(4);

        for (int i = 0; i < 20; i++) {
            final int txNum = i + 1;
            processor.submit(() -> {
                try {
                    if (txNum % 2 == 0) {
                        batchAccount.deposit(100);
                    } else {
                        batchAccount.withdraw(50);
                    }
                } catch (BankException ignored) {}
            });
        }
        processor.stop();
        System.out.println("Batch execution finished. Account balance: Rs. " + batchAccount.getBalance()
                + " (Expected: 5500)");

        System.out.println("\nPhase 2: Producer-Consumer Coordination with wait()/notify()...");
        TransactionBuffer buffer = new TransactionBuffer(2);
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 4; i++) {
                    String tx = "TX-BATCH-" + i;
                    buffer.put(tx);
                    System.out.println("  [Producer] Put into buffer: " + tx);
                    Thread.sleep(30);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Buffer-Producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 4; i++) {
                    String tx = buffer.take();
                    System.out.println("  [Consumer] Took from buffer: " + tx);
                    Thread.sleep(60);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Buffer-Consumer");

        consumer.start();
        producer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException ignored) {}

        System.out.println("\nPhase 3: Deadlock Demonstration and Lock-Ordering Prevention...");
        Account accA = new SavingsAccount("AC0011", "Alice", 10000, 0);
        Account accB = new SavingsAccount("AC0012", "Bob", 10000, 0);

        DeadlockDemo.demonstrateOppositeLocking(accA, accB);

        System.out.println("  [Safe Lock-Ordering Transfer] Transferring A -> B and B -> A concurrently...");
        Thread tSafe1 = new Thread(() -> {
            try {
                DeadlockDemo.safeTransfer(accA, accB, 2000);
            } catch (BankException e) {
                System.out.println("Safe transfer 1 error: " + e.getMessage());
            }
        });
        Thread tSafe2 = new Thread(() -> {
            try {
                DeadlockDemo.safeTransfer(accB, accA, 1000);
            } catch (BankException e) {
                System.out.println("Safe transfer 2 error: " + e.getMessage());
            }
        });

        tSafe1.start();
        tSafe2.start();

        try {
            tSafe1.join();
            tSafe2.join();
        } catch (InterruptedException ignored) {}

        System.out.println("  Safe transfers completed! Final balances: Alice="
                + accA.getBalance() + " (Expected 9000), Bob=" + accB.getBalance() + " (Expected 11000)");

        System.out.println("--- End of Practical 10 Demo ---");
    }

    private static void demonstratePersistenceAndReports() {
        System.out.println();
        System.out.println("--- Practical 11: Persistence, Logs & Reports Demo ---");

        Path dataFile = Path.of("data/accounts.dat");
        Path logsDir = Path.of("logs");
        Path logFile = logsDir.resolve("transactions.log");
        Path reportFile = logsDir.resolve("eod_report.txt");

        Account[] originalAccounts = {
            new SavingsAccount("Riya", 5000, 1000),
            new CurrentAccount("Arjun", 2000, 5000),
            new FixedDepositAccount("Meera", 10000)
        };

        try {
            System.out.println("Saving " + originalAccounts.length + " accounts to " + dataFile + "...");
            StatePersister.save(originalAccounts, dataFile);
            System.out.println("Accounts successfully saved.");

            System.out.println("\nReloading accounts from " + dataFile + "...");
            Account[] loadedAccounts = StatePersister.load(dataFile);
            System.out.println("Loaded " + loadedAccounts.length + " accounts:");
            for (Account a : loadedAccounts) {
                System.out.println("  Restored: " + a);
            }

            Account freshAccount = new SavingsAccount("Kavya", 3000, 500);
            System.out.println("Newly created account after reload: " + freshAccount.getAccountNumber()
                    + " (Confirmed no ID collision!)");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("State persistence error: " + e.getMessage());
        }

        try {
            System.out.println("\nLogging transactions to " + logFile + "...");
            TransactionLog.append(logFile, "DEPOSIT AC0001 500");
            TransactionLog.append(logFile, "WITHDRAW AC0002 200");
            TransactionLog.append(logFile, "DEPOSIT AC0003 1000");
            System.out.println("Appended 3 transactions to log.");

            System.out.println("\nGenerating report from logs directory (" + logsDir + ")...");
            String report = ReportGenerator.generateReport(logsDir, reportFile);
            System.out.println(report);
            System.out.println("Report written to " + reportFile);

        } catch (IOException e) {
            System.err.println("Logging/Report error: " + e.getMessage());
        }

        System.out.println("--- End of Practical 11 Demo ---");
    }

    private static void demonstrateCollectionsAndSorting() {
        System.out.println();
        System.out.println("--- Practical 12: Collections Framework Demo (Milestone 2) ---");

        AccountStore store = new AccountStore();

        Account acc1 = new SavingsAccount("Riya", 5000, 500);
        Account acc2 = new CurrentAccount("Arjun", 2000, 2000);
        Account acc3 = new SavingsAccount("Meera", 9000, 500);

        store.add(acc1);
        store.add(acc2);
        store.add(acc3);

        System.out.println("Lookup by account number (" + acc2.getAccountNumber() + "):");
        Account found = store.get(acc2.getAccountNumber());
        System.out.println("  Result: " + found);

        System.out.println("\nAccounts sorted by balance (Descending):");
        ArrayList<Account> sorted = store.sortedByBalance();
        for (Account a : sorted) {
            System.out.println("  " + a.getAccountNumber() + " | " + a.getOwnerName()
                    + " | Balance: Rs. " + a.getBalance());
        }

        System.out.println("\nAccounts sorted by natural order (accountNumber via Comparable):");
        List<Account> naturalOrder = new ArrayList<>(store.getAll());
        Collections.sort(naturalOrder);
        for (Account a : naturalOrder) {
            System.out.println("  " + a.getAccountNumber() + " | " + a.getOwnerName()
                    + " | Balance: Rs. " + a.getBalance());
        }

        System.out.println("\nTransaction History from AccountStore:");
        for (String record : store.getTransactionHistory()) {
            System.out.println("  [History] " + record);
        }

        System.out.println("--- End of Practical 12 Demo ---");
    }

    private static void demonstrateGenericsAndRepository() {
        System.out.println();
        System.out.println("--- Practical 13: Generics & Generic Repository Demo ---");

        Repository<Account> accountRepo = new Repository<>();
        Account a1 = new SavingsAccount("Riya", 5000, 1000);
        Account a2 = new CurrentAccount("Arjun", 2000, 5000);
        Account a3 = new FixedDepositAccount("Meera", 10000);

        accountRepo.saveAll(a1, a2, a3);
        System.out.println("Saved " + accountRepo.count() + " accounts into generic Repository<Account>.");

        System.out.println("\nRepository findById(" + a2.getAccountNumber() + "):");
        Account fetched = accountRepo.findById(a2.getAccountNumber());
        System.out.println("  Found: " + fetched);

        System.out.println("\nRepository findAll():");
        for (Account a : accountRepo.findAll()) {
            System.out.println("  " + a);
        }

        System.out.println("\nReusing Repository<T> for Customer objects:");
        Repository<Customer> customerRepo = new Repository<>();
        Customer c1 = new Customer("Riya", "riya@example.com", "9876543210");
        Customer c2 = new Customer("Arjun", "arjun@example.com", "9123456780");
        customerRepo.saveAll(c1, c2);

        System.out.println("Saved " + customerRepo.count() + " customers. Lookup " + c1.getCustomerId() + ":");
        Customer foundCustomer = customerRepo.findById(c1.getCustomerId());
        System.out.println("  Found Customer: " + foundCustomer);

        Long[] accountBalances = { a1.getBalance(), a2.getBalance(), a3.getBalance() };
        double avgBalance = Repository.average(accountBalances);
        System.out.println("\nBounded Generic average() of balances: Rs. " + String.format("%.2f", avgBalance));

        Double[] interestRates = { a1.interestRate(), a2.interestRate(), a3.interestRate() };
        double avgRate = Repository.average(interestRates);
        System.out.println("Bounded Generic average() of interest rates: " + String.format("%.2f", avgRate) + "%");

        System.out.println("--- End of Practical 13 Demo ---");
    }
}
