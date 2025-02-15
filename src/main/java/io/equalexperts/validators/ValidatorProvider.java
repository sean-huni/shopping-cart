package io.equalexperts.validators;

import io.equalexperts.exception.CartException;
import io.equalexperts.model.CartError;

public interface ValidatorProvider {
    <Y> void validateData(final Y input);

    CartError buildErrors(final CartException e);
}
