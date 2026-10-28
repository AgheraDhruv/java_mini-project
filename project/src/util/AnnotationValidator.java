package util;

import model.annotation.MaxLength;
import model.annotation.Positive;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * AnnotationValidator — inspects object fields at runtime via Java reflection
 * to enforce @Positive and @MaxLength constraints across class hierarchies.
 */
public class AnnotationValidator {

    /**
     * Inspects obj and its superclasses for custom annotations and returns
     * all validation failure messages.
     *
     * @param obj the object to validate
     * @return array of error messages, empty if valid
     */
    public static String[] validate(Object obj) {
        if (obj == null) {
            return new String[0];
        }

        List<String> errors = new ArrayList<>();
        Class<?> current = obj.getClass();

        // Traverse hierarchy so superclass fields (like in Account) are inspected
        while (current != null && current != Object.class) {
            Field[] fields = current.getDeclaredFields();
            for (Field field : fields) {
                // Ensure private fields are accessible via reflection
                field.setAccessible(true);
                try {
                    Object val = field.get(obj);

                    // 1. Check @Positive on numeric fields
                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive ann = field.getAnnotation(Positive.class);
                        if (val instanceof Number num) {
                            if (num.doubleValue() <= 0) {
                                errors.add(ann.message());
                            }
                        }
                    }

                    // 2. Check @MaxLength on String fields
                    if (field.isAnnotationPresent(MaxLength.class)) {
                        MaxLength ann = field.getAnnotation(MaxLength.class);
                        if (val instanceof String str) {
                            if (str != null && str.length() > ann.value()) {
                                errors.add(field.getName() + ": " + ann.message() + " (" + ann.value() + ")");
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    errors.add("Unable to access field " + field.getName());
                }
            }
            current = current.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}
