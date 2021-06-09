package com.bitcoinfetcher.services;

import com.bitcoinfetcher.DTOs.PriceEntryDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import org.apache.http.HttpEntity;
import org.springframework.http.HttpEntity;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

//  public Object getBitcoinPrices() {
//
//    List<NameValuePair> params = new ArrayList<>();
//    params.add(new BasicNameValuePair("symbol", "BTC"));
//    params.add(new BasicNameValuePair("convert", "USD"));
//
//    try {
//      String rawJson = makeAPICall(apiURL + "/v1/cryptocurrency/quotes/latest", params);
//      JSONObject jsonObject = new JSONObject(rawJson);
//      Object price = jsonObject.getJSONObject("data").getJSONObject("BTC").getJSONObject("quote").getJSONObject("USD").getInt("price");
//      System.out.println(price);
//
//      return price;
//
//    } catch (IOException e) {
//      System.out.println("Error: cannot access content - " + e);
//    } catch (URISyntaxException e) {
//      System.out.println("Error: Invalid URL " + e);
//    }
//    return null;
//  }
//
//  public String makeAPICall (String url, List<NameValuePair> params)
//      throws URISyntaxException, IOException {
//    String responseContent = "";
//
//    URIBuilder query = new URIBuilder(url);
//    query.addParameters(params);
//
//    CloseableHttpClient client = HttpClients.createDefault();
//    HttpGet request = new HttpGet(query.build());
//
//    request.setHeader(HttpHeaders.ACCEPT, "application/json");
//    request.addHeader("X-CMC_PRO_API_KEY", apiKey);
//
//    CloseableHttpResponse response = client.execute(request);
//
//    try {
//      System.out.println(response.getStatusLine());
//      HttpEntity entity = response.getEntity();
//      responseContent = EntityUtils.toString(entity);
//      EntityUtils.consume(entity);
//    } finally {
//      response.close();
//    }
//    return responseContent;
//  }


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

  public void makeLinearRegression (ArrayList<PriceEntryDTO> array) {
    double[] xArray = new double[array.size()];
    double[] yArray = new double[array.size()];

    for (int i = 0; i < array.size(); i++) {
      //xArray[i] = array.get(i).getDate();
      yArray[i] = array.get(i).getPrice();
    }

  }
}
