package com.bitcoinfetcher.services;

import com.bitcoinfetcher.DTOs.PriceEntryDTO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import org.springframework.http.HttpEntity;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class APIService {

  private RestTemplate restTemplate;

  private final String baseURL = System.getenv("API_URL");

  public APIService() {
    this.restTemplate = new RestTemplate();
  }


  public List<PriceEntryDTO> getBitcoinPrices() throws ParseException {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.ACCEPT, "application/json");

    LocalDate localDate = LocalDate.now();

    String url = baseURL + "/v1/bpi/historical/close.json";
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    builder.queryParam("start", localDate.minusDays(7).toString());
    builder.queryParam("end", localDate.toString());

    HttpEntity request = new HttpEntity(headers);

    ResponseEntity<String> rawResponse = restTemplate
        .exchange(builder.toUriString(), HttpMethod.GET, request, String.class);
    JSONObject jsonResponse = new JSONObject(rawResponse.getBody());
    JSONObject jsonPrices = jsonResponse.getJSONObject("bpi");

    List<PriceEntryDTO> arrayEntries = new ArrayList<>();
    Iterator<String> keys = jsonPrices.keys();

    while (keys.hasNext()) {
      String key = keys.next();
      Float price = jsonPrices.getFloat(key);
      SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
      Date date = form.parse(key);
      PriceEntryDTO entry = new PriceEntryDTO(date, price);
      arrayEntries.add(entry);
    }

    TimeSeriesCollection dataset = createDataset(arrayEntries);
    DrawChart chart = new DrawChart(dataset);
    chart.pack();
    RefineryUtilities.centerFrameOnScreen(chart);
    chart.setVisible(true);
    chart.drawRegressionLine();

    return arrayEntries;
  }

  public TimeSeriesCollection createDataset(List<PriceEntryDTO> array) {
    TimeSeriesCollection dataset = new TimeSeriesCollection();
    TimeSeries series = new TimeSeries("Bitcoin Price (in USD)");

    for (int i = 0; i < array.size(); i++) {
      Date date = array.get(i).getDate();
      double price = array.get(i).getPrice();
      series.add(new Day(date), price);
    }
    dataset.addSeries(series);

    return dataset;
  }
}
