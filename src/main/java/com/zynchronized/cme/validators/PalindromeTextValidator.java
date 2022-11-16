package com.zynchronized.cme.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public final class PalindromeTextValidator implements ConstraintValidator<PalindromeText, String> {

  @Override
  public boolean isValid(final String s,
      final ConstraintValidatorContext constraintValidatorContext) {
    for (int i = 0; i < s.length(); i++) {
      final char c = s.charAt(i);
      if (' ' == c) {
        return false;
      }
      if (Character.isDigit(c)) {
        return false;
      }
    }
    return true;
  }
}
