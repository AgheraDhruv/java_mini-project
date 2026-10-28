package model;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;

/**
 * Transactable — defines core transaction capabilities for accounts.
 * Practical 8: updated with domain checked exceptions.
 */
public interface Transactable {
    void deposit(long amount) throws InvalidAmountException;
    boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException;
}
