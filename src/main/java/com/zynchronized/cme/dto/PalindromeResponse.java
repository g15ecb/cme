package com.zynchronized.cme.dto;

public class PalindromeResponse {

  private boolean palindrome;

  public PalindromeResponse() {}

  public PalindromeResponse(final boolean palindrome) {
    this.palindrome = palindrome;
  }

  public boolean isPalindrome() {
    return palindrome;
  }

  public void setPalindrome(boolean palindrome) {
    this.palindrome = palindrome;
  }

  @Override
  public String toString() {
    return "PalindromeResponse{" + "palindrome=" + palindrome + '}';
  }
}
