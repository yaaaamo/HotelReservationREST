package com.example.agence_service.cli;

import java.io.BufferedReader;

public class IntegerInputProcessor extends ComplexUserInputProcessor<Integer> {

  public IntegerInputProcessor(BufferedReader inputReader) {
    super(inputReader);
  }

  @Override
  protected void setMessage() {
    message = "Please enter an integer:";
  }

  @Override
  protected void setValidityCriterion() {
    isValid = str -> {
      try {
        Integer.parseInt(str);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    };
  }

  @Override
  protected void setParser() {
    try {
      parser = Integer.class.getMethod("parseInt", String.class);
    } catch (SecurityException | NoSuchMethodException e) {
      e.printStackTrace();
    }
  }
}
