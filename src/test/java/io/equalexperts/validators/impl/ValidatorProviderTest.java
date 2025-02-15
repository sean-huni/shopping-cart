package io.equalexperts.validators.impl;

import io.equalexperts.exception.CartException;
import io.equalexperts.exception.CartValidationException;
import io.equalexperts.exception.PriceServiceException;
import io.equalexperts.exception.ServiceError;
import io.equalexperts.model.CartError;
import io.equalexperts.model.ProductIn;
import io.equalexperts.validators.ValidatorProvider;
import io.equalexperts.validators.wrapper.PriceWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static io.equalexperts.constant.ErrorConstants.VALIDATION_ERROR;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
@DisplayName("Given Validator-Provider")
class ValidatorProviderTest {
    private final ValidatorProvider validatorProvider = new ValidatorProviderImpl();

    @Nested
    @DisplayName("When validating ProductIn")
    class whenValidatingProductIn {

        @Nested
        @DisplayName("Positive Scenarios")
        class whenValidateDataIsCalledPositiveScenarios {
            @Test
            @DisplayName("Then successfully validated ProductIn")
            void shouldValidateData() {
                // Given
                final ProductIn productIn = new ProductIn("cheerios", 3);

                // When validating the data, then no exception should be thrown
                assertDoesNotThrow(() -> validatorProvider.validateData(productIn));
            }
        }

        @Nested
        @DisplayName("Negative Scenarios")
        class whenValidateDataIsCalledNegativeScenarios {
            @Test
            @DisplayName("Then should throw CartException: ProductName less than 3 characters")
            void shouldThrowErroneousEmptyProductName() {
                final Set<String> message = Set.of("Product-Name must be between 3 and 50 characters", "Product-Name is required");
                // Given
                final ProductIn productIn = new ProductIn("", 3);

                // When validated erroneous data, then a CartException should be thrown
                final Exception exception = assertThrows(CartException.class, () -> validatorProvider.validateData(productIn));
                assertEquals("VALIDATION_ERROR", exception.getMessage());
                assertEquals(1, ((CartValidationException) exception).getViolations().size());
                // @NotBlank or @Size validation triggers in random order.
                assertTrue(message.contains(((CartValidationException) exception).getViolations().get("name")));
                assertNull(((CartValidationException) exception).getViolations().get("quantity"));
            }

            @Test
            @DisplayName("Then should throw CartException: Product-Quantity is required")
            void shouldThrowErroneousNullQuantity() {
                // Given
                final ProductIn productIn = new ProductIn("demo", null);

                // When validated erroneous data, then a CartException should be thrown
                final Exception exception = assertThrows(CartException.class, () -> validatorProvider.validateData(productIn));
                assertEquals("VALIDATION_ERROR", exception.getMessage());
                assertEquals(1, ((CartValidationException) exception).getViolations().size());
                assertEquals("Product-Quantity is required", ((CartValidationException) exception).getViolations().get("quantity"));
                assertNull(((CartValidationException) exception).getViolations().get("name"));
            }

            @Test
            @DisplayName("Then ProductIn with null values should throw CartException")
            void shouldThrowErroneousNullProperties() {
                // Given
                final ProductIn productIn = new ProductIn(null, null);

                // When validated erroneous data, then a CartException should be thrown
                final CartException exception = assertThrows(CartException.class, () -> validatorProvider.validateData(productIn));
                final CartError cartError = validatorProvider.buildErrors(exception);

                assertEquals(VALIDATION_ERROR, cartError.errorType());
                assertEquals(VALIDATION_ERROR, cartError.message());

                // Name
                assertTrue(cartError.violations().containsKey("name"));
                final var nameField = cartError.violations().get("name");
                assertEquals("Product-Name is required", nameField);

                // Quantity
                assertTrue(cartError.violations().containsKey("quantity"));
                final var quantityField = cartError.violations().get("quantity");
                assertEquals("Product-Quantity is required", quantityField);
            }

            @Test
            @DisplayName("The throw ServiceError when CartException is null")
            void shouldThrowServiceErrorWhenCartExceptionIsNull() {
                assertThrows(ServiceError.class, () -> validatorProvider.buildErrors(null));
            }

            @Test
            @DisplayName("The throw ServiceError when CartException is null")
            void shouldThrowServiceErrorWhenCartExceptionNot() {
                assertDoesNotThrow(() -> validatorProvider.buildErrors(new CartException("Demo Exception")));
            }

            @Test
            @DisplayName("Then throw CartException with required fields")
            void shouldThrowCartExceptionWithRequiredFields() {
                // Given ProductIn when values are null
                final ProductIn productIn = new ProductIn(null, null);

                // Then
                final Exception exception = assertThrows(CartException.class, () -> validatorProvider.validateData(productIn));

                assertEquals("VALIDATION_ERROR", exception.getMessage());
                assertEquals(2, ((CartValidationException) exception).getViolations().size());
                assertEquals("Product-Name is required", ((CartValidationException) exception).getViolations().get("name"));
                assertEquals("Product-Quantity is required", ((CartValidationException) exception).getViolations().get("quantity"));
            }

            @Test
            @DisplayName("Then throw CartException with incorrect product data")
            void shouldThrowCartExceptionWithIncorrectProductData() {
                // Given ProductIn when values are null
                final ProductIn productIn = new ProductIn("y", -1);

                // Then
                final Exception exception = assertThrows(CartException.class, () -> validatorProvider.validateData(productIn));

                assertEquals("VALIDATION_ERROR", exception.getMessage());
                assertEquals(2, ((CartValidationException) exception).getViolations().size());
                assertEquals("Product-Name must be between 3 and 50 characters", ((CartValidationException) exception).getViolations().get("name"));
                assertEquals("Product-Quantity must be zero or more", ((CartValidationException) exception).getViolations().get("quantity"));
            }
        }
    }

    @Nested
    @DisplayName("When validating Price")
    class whenValidatingPriceWrapper {

        @Nested
        @DisplayName("Positive Scenarios")
        class whenValidatePriceWrapperPositiveScenarios {

            @Test
            @DisplayName("Then successfully validated PriceWrapper")
            void shouldThrowErroneousPositivePrice() {
                // Given
                final PriceWrapper pw = new PriceWrapper(BigDecimal.valueOf(33.09));

                // When validating the data, then no exception should be thrown
                assertDoesNotThrow(() -> validatorProvider.validateData(pw));
            }
        }

        @Nested
        @DisplayName("Negative Scenarios")
        class whenValidatePriceWrapperNegativeScenarios {

            @Test
            @DisplayName("Then should throw CartException:  Price must be greater than zero")
            void shouldThrowErroneousNegativePrice() {
                // Given
                final PriceWrapper pw = new PriceWrapper(BigDecimal.valueOf(-599.39));

                // When validated erroneous data, then a CartException should be thrown
                final Exception exception = assertThrows(CartException.class, () -> validatorProvider.validateData(pw));
                assertEquals("PRICE_SERVICE_ERROR", ((PriceServiceException) exception).getTitle());
                assertEquals("'price': Price must be greater than zero", exception.getMessage().trim());
            }
        }
    }
}