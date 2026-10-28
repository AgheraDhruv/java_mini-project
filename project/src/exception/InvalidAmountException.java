package exception;

/**
 * InvalidAmountException — thrown when a non-positive or malformed amount is supplied.
 */
public class InvalidAmountException extends BankException {

    public InvalidAmountException(String message) {
        super(message);
    }
}
