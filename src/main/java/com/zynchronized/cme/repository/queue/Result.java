package com.zynchronized.cme.repository.queue;

public final class Result {

  private final String input;
  private final boolean output;

  public Result(final String input, final boolean output) {
    this.input = input;
    this.output = output;
  }

  public String getInput() {
    return input;
  }

  public boolean getOutput() {
    return output;
  }

  @Override
  public String toString() {
    return "Result{" + "input='" + input + '\'' + ", output=" + output + '}';
  }
}
