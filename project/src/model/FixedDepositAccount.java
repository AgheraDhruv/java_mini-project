package model;

/**
 * FixedDepositAccount — account type with locked funds and 7.0% interest.
 * Extends Account. Withdrawals are locked.
 */
public class FixedDepositAccount extends Account {

    private static final long serialVersionUID = 2004L;

    public FixedDepositAccount(String ownerName, long openingBalance) {
        super(ownerName, openingBalance);
    }

    public FixedDepositAccount(String ownerName) {
        this(ownerName, 0);
    }

    public FixedDepositAccount(String accountNumber, String ownerName, long openingBalance) {
        super(accountNumber, ownerName, openingBalance);
    }

    @Override
    public double interestRate() {
        return 7.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return false; // locked deposit
    }

    @Override
    public String toString() {
        return super.toString() + " | FixedDeposit (Locked, Rate: " + interestRate() + "%)";
    }
}
