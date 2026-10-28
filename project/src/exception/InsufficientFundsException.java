package exception;

/**
 * InsufficientFundsException — thrown when an account balance cannot cover a withdrawal/transfer.
 * Holds the shortfall amount in rupees.
 */
public class InsufficientFundsException extends BankException {

    private final long shortfall;

    public InsufficientFundsException(String message, long shortfall) {
        super(message);
        this.shortfall = shortfall;
    }

    public long getShortfall() {
        return shortfall;
    }
}
