package com.bitcoinfetcher.DTOs;

import java.time.LocalDate;

public class PriceEntryDTO {

  private String date;
  private Float price;

  public PriceEntryDTO(String date, Float price) {
    this.date = date;
    this.price = price;
  }

  public String getDate() {
    return date;
  }

  public Float getPrice() {
    return price;
  }
}
