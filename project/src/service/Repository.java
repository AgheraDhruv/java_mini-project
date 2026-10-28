package service;

import model.annotation.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository — generic in-memory data access component backed by a HashMap.
 * Identifies entity primary keys at runtime using the @Id annotation via reflection,
 * supporting fields declared in superclasses.
 *
 * @param <T> entity type
 */
public class Repository<T> {

    private final Map<String, T> storage = new HashMap<>();

    /**
     * Extracts the primary key from entity by locating the @Id field via reflection,
     * searching upwards across the class hierarchy.
     */
    private String extractId(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        Class<?> current = entity.getClass();
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        Object val = field.get(entity);
                        if (val != null) {
                            return val.toString();
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Cannot access @Id field: " + field.getName(), e);
                    }
                }
            }
            current = current.getSuperclass();
        }
        throw new IllegalStateException("Class " + entity.getClass().getSimpleName() + " has no field annotated with @Id");
    }

    /**
     * Saves an entity under its reflective @Id key.
     */
    public T save(T entity) {
        String id = extractId(entity);
        storage.put(id, entity);
        return entity;
    }

    /**
     * Retrieves an entity by its string ID.
     */
    public T findById(String id) {
        return storage.get(id);
    }

    /**
     * Returns a new ArrayList containing all saved entities.
     */
    public ArrayList<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Deletes an entity by ID.
     * @return true if an entity was removed, false otherwise
     */
    public boolean delete(String id) {
        return storage.remove(id) != null;
    }

    /**
     * Returns the total count of stored entities.
     */
    public int count() {
        return storage.size();
    }

    /**
     * Saves multiple entities using variable arguments with legal @SafeVarargs.
     */
    @SafeVarargs
    public final void saveAll(T... items) {
        if (items != null) {
            for (T item : items) {
                if (item != null) {
                    save(item);
                }
            }
        }
    }

    /**
     * Bounded generic method computing the arithmetic mean of any Number array.
     */
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        int count = 0;
        for (N val : values) {
            if (val != null) {
                sum += val.doubleValue();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }
}
