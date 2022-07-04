package Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TestResult {
    private final List<String> errors = new ArrayList<>();

    public boolean ok() {
        return errors.size() == 0;
    }

    public void setError(String error) {
        this.errors.add(error);
    }

    public String getError() {
        return String.join(", ", this.errors);
    }

    public TestResult mergeIfAny(TestResult other) {
        if (!other.ok()) {
            this.errors.addAll(other.errors);
        }
        return this;
    }

    public <T> void checkEqual(T actual, T expected, String prefix) {
        if (actual == null || !actual.equals(expected)) {
            this.setError(Utils.buildErrorMessage(actual, expected, prefix));
        }
    }

    public <T> void checkExist(T expected, String prefix) {
        if (expected == null || !expected.equals(true)) {
            this.setError(Utils.buildErrorMessageExists(prefix));
        }
    }

    public <T> void checkNotExist(T expected, String prefix) {
        if (expected == null || !expected.equals(false)) {
            this.setError(Utils.buildErrorMessageNotExist(prefix));
        }
    }


    public <T> void checkObjectEqual(T actual, T expected) {
        Field[] baseFields = actual.getClass().getDeclaredFields();

        for (Field field : baseFields) {
            try {
                Field expectedField = expected.getClass().getDeclaredField(field.getName());

                field.setAccessible(true);
                expectedField.setAccessible(true);

                Object actualValue = field.get(actual);
                Object expectedValue = expectedField.get(expected);

                this.checkEqual(actualValue, expectedValue, field.getName());
            } catch (Exception e) {
                this.setError("No such expected field: " + field.getName());
            }
        }
    }
}
