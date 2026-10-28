package model;

/**
 * WithdrawRule — functional interface for custom withdrawal policies.
 * Has a single abstract method allow(Account account, long amount).
 */
@FunctionalInterface
public interface WithdrawRule {
    boolean allow(Account account, long amount);
}
