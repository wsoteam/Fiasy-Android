package com.wsoteam.diet.common.networking.food.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MeasurementUnit implements Serializable {
  @SerializedName("id")
  @Expose
  private int id;
  @SerializedName("name")
  @Expose
  private String name;
  @SerializedName("amount")
  @Expose
  private int amount;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public MeasurementUnit withId(int id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MeasurementUnit withName(String name) {
    this.name = name;
    return this;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public MeasurementUnit withAmount(int amount) {
    this.amount = amount;
    return this;
  }
}
