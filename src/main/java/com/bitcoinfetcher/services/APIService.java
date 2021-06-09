package com.bitcoinfetcher.services;

import com.bitcoinfetcher.DTOs.PriceEntryDTO;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
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

    LocalDateTime localDateTime = LocalDateTime.now();

    String url = baseURL + "/v1/bpi/historical/close.json";
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    builder.queryParam("start", localDateTime.minusDays(7).toLocalDate().toString());
    builder.queryParam("end", localDateTime.toLocalDate().toString());

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

    // print linear regression prediction
    SimpleRegression linearRegressionModel = getLinearRegressionModel(arrayEntries);
    System.out.println(makePrediction(linearRegressionModel, arrayEntries,7));

    //draw linear regression
    XYDataset dataset = createDataset(arrayEntries);
    DrawChart chart = new DrawChart(dataset);
    chart.pack();
    RefineryUtilities.centerFrameOnScreen(chart);
    chart.setVisible(true);
    chart.drawRegressionLine();

    return arrayEntries;
  }

  public XYDataset createDataset(List<PriceEntryDTO> array) {
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

  public SimpleRegression getLinearRegressionModel(List<PriceEntryDTO> array) {
    SimpleRegression sr = new SimpleRegression();

    for (int i = 0; i < array.size(); i++) {
      Date date = array.get(i).getDate();
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String strDate = dateFormat.format(date);
      double x = Double.parseDouble(strDate.substring(strDate.length()-2));
      double y = array.get(i).getPrice();
      sr.addData(x, y);
    }
    return sr;
  }

  public String makePrediction(SimpleRegression sr, List<PriceEntryDTO> array, int runs) {
    StringBuilder sb = new StringBuilder();
    // Display the intercept of the regression
    sb.append("Intercept: " + sr.getIntercept());
    sb.append("\n");
    // Display the slope of the regression.
    sb.append("Slope: " + sr.getSlope());
    sb.append("\n");
    // Display the slope standard error
    sb.append("Standard Error: " + sr.getSlopeStdErr());
    sb.append("\n");
    // Display adjusted R2 value
    sb.append("Adjusted R2 value: " + sr.getRSquare());
    sb.append("\n");
    sb.append("*************************************************");
    sb.append("\n");
    sb.append("Running predictions......");
    sb.append("\n");

    for (int i = 0 ; i < runs ; i++) {
      Date date = array.get(6).getDate();
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String strDate = dateFormat.format(date);
      int rn = Integer.parseInt(strDate.substring(8))+i+1;
      sb.append("Day: " + rn + "  Predicted bitcoin price: " + Math.round(sr.predict(rn)));
      sb.append("\n");
    }
    return sb.toString();
  }


}
