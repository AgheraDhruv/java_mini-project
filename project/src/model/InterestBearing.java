package model;

/**
 * InterestBearing — capability interface for accounts that accrue interest.
 * Provides a default method yearlyInterest() calculated from interestRate() and balance.
 */
public interface InterestBearing {
    double interestRate();
    long getBalance();

    /**
     * Default method calculating annual interest in rupees based on interestRate% and balance.
     */
    default double yearlyInterest() {
        return (getBalance() * interestRate()) / 100.0;
    }

    /**
     * Supplementary default method calculating monthly interest.
     */
    default double monthlyInterest() {
        return yearlyInterest() / 12.0;
    }
}
