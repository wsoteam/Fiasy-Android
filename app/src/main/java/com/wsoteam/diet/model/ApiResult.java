package com.wsoteam.diet.model;

import java.net.URL;
import java.util.List;

public class ApiResult<T> {

  private int count;
  private URL next;
  private URL previous;
  private List<T> results;

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public URL getNext() {
    return next;
  }

  public void setNext(URL next) {
    this.next = next;
  }

  public URL getPrevious() {
    return previous;
  }

  public void setPrevious(URL previous) {
    this.previous = previous;
  }

  public List<T> getResults() {
    return results;
  }

  public void setResults(List<T> results) {
    this.results = results;
  }
}
