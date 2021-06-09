package com.bitcoinfetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BitcoinFetcherApplication {

  public static void main(String[] args) {
    System.setProperty("java.awt.headless", "false");
    SpringApplication.run(BitcoinFetcherApplication.class, args);
  }

}
