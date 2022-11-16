package com.zynchronized.cme.services;

import com.zynchronized.cme.Palindrome;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class CachingPalindromeService implements PalindromeService {

  private static final Palindrome palindrome = new Palindrome();

  @Override
  @Cacheable("palindrome")
  public boolean isPalindrome(final String s) {
    return palindrome.test(s);
  }
}
