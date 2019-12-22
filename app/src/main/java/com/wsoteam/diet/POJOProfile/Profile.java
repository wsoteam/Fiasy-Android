package com.wsoteam.diet.POJOProfile;

import java.io.Serializable;

import kotlin.text.StringsKt;

public class Profile implements Serializable {
    private String firstName;
    private String lastName;

    private boolean isFemale;

    private String email;

    private int age;
    private int height;

    private double weight;
    private double loseWeight;

    private String exerciseStress;
    private String photoUrl;

    private int waterCount;
    private int maxKcal;
    private int maxProt;
    private int maxFat;
    private int maxCarbo;

    private float maxWater;

    private String difficultyLevel;

    private int numberOfDay;
    private int month;
    private int year;

    ///change 20.11.2019
    private int goLevel;
    private int goal;

    public Profile() {
    }

    public Profile(String firstName, String lastName, boolean isFemale, int age, int height, double weight, double loseWeight, String exerciseStress, String photoUrl, int waterCount, int maxKcal, int maxProt, int maxFat, int maxCarbo, String difficultyLevel, int numberOfDay, int month, int year) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isFemale = isFemale;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.loseWeight = loseWeight;
        this.exerciseStress = exerciseStress;
        this.photoUrl = photoUrl;
        this.waterCount = waterCount;
        this.maxKcal = maxKcal;
        this.maxProt = maxProt;
        this.maxFat = maxFat;
        this.maxCarbo = maxCarbo;
        this.difficultyLevel = difficultyLevel;
        this.numberOfDay = numberOfDay;
        this.month = month;
        this.year = year;
    }



    public float getMaxWater() {
        return maxWater;
    }

    public void setMaxWater(float maxWater) {
        this.maxWater = maxWater;
    }

    public int getNumberOfDay() {
        return numberOfDay;
    }

    public void setNumberOfDay(int numberOfDay) {
        this.numberOfDay = numberOfDay;
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

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getMaxKcal() {
        return maxKcal;
    }

    public void setMaxKcal(int maxKcal) {
        this.maxKcal = maxKcal;
    }

    public int getMaxProt() {
        return maxProt;
    }

    public void setMaxProt(int maxProt) {
        this.maxProt = maxProt;
    }

    public int getMaxFat() {
        return maxFat;
    }

    public void setMaxFat(int maxFat) {
        this.maxFat = maxFat;
    }

    public int getMaxCarbo() {
        return maxCarbo;
    }

    public void setMaxCarbo(int maxCarbo) {
        this.maxCarbo = maxCarbo;
    }

    public String getFirstName() {
        return (String) StringsKt.trim(firstName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return (String) StringsKt.trim(lastName);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isFemale() {
        return isFemale;
    }

    public void setFemale(boolean female) {
        isFemale = female;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getExerciseStress() {
        return exerciseStress;
    }

    public void setExerciseStress(String exerciseStress) {
        this.exerciseStress = exerciseStress;
    }

    public int getWaterCount() {
        return waterCount;
    }

    public void setWaterCount(int waterCount) {
        this.waterCount = waterCount;
    }

    public double getLoseWeight() {
        return loseWeight;
    }

    public void setLoseWeight(double loseWeight) {
        this.loseWeight = loseWeight;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getGoLevel() {
        return goLevel;
    }

    public void setGoLevel(int goLevel) {
        this.goLevel = goLevel;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isFemale=" + isFemale +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", loseWeight=" + loseWeight +
                ", exerciseStress='" + exerciseStress + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", waterCount=" + waterCount +
                ", maxKcal=" + maxKcal +
                ", maxProt=" + maxProt +
                ", maxFat=" + maxFat +
                ", maxCarbo=" + maxCarbo +
                ", difficultyLevel='" + difficultyLevel + '\'' +
                ", numberOfDay=" + numberOfDay +
                ", month=" + month +
                ", year=" + year +
                '}';
    }
}
