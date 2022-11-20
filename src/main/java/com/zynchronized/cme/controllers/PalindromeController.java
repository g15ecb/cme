package com.zynchronized.cme.controllers;

import com.zynchronized.cme.dto.PalindromeRequest;
import com.zynchronized.cme.dto.PalindromeResponse;
import com.zynchronized.cme.services.PalindromeService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/palindrome")
public class PalindromeController {

  private final PalindromeService palindromeService;

  public PalindromeController(final PalindromeService palindromeService) {
    this.palindromeService = palindromeService;
  }

  @PostMapping
  public ResponseEntity<PalindromeResponse> post(@Valid @RequestBody PalindromeRequest request) {
    final boolean isPalindrome = palindromeService.isPalindrome(request.getText());
    return ResponseEntity.ok(new PalindromeResponse(isPalindrome));
  }
}
