package com.bitcoinfetcher.models;

import java.sql.Timestamp;

public class Status {

  private Timestamp timestamp;
  private int error_code;
  private String error_message;
  private int elapsed;
  private int credit_count;
  private String notice;
  private int total_count;

  public Status() {
  }

  public Status(Timestamp timestamp, int error_code, String error_message, int elapsed,
      int credit_count, String notice, int total_count) {
    this.timestamp = timestamp;
    this.error_code = error_code;
    this.error_message = error_message;
    this.elapsed = elapsed;
    this.credit_count = credit_count;
    this.notice = notice;
    this.total_count = total_count;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public int getError_code() {
    return error_code;
  }

  public String getError_message() {
    return error_message;
  }

  public int getElapsed() {
    return elapsed;
  }

  public int getCredit_count() {
    return credit_count;
  }

  public String getNotice() {
    return notice;
  }

  public int getTotal_count() {
    return total_count;
  }
}
