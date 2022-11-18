package com.zynchronized.cme.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PalindromeTextValidatorTest {

  private static final PalindromeTextValidator validator = new PalindromeTextValidator();

  @ParameterizedTest
  @ValueSource(strings = {"ab c ", " ", "    ", "abcdef12ghi", "1", "   s 213e  "})
  public void testFails(final String s) {
    assertFalse(validator.isValid(s, null));
  }

  @Test
  public void testNullFails() {
    assertFalse(validator.isValid(null, null));
  }

  // there's no rule specified for punctuation, so currently they fall under the palindromic
  // semantics
  @ParameterizedTest
  @ValueSource(strings = {"abcdefg", "!AbdefFhi!j?abc", "mum"})
  public void testPasses(final String s) {
    assertTrue(validator.isValid(s, null));
  }
}
