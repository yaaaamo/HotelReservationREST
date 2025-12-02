package com.example.agence_service.cli;
import com.example.agence_service.model.Hotel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class HotelRestClientCLI extends AbstractMain implements CommandLineRunner {

  @Autowired
  private RestTemplate proxy;

  private IntegerInputProcessor inputProcessor;

  private static String URI_HOTELS;


  @Override
  public void run(String... args) throws Exception {
    BufferedReader inputReader;
    String userInput = "";
    try {
      inputReader = new BufferedReader(new InputStreamReader(System.in));
      setTestServiceUrl(inputReader);

      URI_HOTELS = SERVICE_URL + "/hotels";

      do {
        menu();
        userInput = inputReader.readLine();
        processUserInput(inputReader, userInput, proxy);
        Thread.sleep(3000);
      } while(!userInput.equals(QUIT));

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected boolean validServiceUrl() {
    return SERVICE_URL.equals("http://localhost:8080/hotelservice/api");
  }

  @Override
  protected void menu() {
    StringBuilder builder = new StringBuilder();
    builder.append(QUIT + ". Quit.");
    builder.append("\n1. Get number of hotels.");
    builder.append("\n2. Display all hotels.");

    System.out.println(builder);
  }

  private void processUserInput(BufferedReader reader, String userInput, RestTemplate proxy) {
    Map<String, String> params = new HashMap<>();
    inputProcessor = new IntegerInputProcessor(reader);

    try {
      switch(userInput) {

        case "1":
          String uri = URI_HOTELS + "/count";
          String countStr = proxy.getForObject(uri, String.class);
          ObjectMapper mapper = new ObjectMapper();
          long count = (int) mapper.readValue(countStr, Map.class).get("count");
          System.out.println(String.format("There are %d hotels", count));
          System.out.println();
          break;

        case "2":
          uri = URI_HOTELS;
          Hotel[] hotels = proxy.getForObject(uri, Hotel[].class);
          System.out.println("Hotels :");
          Arrays.asList(hotels).forEach(System.out::println);
          System.out.println();
          break;



        default:
          break;
      }

    } catch (HttpClientErrorException e) {
      System.err.println(e.getStatusCode() + ": " + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
