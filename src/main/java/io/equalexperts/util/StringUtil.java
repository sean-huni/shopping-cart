package io.equalexperts.util;

import io.equalexperts.exception.InstantiationProhibitedException;

import javax.annotation.processing.Generated;
import java.util.Map;

@Generated("io.quickvote.util.StringUtil")
public final class StringUtil {

    private StringUtil() {
        throw new InstantiationProhibitedException("This class cannot be instantiated");
    }

    // Utility method for custom error formatting
    public static String formatErrors(Map<String, String> errors) {
        StringBuilder builder = new StringBuilder();
        errors.forEach((field, message) ->
                builder.append(String.format("'%s': %s%n", field, message))
        );
        return builder.toString();
    }

}
