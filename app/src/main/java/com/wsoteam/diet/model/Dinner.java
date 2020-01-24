package com.wsoteam.diet.model;

import com.wsoteam.diet.presentation.search.basket.db.BasketEntity;
import java.io.Serializable;

public class Dinner extends Eating implements Serializable {
    public Dinner() {
    }

    public Dinner(BasketEntity basketEntity, int day,
        int month, int year, int type) {
        super(basketEntity, day, month, year, type);
    }

    public Dinner(Eating eating) {
        super(eating);
    }

    public Dinner(String name, String urlOfImages, int calories, int carbohydrates, int protein, int fat, int weight, int day, int month, int year) {
        super(name, urlOfImages, calories, carbohydrates, protein, fat, weight, day, month, year);
    }
}
