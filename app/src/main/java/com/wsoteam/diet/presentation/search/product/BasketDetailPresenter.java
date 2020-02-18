package com.wsoteam.diet.presentation.search.product;

import android.content.Context;

import android.icu.text.SymbolTable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.wsoteam.diet.App;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.diary.BasketWork;
import com.wsoteam.diet.common.diary.Finisher;
import com.wsoteam.diet.presentation.search.basket.db.BasketDAO;
import com.wsoteam.diet.presentation.search.product.claim.ClaimAlert;
import com.wsoteam.diet.presentation.search.basket.db.BasketEntity;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static com.wsoteam.diet.Config.STANDART_PORTION;

@InjectViewState
public class BasketDetailPresenter extends MvpPresenter<DetailView> {
  private Context context;
  private BasketEntity basketEntity;
  private final int MINIMAL_PORTION = 1;
  private final int NUMBER_CUSTOM_PORTION = 1;
  private final int DEFAULT_PORTION = 100;
  private ArrayList<Integer> portionsSizes;
  private int position = 0;
  private final int EMPTY_FIELD = -1;
  private BasketDAO basketDAO;
  private int defaultCountPortions;


  public BasketDetailPresenter(Context context, BasketEntity basketEntity) {
    this.context = context;
    this.basketEntity = basketEntity;
    basketDAO = App.getInstance().getFoodDatabase().basketDAO();
  }

  @Override
  protected void onFirstViewAttach() {
    defaultCountPortions = basketEntity.getCountPortions();
    handlePortions();
    int portionSize = portionsSizes.get(0);
    getViewState().fillFields(basketEntity.getName(), basketEntity.getFats(),
        basketEntity.getCarbohydrates(),
        basketEntity.getProteins(), basketEntity.getBrand(), basketEntity.getSugar(),
        basketEntity.getSaturatedFats(),
        basketEntity.getMonoUnSaturatedFats(), basketEntity.getPolyUnSaturatedFats(),
        basketEntity.getCholesterol(), basketEntity.getCellulose(),
        basketEntity.getSodium(), basketEntity.getPottassium(), basketEntity.getEatingType(),
        portionSize, basketEntity.isLiquid());
  }



  public void handleAutoPaste() {
    getViewState().pasteDefaultWeight(defaultCountPortions);
  }

  void handlePortions() {
    portionsSizes = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();

    if (basketEntity.getSizePortion() > Config.DEFAULT_WEIGHT) {
      String unit = context.getResources().getString(R.string.srch_gramm);
      if (basketEntity.isLiquid()){
        unit = context.getResources().getString(R.string.srch_ml);
      }
      portionsSizes.add(basketEntity.getSizePortion());
      names.add(basketEntity.getNamePortion() + " (" + String.valueOf(basketEntity.getSizePortion()) + unit + ")");
    }

    if (basketEntity.getNamePortion().equals(Config.DEFAULT_PORTION_NAME) && basketEntity.getCountPortions() == Config.DEFAULT_PORTION){
      basketEntity.makeAtomicDefault();
    }

    if (basketEntity.getNamePortion() != null && !basketEntity.getNamePortion().equals(Config.DEFAULT_PORTION_NAME) && basketEntity.getSizePortion() != 0 && basketEntity.getCountPortions() != 0){
      basketEntity.makeAtomic(basketEntity.getCountPortions(), basketEntity.getSizePortion());
    }

    portionsSizes.add(MINIMAL_PORTION);
    if (basketEntity.isLiquid()) {
      names.add(context.getResources().getString(R.string.srch_ml));
    } else {
      names.add(context.getResources().getString(R.string.srch_gramm));
    }

    getViewState().fillPortionSpinner(names);
  }


  void calculate(CharSequence weight) {
    double count = Double.parseDouble(weight.toString());
    int portionSize = portionsSizes.get(position);

    String prot =
        String.valueOf(Math.round(count * portionSize * basketEntity.getProteins())) + " " + context
            .getResources()
            .getString(R.string.srch_gramm);
    String carbo = String.valueOf(Math.round(count * portionSize * basketEntity.getCarbohydrates()))
        + " "
        + context.getResources().getString(R.string.srch_gramm);
    String fats = String.valueOf(Math.round(count * portionSize * basketEntity.getFats()))
        + " "
        + context.getResources().getString(R.string.srch_gramm);
    String kcal = String.valueOf(Math.round(count * portionSize * basketEntity.getCalories()));

    getViewState().showResult(kcal, prot, carbo, fats);
  }

  public void showClaimAlert() {
    ClaimAlert.create(context, basketEntity);
  }

  public void changePortion(int position) {
    this.position = position;
    getViewState().refreshCalculating();
  }

  public void prepareToSave(String weight, String prot, String fats, String carbo, String kcal,
      int selectedItemPosition) {
    int calories = Integer.parseInt(kcal);
    int carbohydrates = Integer.parseInt(carbo.split(" ")[0]);
    int proteins = Integer.parseInt(prot.split(" ")[0]);
    int fat = Integer.parseInt(fats.split(" ")[0]);
    int countPortions = Integer.parseInt(weight);

    basketEntity.setCalories(calories);
    basketEntity.setFats(fat);
    basketEntity.setCarbohydrates(carbohydrates);
    basketEntity.setProteins(proteins);
    basketEntity.setCountPortions(countPortions);
    basketEntity.setEatingType(selectedItemPosition);
    basketEntity.setId(0);

    if (portionsSizes.get(position) == MINIMAL_PORTION) {
      basketEntity.setDeepId(-1);
      basketEntity.setNamePortion(Config.DEFAULT_CUSTOM_NAME);
      basketEntity.setSizePortion(1);
    }

    if (basketEntity.getSugar() != EMPTY_FIELD) {
      basketEntity.setSugar(countPortions * portionsSizes.get(position) * basketEntity.getSugar());
    }

    if (basketEntity.getSaturatedFats() != EMPTY_FIELD) {
      basketEntity.setSaturatedFats(
          countPortions * portionsSizes.get(position) * basketEntity.getSaturatedFats());
    }

    if (basketEntity.getMonoUnSaturatedFats() != EMPTY_FIELD) {
      basketEntity.setMonoUnSaturatedFats(
          countPortions * portionsSizes.get(position) * basketEntity.getMonoUnSaturatedFats());
    }

    if (basketEntity.getPolyUnSaturatedFats() != EMPTY_FIELD) {
      basketEntity.setPolyUnSaturatedFats(
          countPortions * portionsSizes.get(position) * basketEntity.getPolyUnSaturatedFats());
    }

    if (basketEntity.getCholesterol() != EMPTY_FIELD) {
      basketEntity.setCholesterol(
          countPortions * portionsSizes.get(position) * basketEntity.getCholesterol());
    }

    if (basketEntity.getCellulose() != EMPTY_FIELD) {
      basketEntity.setCellulose(
          countPortions * portionsSizes.get(position) * basketEntity.getCellulose());
    }

    if (basketEntity.getSodium() != EMPTY_FIELD) {
      basketEntity.setSodium(
          countPortions * portionsSizes.get(position) * basketEntity.getSodium());
    }

    if (basketEntity.getPottassium() != EMPTY_FIELD) {
      basketEntity.setPottassium(
          countPortions * portionsSizes.get(position) * basketEntity.getPottassium());
    }
    saveEntity(basketEntity, selectedItemPosition);
  }

  public void saveEntity(BasketEntity basketEntity, int eating) {
    BasketWork.addToBasket(basketEntity, new Finisher() {
      @Override
      public void finish() {
        getViewState().close(eating);
      }
    });
  }
}
