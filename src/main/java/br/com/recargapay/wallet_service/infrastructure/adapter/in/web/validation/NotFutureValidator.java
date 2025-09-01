package br.com.recargapay.wallet_service.infrastructure.adapter.in.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class NotFutureValidator implements ConstraintValidator<NotFuture, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !value.isAfter(LocalDateTime.now());
    }
}
