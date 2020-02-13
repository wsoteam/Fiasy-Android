package com.wsoteam.diet.presentation.search.basket.db;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.common.networking.food.ISearchResult;
import com.wsoteam.diet.common.networking.food.POJO.Result;
import com.wsoteam.diet.model.Eating;
import java.io.Serializable;

@Entity
public class BasketEntity implements ISearchResult, Serializable {
  @PrimaryKey(autoGenerate = true)
  private long id;
  private int serverId;
  private int deepId;
  private String name;
  private String brand;
  private boolean isLiquid;
  private int countPortions;

  private double kilojoules;
  private double calories;
  private double proteins;
  private double carbohydrates;
  private double sugar;
  private double fats;
  private double saturatedFats;
  private double monoUnSaturatedFats;
  private double polyUnSaturatedFats;
  private double cholesterol;
  private double cellulose;
  private double sodium;
  private double pottassium;
  private int eatingType;

  private String namePortion;
  private int sizePortion;

  public BasketEntity() {
  }

  public BasketEntity(int serverId, int deepId, String name, String brand,
      boolean isLiquid,
      int weight, double kilojoules, double calories, double proteins, double carbohydrates,
      double sugar, double fats, double saturatedFats, double monoUnSaturatedFats,
      double polyUnSaturatedFats, double cholesterol, double cellulose, double sodium,
      double pottassium, int eatingType, String namePortion, int sizePortion) {
    this.serverId = serverId;
    this.deepId = deepId;
    this.name = name;
    this.brand = brand;
    this.isLiquid = isLiquid;
    this.countPortions = weight;
    this.kilojoules = kilojoules;
    this.calories = calories;
    this.proteins = proteins;
    this.carbohydrates = carbohydrates;
    this.sugar = sugar;
    this.fats = fats;
    this.saturatedFats = saturatedFats;
    this.monoUnSaturatedFats = monoUnSaturatedFats;
    this.polyUnSaturatedFats = polyUnSaturatedFats;
    this.cholesterol = cholesterol;
    this.cellulose = cellulose;
    this.sodium = sodium;
    this.pottassium = pottassium;
    this.eatingType = eatingType;
    this.namePortion = namePortion;
    this.sizePortion = sizePortion;
  }

  public BasketEntity(Result result, int weight, int eatingType, int deepId) {
    name = result.getName();
    if (result.getBrand() != null) {
      brand = result.getBrand().getName();
    }
    this.countPortions = weight;
    isLiquid = result.isLiquid();
    kilojoules = result.getKilojoules();
    calories = result.getCalories();
    proteins = result.getProteins();
    carbohydrates = result.getCarbohydrates();
    sugar = result.getSugar();
    fats = result.getFats();
    saturatedFats = result.getSaturatedFats();
    monoUnSaturatedFats = result.getMonounsaturatedFats();
    polyUnSaturatedFats = result.getPolyunsaturatedFats();
    cholesterol = result.getCholesterol();
    cellulose = result.getCellulose();
    sodium = result.getSodium();
    pottassium = result.getPottasium();
    this.eatingType = eatingType;
    serverId = result.getId();
    this.deepId = -1;
    namePortion = Config.DEFAULT_PORTION_NAME;
  }


  //prepareToSave with 100 gramm
  public BasketEntity(Result result, int eatingType) {
    name = result.getName();
    if (result.getBrand() != null) {
      brand = result.getBrand().getName();
    }
    this.countPortions = Config.DEFAULT_PORTION;
    isLiquid = result.isLiquid();
    calories = Config.DEFAULT_PORTION * result.getCalories();
    proteins = Config.DEFAULT_PORTION * result.getProteins();
    carbohydrates = Config.DEFAULT_PORTION * result.getCarbohydrates();
    fats = Config.DEFAULT_PORTION * result.getFats();
    if (result.getSugar() != Config.EMPTY_COUNT) {
      sugar = result.getSugar() * Config.DEFAULT_PORTION;
    } else {
      sugar = Config.EMPTY_COUNT;
    }
    if (result.getSaturatedFats() != Config.EMPTY_COUNT) {
      saturatedFats = result.getSaturatedFats() * Config.DEFAULT_PORTION;
    } else {
      saturatedFats = Config.EMPTY_COUNT;
    }
    if (result.getMonounsaturatedFats() != Config.EMPTY_COUNT) {
      monoUnSaturatedFats = result.getMonounsaturatedFats() * Config.DEFAULT_PORTION;
    } else {
      monoUnSaturatedFats = Config.EMPTY_COUNT;
    }
    if (result.getPolyunsaturatedFats() != Config.EMPTY_COUNT) {
      polyUnSaturatedFats = result.getPolyunsaturatedFats() * Config.DEFAULT_PORTION;
    } else {
      polyUnSaturatedFats = Config.EMPTY_COUNT;
    }
    if (result.getCholesterol() != Config.EMPTY_COUNT) {
      cholesterol = result.getCholesterol() * Config.DEFAULT_PORTION;
    } else {
      cholesterol = Config.EMPTY_COUNT;
    }
    if (result.getCellulose() != Config.EMPTY_COUNT) {
      cellulose = result.getCellulose() * Config.DEFAULT_PORTION;
    } else {
      cellulose = Config.EMPTY_COUNT;
    }
    if (result.getSodium() != Config.EMPTY_COUNT) {
      sodium = result.getSodium() * Config.DEFAULT_PORTION;
    } else {
      sodium = Config.EMPTY_COUNT;
    }
    if (result.getPottasium() != Config.EMPTY_COUNT) {
      pottassium = result.getPottasium() * Config.DEFAULT_PORTION;
    } else {
      pottassium = Config.EMPTY_COUNT;
    }

    this.eatingType = eatingType;
    serverId = result.getId();
    this.deepId = -1;
    namePortion = Config.DEFAULT_PORTION_NAME;
    sizePortion = Config.DEFAULT_WEIGHT;
  }

