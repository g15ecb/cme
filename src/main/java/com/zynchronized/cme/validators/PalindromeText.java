package com.zynchronized.cme.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PalindromeTextValidator.class)
@Documented
public @interface PalindromeText {

  String message() default "{PalindromeText.invalid}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
