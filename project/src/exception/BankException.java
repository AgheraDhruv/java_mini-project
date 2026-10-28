package exception;

/**
 * BankException — checked base class for all domain-specific banking exceptions.
 */
public class BankException extends Exception {

    public BankException(String message) {
        super(message);
    }

    public BankException(String message, Throwable cause) {
        super(message, cause);
    }
}
