package br.com.recargapay.wallet_service.infrastructure.adapter.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NotFutureValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotFuture {
    String message() default "The timestamp cannot be in the future";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}