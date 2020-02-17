package com.wsoteam.diet.presentation.search.basket.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wsoteam.diet.App;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.common.diary.FoodWork;
import com.wsoteam.diet.common.networking.food.HeaderObj;
import com.wsoteam.diet.common.networking.food.ISearchResult;
import com.wsoteam.diet.presentation.search.basket.BasketActivity;
import com.wsoteam.diet.presentation.search.product.DetailActivity;
import com.wsoteam.diet.presentation.search.basket.db.BasketDAO;
import com.wsoteam.diet.presentation.search.basket.db.BasketEntity;
import com.wsoteam.diet.presentation.search.results.controllers.BasketUpdater;
import com.wsoteam.diet.presentation.search.results.controllers.ClickListener;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
    IBasket {
  private List<ISearchResult> adapterFoods;
  private final int HEADER_TYPE = 0;
  private final int ITEM_TYPE = 1;
  private final int EMPTY_LIST = 1;
  private List<List<BasketEntity>> allFood;
  private String[] namesSections;
  private BasketUpdater basketUpdater;
  private BasketDAO basketDAO;
  private CountDownTimer downTimer;
  private BasketEntity deleteEntity;
  private int deletePosition;
  private boolean isHasNextItem = false;
  private Context context;

  @Override public void saveFood(String date) {
    FoodWork.saveMixedList(adapterFoods, date);
    clearDB();
  }

  @Override
  public void emergencyDelete() {
    if (downTimer != null){
      downTimer.onFinish();
    }
  }

  private void clearDB() {
    Completable.fromAction(new Action() {
      @Override
      public void run() throws Exception {
        basketDAO.deleteAll();
      }
    }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe();
  }

  @Override public void cancelRemove() {
    returnRemovedItem(deleteEntity, deletePosition);
    basketUpdater.handleUndoCard(false);
    if (downTimer != null){
      downTimer.cancel();
    }
    downTimer = null;
  }

  @Override public void moveItem(int from, int to) {
    Collections.swap(adapterFoods, from, to);
    setEatingType(to);
    reWrite((BasketEntity) adapterFoods.get(to));
    notifyItemMoved(from, to);
  }

  private void setEatingType(int to) {
    for (int i = to; i >= 0; i--) {
      if (adapterFoods.get(i) instanceof HeaderObj){
        int type = ((HeaderObj) adapterFoods.get(i)).getType();
        ((BasketEntity) adapterFoods.get(to)).setEatingType(type);
        break;
      }
    }
  }

  public BasketAdapter(List<List<BasketEntity>> allFood, String[] namesSections,
      BasketUpdater basketUpdater, Context context) {
    this.basketUpdater = basketUpdater;
    this.allFood = allFood;
    this.namesSections = namesSections;
    this.basketUpdater = basketUpdater;
    this.context = context;
    formGlobalList();
    basketUpdater.getCurrentSize(getRealSize());
    basketDAO = App.getInstance().getFoodDatabase().basketDAO();
  }

  private void formGlobalList() {
    adapterFoods = new ArrayList<>();
    for (int i = 0; i < allFood.size(); i++) {
      if (allFood.get(i).size() > 0) {
        adapterFoods.add(new HeaderObj(i, namesSections[i], false));
        for (int j = 0; j < allFood.get(i).size(); j++) {
          adapterFoods.add(allFood.get(i).get(j));
        }
      }
    }
  }

  @NonNull @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    switch (viewType) {
      case HEADER_TYPE:
        return new BasketHeaderVH(layoutInflater, parent);
      case ITEM_TYPE:
        return new BasketItemVH(layoutInflater, parent);
      default:
        throw new IllegalArgumentException("Invalid view type");
    }
  }

  @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    switch (holder.getItemViewType()) {
      case HEADER_TYPE:
        ((BasketHeaderVH) holder).bind((HeaderObj) adapterFoods.get(position));
        break;
      case ITEM_TYPE:
        ((BasketItemVH) holder).bind((BasketEntity) adapterFoods.get(position),
            new ClickListener() {
              @Override public void click(int position, boolean isNeedSave) {
                if (!isNeedSave) {
                  deleteEntity = (BasketEntity) adapterFoods.get(position);
                  deletePosition = removeItem(deleteEntity);
                  if (adapterFoods.size() != EMPTY_LIST) {
                    runCountdown(deleteEntity);
                  }else {
                    deleteItem(deleteEntity);
                  }
                }
              }

              @Override public void open(int position) {
                BasketEntity entity = (BasketEntity) adapterFoods.get(position);
                openDetailScreen(entity);
              }
            });
        break;
    }
  }

  private void openDetailScreen(BasketEntity entity) {
    Intent intent = new Intent(new Intent(context, DetailActivity.class));
    intent.putExtra(Config.INTENT_DETAIL_FOOD, entity);
    ((Activity)context).startActivityForResult(intent, 1);
  }

  private void runCountdown(BasketEntity basketEntity) {
    if (downTimer != null){
      isHasNextItem = true;
      downTimer.onFinish();
    }else {
      basketUpdater.handleUndoCard(true);
    }
    downTimer  = new CountDownTimer(3000, 100) {
      @Override
      public void onTick(long millisUntilFinished) {
      }

      @Override
      public void onFinish() {
        deleteItem(basketEntity);
        if (!isHasNextItem){
          basketUpdater.handleUndoCard(false);
        }else {
          isHasNextItem = false;
        }
        downTimer = null;
      }
    }.start();
  }

  private void returnRemovedItem(BasketEntity basketEntity, int position) {
    adapterFoods.add(position, basketEntity);
    notifyItemInserted(position);
    basketUpdater.getCurrentSize(getRealSize());
  }

  private void reWrite(BasketEntity basketEntity) {
    Completable.fromAction(new Action() {
      @Override
      public void run() throws Exception {
        basketDAO.insert(basketEntity);
      }
    }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe();
  }

  private void deleteItem(BasketEntity basketEntity) {
    Completable.fromAction(new Action() {
      @Override
      public void run() throws Exception {
        basketDAO.deleteById(basketEntity.getId());
      }
    }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe();
  }

  private int removeItem(BasketEntity basketEntity) {
    int position = getPosition(basketEntity);
    notifyItemRemoved(position);
    basketUpdater.getCurrentSize(getRealSize());
    return position;
  }

  private int getPosition(BasketEntity basketEntity) {
    int position = -1;
    for (int i = 0; i < adapterFoods.size(); i++) {
      if (adapterFoods.get(i) instanceof BasketEntity) {
        if (((BasketEntity )adapterFoods.get(i)).getId() == basketEntity.getId()) {
          adapterFoods.remove(i);
          position = i;
          break;
        }
      }
    }
    return position;
  }

  private int getRealSize() {
    int counter = 0;
    for (int i = 0; i < adapterFoods.size(); i++) {
      if (adapterFoods.get(i) instanceof BasketEntity){
        counter++;
      }
    }
    return counter;
  }

  @Override public int getItemCount() {
    return adapterFoods.size();
  }

  @Override public int getItemViewType(int position) {
    if (adapterFoods.get(position) instanceof HeaderObj) {
      return HEADER_TYPE;
    } else if (adapterFoods.get(position) instanceof BasketEntity) {
      return ITEM_TYPE;
    } else {
      return -1;
    }
  }
}
