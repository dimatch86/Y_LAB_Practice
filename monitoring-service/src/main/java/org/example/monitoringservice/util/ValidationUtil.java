package org.example.monitoringservice.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.experimental.UtilityClass;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for validation input data
 */
@UtilityClass
public class ValidationUtil {

    /**
     * Validates the provided data.
     *
     * @param <T> the type of data to be validated
     * @param data the data to be validated
     * @return a string containing error messages if validation fails, or an empty string if validation succeeds
     */
    public <T> String validate(T data) {
        String errorMessages = "";
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(data);
        if (!violations.isEmpty()) {
            errorMessages = violations.stream().map(violation ->
                            violation.getPropertyPath().toString() + " " + violation.getMessage())
                    .collect(Collectors.joining(", "));
        }
        validatorFactory.close();
        return errorMessages;
    }
}
