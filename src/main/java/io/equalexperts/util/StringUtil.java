package io.equalexperts.util;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public final class StringUtil {
    // Utility method for custom error formatting
    public static String formatErrors(Map<String, String> errors) {
        StringBuilder builder = new StringBuilder();
        errors.forEach((field, message) ->
                builder.append(String.format("'%s': %s%n", field, message))
        );
        return builder.toString();
    }
}
