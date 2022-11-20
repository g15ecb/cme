package com.zynchronized.cme;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

// the inputs here are only tested for inputs that the controller and its validation permits, hence
// the tame inputs.
class PalindromeTest {

  private static final Palindrome predicate = new Palindrome();

  @ParameterizedTest
  @ValueSource(strings = {"a", "A", "aa", "abcba", "AbA", "madam", "mAdAm", "kayak", "kaYak"})
  public void testIsPalindrome(final String s) {
    assertTrue(predicate.test(s));
  }

  @ParameterizedTest
  @ValueSource(strings = {"aA", "bcB", "ab", "madaM", "Kayak", "kAYak"})
  public void testIsNotPalindrome(final String s) {
    assertFalse(predicate.test(s));
  }
}
