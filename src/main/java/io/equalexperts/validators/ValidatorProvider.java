package io.equalexperts.validators;

import io.equalexperts.exception.CartException;
import io.equalexperts.exception.CartValidationException;
import io.equalexperts.exception.PriceServiceException;
import io.equalexperts.exception.ServiceError;
import io.equalexperts.model.CartError;
import io.equalexperts.validators.wrapper.PriceWrapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.equalexperts.constant.ErrorConstants.PRICE_SERVICE_ERROR;
import static io.equalexperts.constant.ErrorConstants.VALIDATION_ERROR;
import static io.equalexperts.util.StringUtil.formatErrors;
import static java.util.Objects.isNull;

@Log4j2
@UtilityClass
public class ValidatorProvider {
    private static final ValidatorFactory factory;
    private static final Validator validator;

    /**
     * Thread-Safe Initialization of ValidatorFactory and Validator *
     */
    static {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static <Y> void validateData(final Y input) throws ConstraintViolationException {
        final var violations = validate(input);

        if (!violations.isEmpty()) {
            log.warn("Validation Errors: {}", getFormattedErrors(violations));

            if (input instanceof PriceWrapper) {
                throw new PriceServiceException(PRICE_SERVICE_ERROR, formatErrors(getFormattedErrors(violations)));
            }

            throw new CartValidationException(VALIDATION_ERROR, getFormattedErrors(violations));
        }
    }

    private static <T> Set<ConstraintViolation<T>> validate(final T input) {
        final Set<ConstraintViolation<T>> violations = validator.validate(input);

        if (!violations.isEmpty()) {
            return violations;
        }
        return Collections.emptySet();
    }

    private static <T> Map<String, String> getFormattedErrors(final Set<ConstraintViolation<T>> violations) {
        final Map<String, String> errorMap = new HashMap<>();

        for (final ConstraintViolation<T> violation : violations) {
            errorMap.put(violation.getPropertyPath().toString(), violation.getMessage());
            log.warn("Validation Error: {}", violation.getMessage());
        }
        return errorMap;
    }

    public static CartError buildErrors(final CartException e) {
        final var errorMap = new HashMap<String, String>();
        if (isNull(e)) {
            throw new ServiceError("CartException is null");
        }

        if (e instanceof CartValidationException cartValidationException) {
            final var violations = cartValidationException.getViolations();
            errorMap.putAll(violations);    //Put each violation in the errorMap
        }

        return new CartError(CartException.getStatusCode(), VALIDATION_ERROR, errorMap, e.getMessage());
    }
}

