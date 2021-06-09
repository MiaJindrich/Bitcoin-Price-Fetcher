package com.bitcoinfetcher.DTOs;

import java.util.Date;

public class PriceEntryDTO {

  private Date date;
  private Float price;

  public PriceEntryDTO(Date date, Float price) {
    this.date = date;
    this.price = price;
  }

  public Date getDate() {
    return date;
  }

  public Float getPrice() {
    return price;
  }
}
