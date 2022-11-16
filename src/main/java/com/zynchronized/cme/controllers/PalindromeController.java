package com.zynchronized.cme.controllers;

import com.zynchronized.cme.dao.PalindromeRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/palindrome")
public class PalindromeController {

  private static final Logger log = LoggerFactory.getLogger(PalindromeController.class);

  @PostMapping
  public void post(@Valid @RequestBody PalindromeRequest request) {
    log.info("hello, {}", request);
  }
}
