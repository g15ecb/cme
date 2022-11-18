package com.zynchronized.cme;

import java.util.function.Predicate;

/** Tests if a {@link String} is a palindrome. */
public class Palindrome implements Predicate<String> {

  // This admits anything that validation permits
  @Override
  public boolean test(final String s) {
    // Dev Notes.
    //   Pure function. Same output for the same input.
    //   Runtime: O(n), n=s.length()
    //   Memory:  constant -- i, j and the temps involved in the updates of i, j and reads (char)
    //            of O(1) accesses to [s]
    for (int i = 0, j = s.length() - 1; i < j; i++, j--) {
      if (s.charAt(i) != s.charAt(j)) { // O(1), O(1)
        return false;
      }
    }
    return true;
  }
}