  public BasketEntity(Result result, int eatingType, int weight) {
    name = result.getName();
    if (result.getBrand() != null) {
      brand = result.getBrand().getName();
    }
    this.countPortions = weight;
    isLiquid = result.isLiquid();
    calories = weight * result.getCalories();
    proteins = weight * result.getProteins();
    carbohydrates = weight * result.getCarbohydrates();
    fats = weight * result.getFats();
    if (result.getSugar() != Config.EMPTY_COUNT) {
      sugar = result.getSugar() * weight;
    } else {
      sugar = Config.EMPTY_COUNT;
    }
    if (result.getSaturatedFats() != Config.EMPTY_COUNT) {
      saturatedFats = result.getSaturatedFats() * weight;
    } else {
      saturatedFats = Config.EMPTY_COUNT;
    }
    if (result.getMonounsaturatedFats() != Config.EMPTY_COUNT) {
      monoUnSaturatedFats = result.getMonounsaturatedFats() * weight;
    } else {
      monoUnSaturatedFats = Config.EMPTY_COUNT;
    }
    if (result.getPolyunsaturatedFats() != Config.EMPTY_COUNT) {
      polyUnSaturatedFats = result.getPolyunsaturatedFats() * weight;
    } else {
      polyUnSaturatedFats = Config.EMPTY_COUNT;
    }
    if (result.getCholesterol() != Config.EMPTY_COUNT) {
      cholesterol = result.getCholesterol() * weight;
    } else {
      cholesterol = Config.EMPTY_COUNT;
    }
    if (result.getCellulose() != Config.EMPTY_COUNT) {
      cellulose = result.getCellulose() * weight;
    } else {
      cellulose = Config.EMPTY_COUNT;
    }
    if (result.getSodium() != Config.EMPTY_COUNT) {
      sodium = result.getSodium() * weight;
    } else {
      sodium = Config.EMPTY_COUNT;
    }
    if (result.getPottasium() != Config.EMPTY_COUNT) {
      pottassium = result.getPottasium() * weight;
    } else {
      pottassium = Config.EMPTY_COUNT;
    }

    this.eatingType = eatingType;
    serverId = result.getId();
    this.deepId = -1;
    namePortion = Config.DEFAULT_PORTION_NAME;
    sizePortion = Config.DEFAULT_WEIGHT;
  }

  public BasketEntity(Result result, int countPortions, int sizePortion, String namePortion,
      int eatingType, int deepId) {
    name = result.getName();
    if (result.getBrand() != null) {
      brand = result.getBrand().getName();
    }

    this.countPortions = sizePortion;
    isLiquid = result.isLiquid();
    calories = sizePortion * result.getCalories();
    proteins = sizePortion * result.getProteins();
    carbohydrates = sizePortion * result.getCarbohydrates();
    fats = sizePortion * result.getFats();
    if (result.getSugar() != Config.EMPTY_COUNT) {
      sugar = result.getSugar() * sizePortion;
    } else {
      sugar = Config.EMPTY_COUNT;
    }
    if (result.getSaturatedFats() != Config.EMPTY_COUNT) {
      saturatedFats = result.getSaturatedFats() * sizePortion;
    } else {
      saturatedFats = Config.EMPTY_COUNT;
    }
    if (result.getMonounsaturatedFats() != Config.EMPTY_COUNT) {
      monoUnSaturatedFats = result.getMonounsaturatedFats() * sizePortion;
    } else {
      monoUnSaturatedFats = Config.EMPTY_COUNT;
    }
    if (result.getPolyunsaturatedFats() != Config.EMPTY_COUNT) {
      polyUnSaturatedFats = result.getPolyunsaturatedFats() * sizePortion;
    } else {
      polyUnSaturatedFats = Config.EMPTY_COUNT;
    }
    if (result.getCholesterol() != Config.EMPTY_COUNT) {
      cholesterol = result.getCholesterol() * sizePortion;
    } else {
      cholesterol = Config.EMPTY_COUNT;
    }
    if (result.getCellulose() != Config.EMPTY_COUNT) {
      cellulose = result.getCellulose() * sizePortion;
    } else {
      cellulose = Config.EMPTY_COUNT;
    }
    if (result.getSodium() != Config.EMPTY_COUNT) {
      sodium = result.getSodium() * sizePortion;
    } else {
      sodium = Config.EMPTY_COUNT;
    }
    if (result.getPottasium() != Config.EMPTY_COUNT) {
      pottassium = result.getPottasium() * sizePortion;
    } else {
      pottassium = Config.EMPTY_COUNT;
    }

    this.eatingType = eatingType;
    serverId = result.getId();
    this.deepId = deepId;
    this.sizePortion = sizePortion;
    this.namePortion = namePortion;
    this.countPortions = countPortions;
  }

