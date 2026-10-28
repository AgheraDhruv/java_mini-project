package util;

import model.Account;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * StatePersister — serializes and deserializes Account arrays using Object streams.
 * Prevents account ID collision upon restoring accounts.
 */
public class StatePersister {

    /**
     * Serializes an array of Account objects to disk using ObjectOutputStream inside try-with-resources.
     */
    public static void save(Account[] accounts, Path file) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(accounts);
        }
    }

    /**
     * Deserializes an array of Account objects from disk using ObjectInputStream inside try-with-resources.
     */
    public static Account[] load(Path file) throws IOException, ClassNotFoundException {
        if (!Files.exists(file)) {
            return new Account[0];
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            Account[] accounts = (Account[]) ois.readObject();
            for (Account a : accounts) {
                if (a != null) {
                    Account.updateCounterFromAccount(a.getAccountNumber());
                }
            }
            return accounts;
        }
    }
}
