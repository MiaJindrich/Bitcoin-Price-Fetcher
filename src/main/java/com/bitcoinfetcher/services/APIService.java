package com.bitcoinfetcher.services;

import com.bitcoinfetcher.DTOs.PriceEntryDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.apache.commons.math3.stat.regression.SimpleRegression;


@Service
public class APIService {

  private RestTemplate restTemplate;

  private final String baseURL = System.getenv("API_URL");

  public APIService() {
    this.restTemplate = new RestTemplate();
  }


  public List<PriceEntryDTO> getBitcoinPrices() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.ACCEPT, "application/json");

    LocalDateTime localDateTime = LocalDateTime.now();

    String url = baseURL + "/v1/bpi/historical/close.json";
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    builder.queryParam("start", localDateTime.minusDays(7).toLocalDate().toString());
    builder.queryParam("end", localDateTime.toLocalDate().toString());

    HttpEntity request = new HttpEntity(headers);

    ResponseEntity<String> rawResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, String.class);
    JSONObject jsonResponse = new JSONObject(rawResponse.getBody());
    JSONObject jsonPrices = jsonResponse.getJSONObject("bpi");

    List<PriceEntryDTO> arrayEntries = new ArrayList<>();
    Iterator<String> keys = jsonPrices.keys();

    while(keys.hasNext()) {
      String key = keys.next();
      Float price = jsonPrices.getFloat(key);
      PriceEntryDTO entry = new PriceEntryDTO(key, price);
      arrayEntries.add(entry);
    }
    return arrayEntries;
  }

  public SimpleRegression getLinearRegressionModel (ArrayList<PriceEntryDTO> array) {
    SimpleRegression sr = new SimpleRegression();

    for (int i = 0; i < array.size(); i++) {
      double x = Double.parseDouble(array.get(i).getDate());
      double y = array.get(i).getPrice();
      sr.addData(x, y);
    }
    return sr;
  }
}