  public void append(BasketEntity appendObj) {
    countPortions += appendObj.getCountPortions();
    calories += appendObj.getCalories();
    proteins += appendObj.getProteins();
    carbohydrates += appendObj.getCarbohydrates();
    fats += appendObj.getFats();
    if (appendObj.getSugar() >= Config.STANDART_PORTION) {
      sugar += appendObj.getSugar();
    } else {
      sugar = Config.EMPTY_COUNT;
    }
    if (appendObj.getSaturatedFats() >= Config.STANDART_PORTION) {
      saturatedFats += appendObj.getSaturatedFats();
    } else {
      saturatedFats = Config.EMPTY_COUNT;
    }
    if (appendObj.getMonoUnSaturatedFats() >= Config.STANDART_PORTION) {
      monoUnSaturatedFats += appendObj.getMonoUnSaturatedFats();
    } else {
      monoUnSaturatedFats = Config.EMPTY_COUNT;
    }
    if (appendObj.getPolyUnSaturatedFats() >= Config.STANDART_PORTION) {
      polyUnSaturatedFats += appendObj.getPolyUnSaturatedFats();
    } else {
      polyUnSaturatedFats = Config.EMPTY_COUNT;
    }
    if (appendObj.getCholesterol() >= Config.STANDART_PORTION) {
      cholesterol += appendObj.getCholesterol();
    } else {
      cholesterol = Config.EMPTY_COUNT;
    }
    if (appendObj.getCellulose() >= Config.STANDART_PORTION) {
      cellulose += appendObj.getCellulose();
    } else {
      cellulose = Config.EMPTY_COUNT;
    }
    if (appendObj.getSodium() >= Config.STANDART_PORTION) {
      sodium += appendObj.getSodium();
    } else {
      sodium = Config.EMPTY_COUNT;
    }
    if (appendObj.getPottassium() >= Config.STANDART_PORTION) {
      pottassium = appendObj.getPottassium();
    } else {
      pottassium = Config.EMPTY_COUNT;
    }

  }


  public BasketEntity(Eating eating, int typeEating) {
    if (eating.isNewFood()){
      convertNewFood(eating, typeEating);
    }else {
      convertOldFood(eating, typeEating);
    }
  }

  private void convertOldFood(Eating eating, int typeEating) {
    this.serverId = Config.EMPTY_COUNT;
    this.deepId = Config.EMPTY_COUNT;
    this.name = eating.getName();
    this.brand = eating.getBrand();
    this.isLiquid = eating.isLiquid();
    this.countPortions = eating.getWeight();
    this.kilojoules = Config.EMPTY_COUNT;
    this.calories = eating.getCalories();
    this.proteins = eating.getProtein();
    this.carbohydrates = eating.getCarbohydrates();
    this.sugar = Config.EMPTY_COUNT;
    this.fats = eating.getFat();
    this.saturatedFats = Config.EMPTY_COUNT;
    this.monoUnSaturatedFats = Config.EMPTY_COUNT;
    this.polyUnSaturatedFats = Config.EMPTY_COUNT;
    this.cholesterol = Config.EMPTY_COUNT;
    this.cellulose = Config.EMPTY_COUNT;
    this.sodium = Config.EMPTY_COUNT;
    this.pottassium = Config.EMPTY_COUNT;
    this.eatingType = typeEating;
    this.namePortion = Config.DEFAULT_PORTION_NAME;
    this.sizePortion = Config.DEFAULT_WEIGHT;
  }

