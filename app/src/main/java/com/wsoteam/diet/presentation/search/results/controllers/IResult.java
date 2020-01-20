package com.wsoteam.diet.presentation.search.results.controllers;

import com.wsoteam.diet.presentation.search.basket.db.BasketEntity;
import java.util.List;

public interface IResult {
  void save(String date);
  void sendSearchString(String searchString);
  void setNewBasket(List<BasketEntity> savedFood);
  int[] getParams();
  void refreshBasket();
}
