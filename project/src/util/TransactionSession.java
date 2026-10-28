package util;

/**
 * TransactionSession — a small AutoCloseable resource to demonstrate try-with-resources.
 * Automatically logs closing of a transaction scope.
 */
public class TransactionSession implements AutoCloseable {

    private final String sessionId;

    public TransactionSession(String sessionId) {
        this.sessionId = sessionId;
        System.out.println("[Session " + sessionId + "] Opened transaction session.");
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void close() {
        System.out.println("[Session " + sessionId + "] Closed transaction session.");
    }
}
