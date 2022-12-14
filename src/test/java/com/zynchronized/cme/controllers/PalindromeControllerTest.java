package com.zynchronized.cme.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynchronized.cme.dto.PalindromeRequest;
import com.zynchronized.cme.repository.PalindromeRepository;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PalindromeControllerTest {

  @MockBean PalindromeRepository palindromeRepository;

  private static final String API = "/api/v1/palindrome";
  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper mapper;

  private static Stream<Arguments> invalidRequestParameters() {
    return Stream.of(
        Arguments.of("", "hello"),
        Arguments.of("gbarnett", "123"),
        Arguments.of("g12", "abd"),
        Arguments.of("g b", "abd"),
        Arguments.of("gbarnett", "ab c"));
  }

  private static Stream<Arguments> validRequestParameters() {
    return Stream.of(Arguments.of("gbarnett", "abcdEf!!fg"), Arguments.of("g", "AbdEfgH"));
  }

  @ParameterizedTest
  @MethodSource("invalidRequestParameters")
  public void testInvalidRequest(final String username, final String text) throws Exception {
    final var request = new PalindromeRequest(username, text);
    final var payload = mapper.writeValueAsString(request);
    mvc.perform(post(API).contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @MethodSource("validRequestParameters")
  public void testValidRequest(final String username, final String text) throws Exception {
    final var request = new PalindromeRequest(username, text);
    final var payload = mapper.writeValueAsString(request);
    mvc.perform(post(API).contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isOk());
  }

  @Test
  public void testPalindromeResponse() throws Exception {
    final var request = new PalindromeRequest("granville", "kayak");
    final var payload = mapper.writeValueAsString(request);
    mvc.perform(post(API).contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.palindrome").value(true));
  }

  @Test
  public void testNotPalindromeResponse() throws Exception {
    final var request = new PalindromeRequest("granville", "hello");
    final var payload = mapper.writeValueAsString(request);
    mvc.perform(post(API).contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.palindrome").value(false));
  }
}
