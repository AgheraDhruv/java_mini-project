# MiniBank — Java Console Banking Application

**Student Name:** Bhavy Pan  
**Enrollment No.:** 25CE098  
**Subject:** Object-Oriented Programming (CEUE203)  
**Academic Year:** 2026–27 | Semester 3 (ODD)  
**Institute:** CHARUSAT — Faculty of Technology & Engineering (CSPIT)  
**Project:** MiniBank — Comprehensive Console Banking System  
**Repository:** [https://github.com/AgheraDhruv/OPP_MiniBank.git](https://github.com/AgheraDhruv/OPP_MiniBank.git)  
**Branch:** `main` | **Target JDK:** Java 17+ (tested with OpenJDK 26)

---

## 1. Project Overview & Architecture

**MiniBank** is an enterprise-pattern, console-based banking application built strictly according to the CHARUSAT CEUE203 practical curriculum. Rather than a monolithic single-pass project, MiniBank was developed incrementally across **14 distinct practical stages (Part B)**. Every single practical builds on the foundational codebase of previous practicals, demonstrating clean software evolution, object-oriented design, robustness, and architectural separation of concerns.

### Architectural Layering

```
minibank/
├── src/
│   ├── MiniBank.java                    # Entry point & interactive CLI controller
│   ├── model/                          # Domain Entities & Value Objects
│   │   ├── BankInfo.java               # Bank metadata (Record)
│   │   ├── MenuOption.java             # Menu commands (Enum)
│   │   ├── TransactionType.java        # Operation kinds (Enum)
│   │   ├── Command.java                # Parsed user command (Record)
│   │   ├── Customer.java               # Customer entity (Cloneable, nested Address)
│   │   ├── Account.java                # Abstract base entity (Comparable, Transactable)
│   │   ├── SavingsAccount.java         # Subtype: Minimum balance, 4.0% interest
│   │   ├── CurrentAccount.java         # Subtype: Overdraft limit, 0.0% interest
│   │   ├── FixedDepositAccount.java    # Subtype: Locked withdrawals, 7.0% interest
│   │   ├── Transactable.java           # Core transaction contract (Interface)
│   │   ├── InterestBearing.java        # Default method for yearly interest calculation
│   │   ├── WithdrawRule.java           # Custom rule evaluation (Functional Interface)
│   │   ├── Premium.java                # Account tier marker (Marker Interface)
│   │   └── annotation/                 # Custom Runtime Annotations
│   │       ├── Id.java                 # Primary key marker
│   │       ├── Positive.java           # Numeric constraint validator
│   │       ├── MaxLength.java          # String length constraint validator
│   │       └── Audit.java              # Reflection-based audit logger
│   ├── exception/                      # Checked Banking Exception Hierarchy
│   │   ├── BankException.java          # Base checked application exception
│   │   ├── InsufficientFundsException.java # Thrown with shortfall metadata
│   │   ├── AccountNotFoundException.java   # Thrown when lookup fails
│   │   └── InvalidAmountException.java     # Thrown on negative or zero amounts
│   ├── service/                        # Business Logic & Infrastructure Services
│   │   ├── BankService.java            # Unified facade coordinating bank operations
│   │   ├── Repository.java             # Generic type-safe entity repository (Generics)
│   │   ├── AccountStore.java           # Specialized collection store with sorting
│   │   ├── AccountService.java         # Account rule orchestration
│   │   ├── AccountWorker.java          # Runnable worker for concurrent testing
│   │   ├── UnsafeAccount.java          # Deliberately un-synchronized race condition demo
│   │   ├── TransactionProcessor.java   # Fixed thread pool executor & batch processor
│   │   ├── TransactionBuffer.java      # Thread-safe bounded buffer (wait / notifyAll)
│   │   └── DeadlockDemo.java           # Circular-wait demonstration & prevention
│   └── util/                           # Utilities & IO Helpers
│       ├── Validator.java              # Pre-compiled Regex validation (Email, PAN, Mobile, IFSC)
│       ├── CommandParser.java          # Command-line string parser
│       ├── StatementFormatter.java     # StringBuilder statement generator
│       ├── AnnotationValidator.java    # Reflection engine validating annotated constraints
│       ├── TransactionSession.java     # AutoCloseable session for try-with-resources
│       ├── StatePersister.java         # ObjectOutputStream/ObjectInputStream serializer
│       ├── TransactionLog.java         # NIO-based append-only audit trail
│       └── ReportGenerator.java        # DirectoryStream & NIO file attribute analytics
├── test/
│   └── MiniBankTest.java               # Automated JUnit 5 Unit Test Suite
├── Manifest.txt                        # JAR manifest specifying Main-Class: MiniBank
└── docs/
    └── PROGRESS.md                     # Step-by-step practical implementation log
```

---

## 2. Practical Progression & Feature Matrix

| Practical | Tag | Core Concepts | Key Classes & Methods | Status |
| :--- | :--- | :--- | :--- | :---: |
| **Lab 01** | `lab-01` | Project setup, `record`, `enum`, switch expressions, Scanner CLI | `MiniBank`, `BankInfo`, `MenuOption` | Completed |
| **Lab 02** | `lab-02` | Encapsulation, static counters, constructor chaining (`this()`) | `Customer`, `Account` (`CUST101`, `AC0001`) | Completed |
| **Lab 03** | `lab-03` | `toString()`, `equals()` & `hashCode()`, static nested class, `clone()` | `Account.equals()`, `Customer.Address`, `Customer.clone()` | Completed |
| **Lab 04** | `lab-04` | Regex `Pattern`, string parsing, `StringBuilder` statement builder | `Validator`, `CommandParser`, `StatementFormatter` | Completed |
| **Lab 05** | `lab-05` | Abstract classes, inheritance, subtype withdrawal, dynamic dispatch | `SavingsAccount`, `CurrentAccount`, `FixedDepositAccount` | Completed |
| **Lab 06** | `lab-06` | Interfaces, default methods, lambdas, packages, runnable JAR | `Transactable`, `InterestBearing`, `WithdrawRule`, `Premium` | Completed |
| **Lab 07** | `lab-07` | Custom annotations (`@Id`, `@Positive`), Java reflection inspection | `@Target`, `@Retention`, `AnnotationValidator.validate()` | Completed |
| **Lab 08** | `lab-08` | Checked exceptions, atomic transfer rollback, `try-with-resources` | `BankException` hierarchy, `TransactionSession` | Completed |
| **Lab 09** | `lab-09` | Multithreading, race conditions, thread safety, `synchronized` | `AccountWorker`, `UnsafeAccount`, synchronized `deposit()` | Completed |
| **Lab 10** | `lab-10` | Thread pools (`ExecutorService`), `wait()`/`notifyAll()`, deadlock prevention | `TransactionProcessor`, `TransactionBuffer`, `DeadlockDemo` | Completed |
| **Lab 11** | `lab-11` | Object serialization, NIO `Files`, audit logs, EOD summary report | `StatePersister`, `TransactionLog`, `ReportGenerator` | Completed |
| **Lab 12** | `lab-12` | Collections Framework (`HashMap`, `ArrayList`, `ConcurrentHashMap`), sorting | `AccountStore`, `Comparable`, `Comparator` | Completed |
| **Lab 13** | `lab-13` | Generic `Repository<T>`, `@SafeVarargs`, bounded type parameter | `Repository<T>`, `<N extends Number> average(N[])` | Completed |
| **Lab 14** | `lab-14` | Full system integration, `@Audit`, JUnit 5 testing, lifecycle management | `BankService`, `MiniBankTest`, standalone JUnit runner | Completed |

---

## 3. How to Compile, Build, Run, and Test

Ensure JDK 17 or higher is installed and available in your environment path.

### 3.1. Compilation
From the project root directory:

```bash
# Windows PowerShell / CMD
javac -d out (Get-ChildItem -Path src -Recurse -Filter "*.java")

# Linux / macOS Bash
javac -d out $(find src -name "*.java")
```

### 3.2. Running the Interactive Console Application
Launch MiniBank with the interactive text menu:

```bash
java -cp out MiniBank
```

**Interactive Menu Options:**
1. `OPEN_ACCOUNT`: Create a new Savings, Current, or Fixed Deposit account.
2. `DEPOSIT`: Deposit funds into an account with atomic balance updating.
3. `WITHDRAW`: Withdraw funds with account-specific constraint checking.
4. `TRANSFER`: Atomically transfer funds between accounts with rollback on failure.
5. `STATEMENT`: View a formatted account statement with KYC metadata.
6. `LIST_ACCOUNTS`: List all registered accounts sorted descending by balance.
7. `BATCH_PROCESS`: Submit concurrent transaction batches using a thread pool.
8. `REPORT`: Generate an end-of-day summary of NIO audit logs.
9. `RUN_DEMO_SUITE`: Run the integrated automated demonstration covering Practicals 1 to 14.
10. `EXIT`: Safely persist accounts to disk (`data/accounts.dat`) and terminate.

### 3.3. Building the Runnable JAR
Package the application into an executable JAR file with the pre-configured manifest (`Manifest.txt`):

```bash
jar -cvfm minibank.jar Manifest.txt -C out .
```

### 3.4. Executing the Runnable JAR
Run the standalone JAR directly on any system with Java installed:

```bash
java -jar minibank.jar
```

### 3.5. Running the JUnit 5 Unit Test Suite
MiniBank includes a comprehensive JUnit 5 test suite verifying core business logic. Run the tests via the included console runner:

```bash
# Windows PowerShell
java -jar lib/junit-platform-console-standalone-1.10.2.jar -cp "out;test" --select-class MiniBankTest

# Linux / macOS Bash
java -jar lib/junit-platform-console-standalone-1.10.2.jar -cp "out:test" --select-class MiniBankTest
```

**Test Suite Coverage:**
- `testDeposit()`: Verifies positive balance updates and negative amount rejections.
- `testWithdrawInsufficientFundsThrowsException()`: Confirms `InsufficientFundsException` is raised with the exact shortfall.
- `testAtomicTransferRollback()`: Asserts that failed transfers preserve original balances.
- `testValidationRejection()`: Tests regex validation on invalid mobile, email, PAN, and IFSC formats.
- `testGenericRepositoryCrud()`: Tests polymorphic generic storage, key lookup, count, and entity removal.

---

## 4. Practical Implementation Highlights

### Practical 1 — Record, Enum & Modern Switch
- **`BankInfo` Record:** Introduced in Java 14/16 as transparent data carriers. Generates immutable fields, constructor, getters, `equals()`, `hashCode()`, and `toString()` automatically.
- **`MenuOption` Enum:** Type-safe menu constants eliminating magic integers or strings.
- **Switch Expression:** Uses arrow syntax (`->`) avoiding verbose `break` statements and fall-through bugs.

### Practical 2 — Encapsulation & Constructor Chaining
- All entity fields are `private` and accessed via public getters.
- `Account.balance` has no public setter; balance can only be mutated through guarded `deposit()` and `withdraw()` business methods.
- Auto-incrementing static counters produce standardized business identifiers (`CUST101`, `AC0001`).
- Constructor chaining (`this(...)`) eliminates code duplication across multiple constructor overloads.

### Practical 3 — Object Contract & Deep Cloning
- Overrides `Account.equals()` and `Account.hashCode()` based strictly on unique `accountNumber`, adhering to the Java Object specification.
- Public static nested class `Customer.Address` groups address attributes logically without polluting the global namespace.
- `Customer.clone()` implements deep copying of the nested `Address` instance, ensuring true prototype immutability.

### Practical 4 — Regex Validation & Command Parsing
- `Validator` pre-compiles regex patterns (`java.util.regex.Pattern`) for maximum execution efficiency.
- Validates Indian mobile numbers (`^[6-9]\d{9}$`), standard emails, PAN cards (`^[A-Z]{5}[0-9]{4}[A-Z]{1}$`), and IFSC codes (`^[A-Z]{4}0[A-Z0-9]{6}$`).
- `StatementFormatter` uses `StringBuilder` for efficient string assembly, avoiding intermediate immutable string allocations.

### Practical 5 — Polymorphic Account Hierarchy
- `Account` transformed into an abstract base class with `abstract double interestRate()` and `abstract boolean canWithdraw(long amount)`.
- Subtypes define distinct banking rules:
  - `SavingsAccount`: 4.0% interest; requires maintaining a minimum balance of Rs. 1,000.
  - `CurrentAccount`: 0.0% interest; allows an overdraft buffer of Rs. 5,000 beyond zero.
  - `FixedDepositAccount`: 7.0% interest; strictly locks principal against early withdrawals.
- Dynamic method dispatch executes subtype-specific behavior polymorphically through base `Account` references.

### Practical 6 — Interfaces, Lambdas & Package Structure
- `Transactable`: Defines core banking behavior (`deposit`, `withdraw`).
- `InterestBearing`: Provides a `default` method `yearlyInterest()` computing interest from balance and rate.
- `WithdrawRule`: Single Abstract Method (SAM) functional interface evaluated via both anonymous inner classes and Java 8 lambda expressions (`(acc, amt) -> acc.canWithdraw(amt)`).
- Clean package partitioning: `model`, `service`, `util`, `exception`.

### Practical 7 — Custom Annotations & Reflection Engine
- Custom runtime annotations `@Id`, `@Positive`, and `@MaxLength` with `@Retention(RetentionPolicy.RUNTIME)` and `@Target(ElementType.FIELD)`.
- `AnnotationValidator.validate(Object)` inspects private fields using `getDeclaredFields()`, traverses superclass hierarchies (`clazz.getSuperclass()`), applies `field.setAccessible(true)`, and accumulates descriptive validation errors.

### Practical 8 — Checked Exception Hierarchy & Atomic Transactions
- Custom checked exception tree rooted at `BankException`:
  - `InsufficientFundsException` encapsulates `shortfall` (required minus available).
  - `AccountNotFoundException` and `InvalidAmountException`.
- Atomic balance transfers implemented with explicit rollback in `try-catch-finally` to ensure consistency.
- `TransactionSession` implements `AutoCloseable`, managing transactional boundaries cleanly via `try-with-resources`.

### Practical 9 — Multithreading & Method Synchronization
- `AccountWorker` implements `Runnable` executing concurrent deposits.
- `UnsafeAccount` demonstrates real-world race conditions and lost updates under 10 threads × 1,000 iterations (yielding ~25% balance loss).
- Synchronized methods (`synchronized void deposit(...)`) eliminate data races, producing exact deterministic balances.

### Practical 10 — Thread Pools & Deadlock Prevention
- `TransactionProcessor` coordinates execution across an `ExecutorService` fixed thread pool of 4 workers.
- `TransactionBuffer` implements a thread-safe bounded circular array coordinated with `wait()` and `notifyAll()`.
- `DeadlockDemo` reproduces classic dining-philosopher circular wait when two threads lock accounts in reverse order.
- **Deadlock Resolution:** Consistent global lock ordering sorting account numbers (`a.compareTo(b) < 0 ? lock a then b : lock b then a`) guarantees cycle-free lock acquisition.

### Practical 11 — Persistence, NIO & File Analytics
- `Account` and `Customer` implement `Serializable` with explicit `serialVersionUID`.
- `StatePersister` saves and reloads accounts via `ObjectOutputStream` and `ObjectInputStream`.
- Static ID counters dynamically resynchronize based on reloaded records, completely preventing identifier collisions.
- `TransactionLog` logs every transaction to an append-only text file using `java.nio.file.Files`.
- `ReportGenerator` traverses logs using `Files.newDirectoryStream` and analyzes metadata via `BasicFileAttributes`.

### Practical 12 — Collections Framework & Custom Sorting
- `AccountStore` backs accounts with `HashMap<String, Account>` for O(1) key lookups and `ArrayList<String>` for audit order.
- `Account` implements `Comparable<Account>` establishing natural ordering by `accountNumber`.
- `sortedByBalance()` uses `Comparator<Account>` to sort accounts in descending balance order without modifying stored collections.

### Practical 13 — Generic Repository & Bounded Wildcards
- `Repository<T>` provides full type-safe CRUD operations backed by an internal map.
- Uses Java reflection to dynamically extract keys marked with `@Id` across any entity hierarchy.
- Features `@SafeVarargs public final void saveAll(T... items)`.
- Implements bounded generic method `<N extends Number> double average(N[] values)` demonstrating bounded type parameters.

### Practical 14 — Integration, Auditing & Unit Testing
- `@Audit` custom annotation tracks method execution via reflection.
- `BankService` acts as a unified facade coordinating validation, repository storage, concurrent execution, transaction logging, and state serialization.
- Fully automated JUnit 5 test suite ensures zero regression across banking invariants.

---

## 5. Comprehensive Viva Preparation Guide

This section provides answers and key explanations for viva voce examiners covering all 14 practicals and core OOPJ concepts.

### 5.1. Java Platform & Architecture
- **What is the difference between JDK, JRE, and JVM?**
  - **JDK (Java Development Kit):** Full development environment containing the compiler (`javac`), archiver (`jar`), debugging tools, and the JRE.
  - **JRE (Java Runtime Environment):** Execution environment containing core class libraries (`rt.jar` / `java.base`) and the JVM.
  - **JVM (Java Virtual Machine):** Abstract computing machine that executes compiled Java bytecode (`.class` files). Provides platform independence ("Write Once, Run Anywhere").
- **What is the JVM Memory Model?**
  - **Method Area / Metaspace:** Stores class metadata, bytecode, static fields, and constant pools.
  - **Heap Memory:** Shared runtime memory where all objects and array instances are allocated. Managed by the Garbage Collector.
  - **Java Thread Stack:** Stores local primitive variables, method call frames, and object references for each executing thread.
  - **PC Register:** Holds the address of the currently executing JVM instruction.
  - **Native Method Stack:** Supports execution of native (C/C++) methods via JNI.

### 5.2. OOP Fundamentals & Encapsulation
- **What are the four pillars of Object-Oriented Programming?**
  - **Encapsulation:** Bundling state and behavior together while restricting direct access to internal state using `private` access modifiers and controlled public methods.
  - **Abstraction:** Hiding complex implementation details and showing only necessary interfaces to callers (via `abstract` classes and `interface`).
  - **Inheritance:** Deriving new classes from existing classes to promote code reuse and establish is-a relationships (`extends`).
  - **Polymorphism:** The ability of objects of different types to respond to the same message in subtype-specific ways ("one interface, multiple implementations").
- **Why shouldn't `Account` have a public `setBalance(long balance)`?**
  - Providing a balance setter breaks encapsulation and violates banking integrity. Balance can only be mutated through business-validated transactions (`deposit` and `withdraw`). A setter would allow external code to silently overwrite balances without audit trails or constraint checks.
- **What is constructor chaining using `this()`?**
  - Calling another constructor within the same class using `this(arguments)`. Must be the very first statement in the constructor body. Prevents redundant initialization logic.

### 5.3. Object Methods Contract & Immutability
- **What is the contract between `equals()` and `hashCode()`?**
  1. If two objects are equal according to `equals(Object)`, they must have the exact same `hashCode()`.
  2. If two objects have the same `hashCode()`, they are not necessarily equal (hash collision).
  3. If `equals()` is overridden, `hashCode()` MUST also be overridden. Violating this breaks hash-based collections like `HashMap` and `HashSet`.
- **What is the difference between shallow copy and deep copy in `clone()`?**
  - **Shallow Copy (`super.clone()`):** Copies primitive values and field references as-is. Both original and clone point to the exact same child objects in memory.
  - **Deep Copy:** Explicitly duplicates nested objects (e.g., `clonedCustomer.address = this.address.clone()`), ensuring mutations to the clone do not affect the original.

### 5.4. Polymorphism & Abstract Classes vs. Interfaces
- **When should you use an Abstract Class vs. an Interface?**
  - **Abstract Class:** Use when classes share common state (non-static instance fields), constructors, or code (`Account` with balance and owner fields). Java supports single class inheritance.
  - **Interface:** Use to define a contract or behavior across unrelated classes (`Transactable`, `Serializable`). A class can implement multiple interfaces.
- **What are Java 8 default methods in interfaces?**
  - Methods declared with the `default` keyword inside an interface that provide a default implementation (e.g., `InterestBearing.yearlyInterest()`). They allow adding new methods to interfaces without breaking existing implementing classes.
- **What is a Functional Interface and a Lambda Expression?**
  - A functional interface is an interface with exactly one abstract method (annotated with `@FunctionalInterface`).
  - A lambda expression is a concise representation of an anonymous function that can be passed around as data, implementing the single abstract method directly.

### 5.5. Annotations & Reflection
- **What are `@Target` and `@Retention`?**
  - **`@Target`:** Meta-annotation specifying where an annotation can be placed (`ElementType.FIELD`, `ElementType.METHOD`, `ElementType.TYPE`).
  - **`@Retention`:** Meta-annotation specifying how long the annotation is retained:
    - `SOURCE`: Discarded by the compiler.
    - `CLASS`: Recorded in bytecode but ignored by JVM at runtime.
    - `RUNTIME`: Retained by the JVM at runtime and readable via Java Reflection (`RetentionPolicy.RUNTIME`).
- **How does Java Reflection work?**
  - Reflection allows inspecting and modifying classes, interfaces, fields, and methods at runtime without knowing their names at compile time (`Class.forName()`, `getDeclaredFields()`, `setAccessible(true)`). It powers frameworks like Spring, Hibernate, and JUnit.

### 5.6. Exception Handling & Robustness
- **What is the difference between Checked and Unchecked Exceptions?**
  - **Checked Exceptions (extend `Exception` but not `RuntimeException`):** Anticipated exceptional conditions that a recoverable application must catch or declare in `throws` (e.g., `BankException`, `IOException`). Checked at compile time.
  - **Unchecked Exceptions (extend `RuntimeException`):** Programming errors, bugs, or logic defects (e.g., `NullPointerException`, `IndexOutOfBoundsException`). Not enforced by the compiler.
- **How does `try-with-resources` work?**
  - Any resource implementing `java.lang.AutoCloseable` or `java.io.Closeable` opened in a `try(...)` header is automatically closed at the end of the block, even if an exception is thrown, preventing memory and file descriptor leaks.

### 5.7. Concurrency, Race Conditions & Deadlock
- **What is a Race Condition?**
  - A situation where multiple threads read and write shared mutable memory concurrently without synchronization, leading to non-deterministic, corrupted results (e.g., lost updates in `UnsafeAccount`).
- **What does the `synchronized` keyword do?**
  - Acquires an intrinsic lock (monitor) on the target object. Only one thread can execute a synchronized block or method on that object at any given time, guaranteeing mutual exclusion and memory visibility across threads.
- **What is a Deadlock and how is it resolved?**
  - A deadlock occurs when two or more threads are blocked forever, each waiting for a lock held by the other (circular wait condition).
  - **Resolution:** Enforce a consistent global lock acquisition order across all threads. For example, always lock the account with the smaller `accountNumber` first.

### 5.8. Collections & Generics
- **What is the difference between `Comparable` and `Comparator`?**
  - **`Comparable<T>`:** Defines the natural ordering of a class by implementing `compareTo(T other)` within that class.
  - **`Comparator<T>`:** Defines external, custom sorting strategies by implementing `compare(T o1, T o2)` in a separate class or lambda.
- **What is Type Erasure in Java Generics?**
  - The process where the Java compiler translates generic types into raw types or upper bounds at compile time, inserting casts and erasing generic type parameters from the generated bytecode for backward compatibility with older JVM versions.

---

## 6. Verification and Test Results

### JUnit 5 Standalone Test Run Log

```text
Thanks for using JUnit! Support its development at https://junit.org/sponsoring

╷
├─ JUnit Platform Suite
├─ JUnit Jupiter
│  └─ MiniBankTest
│     ├─ testValidationRejection() [OK]
│     ├─ testDeposit() [OK]
│     ├─ testWithdrawInsufficientFundsThrowsException() [OK]
│     ├─ testAtomicTransferRollback() [OK]
│     └─ testGenericRepositoryCrud() [OK]
└─ JUnit Vintage
   └─ MiniBankTest [OK]

Test run finished after 178 ms
[         3 containers found      ]
[         0 containers skipped    ]
[         3 containers started    ]
[         0 containers aborted    ]
[         3 containers successful ]
[         0 containers failed     ]
[         6 tests found           ]
[         0 tests skipped         ]
[         6 tests started         ]
[         0 tests aborted         ]
[         6 tests successful      ]
[         0 tests failed          ]
```

---

## 7. Project Metadata

- **Author:** Bhavy Pan (25CE098)
- **Course:** CEUE203 — Object-Oriented Programming (Java)
- **Institution:** Charotar University of Science and Technology (CHARUSAT)
- **Date:** October 2026
- **License:** Educational Project for Academic Evaluation
