package com.bitcoinfetcher.models;

import java.util.List;
import java.util.Map;

public class Currency {

  private int id;
  private String name;
  private String symbol;
  private String slug;
  private int num_market_pairs;
  private String date_added;
  private List<String> tags;
  private int max_supply;
  private int circulating_supply;
  private int total_supply;
  private Map<String, String> platform;
  private int cmc_rank;
  private String last_updated;
  private Map<String, Map<String, Double>> quote;

}
