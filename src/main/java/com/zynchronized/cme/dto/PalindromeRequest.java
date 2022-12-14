package com.zynchronized.cme.dto;

import com.zynchronized.cme.validators.ContainsNoSpacesOrNumbers;
import javax.validation.constraints.NotBlank;

public final class PalindromeRequest {

  @NotBlank @ContainsNoSpacesOrNumbers private String username;
  @NotBlank @ContainsNoSpacesOrNumbers private String text;

  public PalindromeRequest() {}

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
    return "PalindromeRequest{" + "username='" + username + '\'' + ", text='" + text + '\'' + '}';
  }
}
