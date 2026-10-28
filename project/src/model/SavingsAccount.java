package model;

/**
 * SavingsAccount — account type with minimum balance requirement and 4.0% interest rate.
 * Extends Account. Serializable via inheritance.
 */
public class SavingsAccount extends Account {

    private static final long serialVersionUID = 2002L;

    private long minBalance;

    public SavingsAccount(String ownerName, long openingBalance, long minBalance) {
        super(ownerName, openingBalance);
        this.minBalance = minBalance;
    }

    public SavingsAccount(String ownerName, long openingBalance) {
        this(ownerName, openingBalance, 1000);
    }

    public SavingsAccount(String ownerName) {
        this(ownerName, 0, 1000);
    }

    public SavingsAccount(String accountNumber, String ownerName, long openingBalance, long minBalance) {
        super(accountNumber, ownerName, openingBalance);
        this.minBalance = minBalance;
    }

    public long getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(long minBalance) {
        this.minBalance = minBalance;
    }

    @Override
    public double interestRate() {
        return 4.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= minBalance;
    }

    @Override
    public String toString() {
        return super.toString() + " | Savings (Min: " + minBalance + ", Rate: " + interestRate() + "%)";
    }
}
