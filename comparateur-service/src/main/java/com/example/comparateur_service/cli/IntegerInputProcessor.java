package com.example.comparateur_service.cli;

import java.io.BufferedReader;
import java.io.IOException;

public class IntegerInputProcessor {

  private BufferedReader inputReader;
  private String message;

  public IntegerInputProcessor(BufferedReader inputReader) {
    this.inputReader = inputReader;
    this.message = "Please enter an integer:";
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int process() throws IOException {
    System.out.println(message);
    while (true) {
      try {
        String input = inputReader.readLine().trim();
        return Integer.parseInt(input);
      } catch (NumberFormatException e) {
        System.err.println("Invalid number. Please try again.");
        System.out.println(message);
      }
    }
  }
}
