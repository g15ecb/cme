package com.zynchronized.cme;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

// Note that everything outside of the rules specified in the design brief are considered valid and
// therefore are tested under a palindromic semantics -- one would argue this is incorrect but as
// it's not in the requirements I will defer that to a future discussion. I'm keeping validation at
// the boundary of the public API (the REST controller), hence no validation here.
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
