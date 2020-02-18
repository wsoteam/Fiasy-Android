package com.wsoteam.diet.model;

import com.wsoteam.diet.presentation.search.basket.db.BasketEntity;
import java.io.Serializable;

public class Eating implements Serializable {
  private String name;
  private String urlOfImages;

  private double calories;
  private double carbohydrates;
  private double protein;
  private double fat;

  private int weight;

  private int day;
  private int month;
  private int year;

  //21.10.2019 changes
  private int serverId;
  private int deepId;

  private boolean isNewFood;

  private String brand;
  private boolean isLiquid;

  private double kilojoules;
  private double sugar;
  private double saturatedFats;
  private double monoUnSaturatedFats;
  private double polyUnSaturatedFats;
  private double cholesterol;
  private double cellulose;
  private double sodium;
  private double pottassium;

  private String namePortion;
  private int sizePortion;

  //0 - normal food
  private int type;

  public Eating() {
  }

  public Eating(String name, String urlOfImages, int calories, int carbohydrates, int protein,
      int fat, int weight, int day, int month, int year, int serverId, int deepId,
      boolean isNewFood,
      String brand, boolean isLiquid, double kilojoules, double sugar, double saturatedFats,
      double monoUnSaturatedFats, double polyUnSaturatedFats, double cholesterol, double cellulose,
      double sodium, double pottassium, String namePortion, int sizePortion, int type) {
    this.name = name;
    this.urlOfImages = urlOfImages;
    this.calories = calories;
    this.carbohydrates = carbohydrates;
    this.protein = protein;
    this.fat = fat;
    this.weight = weight;
    this.day = day;
    this.month = month;
    this.year = year;
    this.serverId = serverId;
    this.deepId = deepId;
    this.isNewFood = isNewFood;
    this.brand = brand;
    this.isLiquid = isLiquid;
    this.kilojoules = kilojoules;
    this.sugar = sugar;
    this.saturatedFats = saturatedFats;
    this.monoUnSaturatedFats = monoUnSaturatedFats;
    this.polyUnSaturatedFats = polyUnSaturatedFats;
    this.cholesterol = cholesterol;
    this.cellulose = cellulose;
    this.sodium = sodium;
    this.pottassium = pottassium;
    this.namePortion = namePortion;
    this.sizePortion = sizePortion;
    this.type = type;
  }

  public Eating(BasketEntity basketEntity, int day, int month, int year, int type) {
    this.name = basketEntity.getName();
    this.calories = (int) Math.round(basketEntity.getCalories());
    this.carbohydrates = (int) Math.round(basketEntity.getCarbohydrates());
    this.protein = (int) Math.round(basketEntity.getProteins());
    this.fat = (int) Math.round(basketEntity.getFats());
    this.weight = basketEntity.getCountPortions();
    this.day = day;
    this.month = month;
    this.year = year;
    this.serverId = basketEntity.getServerId();
    this.deepId = basketEntity.getDeepId();
    this.brand = basketEntity.getBrand();
    this.isLiquid = basketEntity.isLiquid();
    this.kilojoules = basketEntity.getKilojoules();
    this.sugar = basketEntity.getSugar();
    this.saturatedFats = basketEntity.getSaturatedFats();
    this.monoUnSaturatedFats = basketEntity.getMonoUnSaturatedFats();
    this.polyUnSaturatedFats = basketEntity.getPolyUnSaturatedFats();
    this.cholesterol = basketEntity.getCholesterol();
    this.cellulose = basketEntity.getCellulose();
    this.sodium = basketEntity.getSodium();
    this.pottassium = basketEntity.getPottassium();
    this.type = type;
    this.isNewFood = true;
    this.namePortion = basketEntity.getNamePortion();
    this.sizePortion = basketEntity.getSizePortion();
  }

  public Eating(String name, String urlOfImages, int calories, int carbohydrates, int protein,
      int fat, int weight, int day, int month, int year) {
    this.name = name;
    this.urlOfImages = urlOfImages;
    this.calories = calories;
    this.carbohydrates = carbohydrates;
    this.protein = protein;
    this.fat = fat;
    this.weight = weight;
    this.day = day;
    this.month = month;
    this.year = year;
  }

  public boolean isNewFood() {
    return isNewFood;
  }

  public void setNewFood(boolean newFood) {
    isNewFood = newFood;
  }

  public String getNamePortion() {
    return namePortion;
  }

  public void setNamePortion(String namePortion) {
    this.namePortion = namePortion;
  }

  public int getSizePortion() {
    return sizePortion;
  }

  public void setSizePortion(int sizePortion) {
    this.sizePortion = sizePortion;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrlOfImages() {
    return urlOfImages;
  }

  public void setUrlOfImages(String urlOfImages) {
    this.urlOfImages = urlOfImages;
  }

  public double getCalories() {
    return calories;
  }

  public void setCalories(double calories) {
    this.calories = calories;
  }

  public double getCarbohydrates() {
    return carbohydrates;
  }

  public void setCarbohydrates(double carbohydrates) {
    this.carbohydrates = carbohydrates;
  }

  public double getProtein() {
    return protein;
  }

  public void setProtein(double protein) {
    this.protein = protein;
  }

  public double getFat() {
    return fat;
  }

  public void setFat(double fat) {
    this.fat = fat;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public int getDay() {
    return day;
  }

  public void setDay(int day) {
    this.day = day;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public int getServerId() {
    return serverId;
  }

  public void setServerId(int serverId) {
    this.serverId = serverId;
  }

  public int getDeepId() {
    return deepId;
  }

  public void setDeepId(int deepId) {
    this.deepId = deepId;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public boolean isLiquid() {
    return isLiquid;
  }

  public void setLiquid(boolean liquid) {
    isLiquid = liquid;
  }

  public double getKilojoules() {
    return kilojoules;
  }

  public void setKilojoules(double kilojoules) {
    this.kilojoules = kilojoules;
  }

  public double getSugar() {
    return sugar;
  }

  public void setSugar(double sugar) {
    this.sugar = sugar;
  }

  public double getSaturatedFats() {
    return saturatedFats;
  }

  public void setSaturatedFats(double saturatedFats) {
    this.saturatedFats = saturatedFats;
  }

  public double getMonoUnSaturatedFats() {
    return monoUnSaturatedFats;
  }

  public void setMonoUnSaturatedFats(double monoUnSaturatedFats) {
    this.monoUnSaturatedFats = monoUnSaturatedFats;
  }

  public double getPolyUnSaturatedFats() {
    return polyUnSaturatedFats;
  }

  public void setPolyUnSaturatedFats(double polyUnSaturatedFats) {
    this.polyUnSaturatedFats = polyUnSaturatedFats;
  }

  public double getCholesterol() {
    return cholesterol;
  }

  public void setCholesterol(double cholesterol) {
    this.cholesterol = cholesterol;
  }

  public double getCellulose() {
    return cellulose;
  }

  public void setCellulose(double cellulose) {
    this.cellulose = cellulose;
  }

  public double getSodium() {
    return sodium;
  }

  public void setSodium(double sodium) {
    this.sodium = sodium;
  }

  public double getPottassium() {
    return pottassium;
  }

  public void setPottassium(double pottassium) {
    this.pottassium = pottassium;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}