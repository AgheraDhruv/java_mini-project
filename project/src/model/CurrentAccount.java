package model;

/**
 * CurrentAccount — account type with overdraft facility and 0.0% interest rate.
 * Extends Account. Allows balance to drop down to -overdraftLimit.
 */
public class CurrentAccount extends Account implements Premium {

    private static final long serialVersionUID = 2003L;

    private long overdraftLimit;

    public CurrentAccount(String ownerName, long openingBalance, long overdraftLimit) {
        super(ownerName, openingBalance);
        this.overdraftLimit = overdraftLimit;
    }

    public CurrentAccount(String ownerName, long openingBalance) {
        this(ownerName, openingBalance, 5000);
    }

    public CurrentAccount(String ownerName) {
        this(ownerName, 0, 5000);
    }

    public CurrentAccount(String accountNumber, String ownerName, long openingBalance, long overdraftLimit) {
        super(accountNumber, ownerName, openingBalance);
        this.overdraftLimit = overdraftLimit;
    }

    public long getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(long overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public double interestRate() {
        return 0.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= -overdraftLimit;
    }

    @Override
    public String toString() {
        return super.toString() + " | Current (Overdraft: " + overdraftLimit + ", Rate: " + interestRate() + "%)";
    }
}
