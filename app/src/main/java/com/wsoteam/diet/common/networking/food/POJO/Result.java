
package com.wsoteam.diet.common.networking.food.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.moshi.Json;
import com.wsoteam.diet.common.networking.food.ISearchResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result implements ISearchResult, Serializable {

    private int id;
    private String name;
    private String fullInfo;
    private Object barcode;
    private double portion;
    @SerializedName("is_liquid")
    private boolean isLiquid;
    private double kilojoules;
    private double calories;
    private double proteins;
    private double carbohydrates;
    private double sugar;
    private double fats;
    @SerializedName("saturated_fats")
    private double saturatedFats;
    @SerializedName("monounsaturated_fats")
    private double monounsaturatedFats;
    @SerializedName("polyunsaturated_fats")
    private double polyunsaturatedFats;
    private double cholesterol;
    private double cellulose;
    private double sodium;
    private double pottasium;
    private Category category;
    private Brand brand;
    @SerializedName("measurement_units")
    @Expose
    private List<MeasurementUnit> measurementUnits = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullInfo() {
        return fullInfo;
    }

    public void setFullInfo(String fullInfo) {
        this.fullInfo = fullInfo;
    }

    public Object getBarcode() {
        return barcode;
    }

    public void setBarcode(Object barcode) {
        this.barcode = barcode;
    }

    public double getPortion() {
        return portion;
    }

    public void setPortion(double portion) {
        this.portion = portion;
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

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getSaturatedFats() {
        return saturatedFats;
    }

    public void setSaturatedFats(double saturatedFats) {
        this.saturatedFats = saturatedFats;
    }

    public double getMonounsaturatedFats() {
        return monounsaturatedFats;
    }

    public void setMonounsaturatedFats(double monounsaturatedFats) {
        this.monounsaturatedFats = monounsaturatedFats;
    }

    public double getPolyunsaturatedFats() {
        return polyunsaturatedFats;
    }

    public void setPolyunsaturatedFats(double polyunsaturatedFats) {
        this.polyunsaturatedFats = polyunsaturatedFats;
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

    public double getPottasium() {
        return pottasium;
    }

    public void setPottasium(double pottasium) {
        this.pottasium = pottasium;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public List<MeasurementUnit> getMeasurementUnits() {
        return measurementUnits;
    }

    public void setMeasurementUnits(List<MeasurementUnit> measurementUnits) {
        this.measurementUnits = measurementUnits;
    }

    public Result withMeasurementUnits(List<MeasurementUnit> measurementUnits) {
        this.measurementUnits = measurementUnits;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fullInfo='" + fullInfo + '\'' +
                ", barcode=" + barcode +
                ", portion=" + portion +
                ", isLiquid=" + isLiquid +
                ", kilojoules=" + kilojoules +
                ", calories=" + calories +
                ", proteins=" + proteins +
                ", carbohydrates=" + carbohydrates +
                ", sugar=" + sugar +
                ", fats=" + fats +
                ", saturatedFats=" + saturatedFats +
                ", monounsaturatedFats=" + monounsaturatedFats +
                ", polyunsaturatedFats=" + polyunsaturatedFats +
                ", cholesterol=" + cholesterol +
                ", cellulose=" + cellulose +
                ", sodium=" + sodium +
                ", pottasium=" + pottasium +
                ", category=" + category +
                ", brand=" + brand +
                ", measurementUnits=" + measurementUnits +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
