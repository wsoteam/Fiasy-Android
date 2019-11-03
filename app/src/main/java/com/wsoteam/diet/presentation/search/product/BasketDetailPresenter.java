package com.wsoteam.diet.presentation.search.product;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.search.product.claim.ClaimAlert;
import com.wsoteam.diet.presentation.search.basket.db.BasketEntity;

import java.util.ArrayList;

import static com.wsoteam.diet.Config.STANDART_PORTION;

@InjectViewState
public class BasketDetailPresenter extends MvpPresenter<DetailView> {
    private Context context;
    private BasketEntity basketEntity;
    private final int MINIMAL_PORTION = 1;
    private final int NUMBER_CUSTOM_PORTION = 1;
    private ArrayList<Integer> portionsSizes;
    private int position = 0;

    public BasketDetailPresenter(Context context, BasketEntity basketEntity) {
        this.context = context;
        this.basketEntity = basketEntity;
    }

    @Override
    protected void onFirstViewAttach() {
        handlePortions();
        getViewState().fillFields(basketEntity.getName(), basketEntity.getFats(), basketEntity.getCarbohydrates(),
                basketEntity.getProteins(), basketEntity.getBrand(), basketEntity.getSugar(), basketEntity.getSaturatedFats(),
                basketEntity.getMonoUnSaturatedFats(), basketEntity.getPolyUnSaturatedFats(), basketEntity.getCholesterol(), basketEntity.getCellulose(), basketEntity.getSodium(), basketEntity.getPottassium(), basketEntity.getEatingType(), portionsSizes.get(0));
    }

    private void handlePortions() {
        portionsSizes = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        if (basketEntity.getSizePortion() != STANDART_PORTION) {
            portionsSizes.add(basketEntity.getSizePortion());
            names.add(basketEntity.getNamePortion());
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

        String prot = String.valueOf(Math.round(count * portionSize * basketEntity.getProteins())) + " " + context.getResources().getString(R.string.gramm);
        String carbo = String.valueOf(Math.round(count * portionSize * basketEntity.getCarbohydrates())) + " " + context.getResources().getString(R.string.gramm);
        String fats = String.valueOf(Math.round(count * portionSize * basketEntity.getFats())) + " " + context.getResources().getString(R.string.gramm);
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
}
