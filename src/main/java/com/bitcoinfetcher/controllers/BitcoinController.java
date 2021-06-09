package com.bitcoinfetcher.controllers;

import com.bitcoinfetcher.DTOs.PriceEntryDTO;
import com.bitcoinfetcher.services.APIService;
import java.text.ParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BitcoinController {

  private APIService apiService;

  @Autowired
  public BitcoinController(APIService apiService) {
    this.apiService = apiService;
  }

  @GetMapping ("api/bitcoin/prices")
  public ResponseEntity<List<PriceEntryDTO>> getBitcoinPrices() throws ParseException {
    List<PriceEntryDTO> response = apiService.getBitcoinPrices();
    return new ResponseEntity(response, HttpStatus.OK);
  }
}
