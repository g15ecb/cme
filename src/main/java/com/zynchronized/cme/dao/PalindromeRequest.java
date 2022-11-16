package com.zynchronized.cme.dao;

import com.zynchronized.cme.validators.PalindromeText;
import javax.validation.constraints.NotBlank;

public final class PalindromeRequest {

  @NotBlank
  private String username;
  @PalindromeText
  private String text;

  public PalindromeRequest() {
  }

  public PalindromeRequest(final String username, final String text) {
    this.username = username;
    this.text = text;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "PalindromeRequest{" +
        "username='" + username + '\'' +
        ", text='" + text + '\'' +
        '}';
  }
}