  private void convertNewFood(Eating eating, int typeEating) {
    this.serverId = eating.getServerId();
    this.deepId = eating.getDeepId();
    this.name = eating.getName();
    this.brand = eating.getBrand();
    this.isLiquid = eating.isLiquid();
    this.countPortions = eating.getWeight();
    this.kilojoules = eating.getKilojoules();
    this.calories = eating.getCalories();
    this.proteins = eating.getProtein();
    this.carbohydrates = eating.getCarbohydrates();
    this.sugar = eating.getSugar();
    this.fats = eating.getFat();
    this.saturatedFats = eating.getSaturatedFats();
    this.monoUnSaturatedFats = eating.getMonoUnSaturatedFats();
    this.polyUnSaturatedFats = eating.getPolyUnSaturatedFats();
    this.cholesterol = eating.getCholesterol();
    this.cellulose = eating.getCellulose();
    this.sodium = eating.getSodium();
    this.pottassium = eating.getPottassium();
    this.eatingType = typeEating;
    this.namePortion = eating.getNamePortion();
    this.sizePortion = eating.getSizePortion();
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

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public int getCountPortions() {
    return countPortions;
  }

  public void setCountPortions(int countPortions) {
    this.countPortions = countPortions;
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

  public int getEatingType() {
    return eatingType;
  }

  public void setEatingType(int eatingType) {
    this.eatingType = eatingType;
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

  @Override
  public String toString() {
    return "BasketEntity{" +
            "id=" + id +
            ", serverId=" + serverId +
            ", deepId=" + deepId +
            ", name='" + name + '\'' +
            ", brand='" + brand + '\'' +
            ", isLiquid=" + isLiquid +
            ", countPortions=" + countPortions +
            ", kilojoules=" + kilojoules +
            ", calories=" + calories +
            ", proteins=" + proteins +
            ", carbohydrates=" + carbohydrates +
            ", sugar=" + sugar +
            ", fats=" + fats +
            ", saturatedFats=" + saturatedFats +
            ", monoUnSaturatedFats=" + monoUnSaturatedFats +
            ", polyUnSaturatedFats=" + polyUnSaturatedFats +
            ", cholesterol=" + cholesterol +
            ", cellulose=" + cellulose +
            ", sodium=" + sodium +
            ", pottassium=" + pottassium +
            ", eatingType=" + eatingType +
            ", namePortion='" + namePortion + '\'' +
            ", sizePortion=" + sizePortion +
            '}';
  }

  public void makeAtomicDefault() {
    countPortions = countPortions / Config.DEFAULT_PORTION;
    calories = calories / Config.DEFAULT_PORTION;
    proteins = proteins / Config.DEFAULT_PORTION;
    carbohydrates = carbohydrates / Config.DEFAULT_PORTION;
    fats = fats / Config.DEFAULT_PORTION;

    sugar = sugar / Config.DEFAULT_PORTION;
    saturatedFats = saturatedFats / Config.DEFAULT_PORTION;
    monoUnSaturatedFats = monoUnSaturatedFats / Config.DEFAULT_PORTION;
    polyUnSaturatedFats = polyUnSaturatedFats / Config.DEFAULT_PORTION;
    cholesterol = cholesterol / Config.DEFAULT_PORTION;
    cellulose = cellulose / Config.DEFAULT_PORTION;
    sodium = sodium / Config.DEFAULT_PORTION;
    pottassium = pottassium / Config.DEFAULT_PORTION;
  }

  public void makeAtomic(int countPortions, int sizePortion) {
    this.countPortions = 1;
    calories = calories / countPortions / sizePortion;
    proteins = proteins / countPortions / sizePortion;
    carbohydrates = carbohydrates / countPortions / sizePortion;
    fats = fats / countPortions / sizePortion;

    sugar = sugar / countPortions / sizePortion;
    saturatedFats = saturatedFats / countPortions / sizePortion;
    monoUnSaturatedFats = monoUnSaturatedFats / countPortions / sizePortion;
    polyUnSaturatedFats = polyUnSaturatedFats / countPortions / sizePortion;
    cholesterol = cholesterol / countPortions / sizePortion;
    cellulose = cellulose / countPortions / sizePortion;
    sodium = sodium / countPortions / sizePortion;
    pottassium = pottassium / countPortions / sizePortion;
  }

    public void swapId(BasketEntity basketEntity) {
      id = basketEntity.getId();
      serverId = basketEntity.getServerId();
      deepId = basketEntity.getDeepId();
      eatingType = basketEntity.getEatingType();
    }

  @Override
  public boolean equals(@Nullable Object obj) {
    return this.getDeepId() == ((BasketEntity) obj).getDeepId() && this.getServerId() == ((BasketEntity) obj).getServerId();
  }

  @Override
  public int hashCode() {
    return this.getServerId() * 1000 + this.getDeepId();
  }
}
