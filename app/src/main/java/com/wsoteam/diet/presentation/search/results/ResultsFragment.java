package com.wsoteam.diet.presentation.search.results;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.bumptech.glide.Glide;
import com.wsoteam.diet.App;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.common.diary.BasketWork;
import com.wsoteam.diet.common.networking.food.FoodResultAPI;
import com.wsoteam.diet.common.networking.food.FoodSearch;
import com.wsoteam.diet.common.networking.food.HeaderObj;
import com.wsoteam.diet.common.networking.food.ISearchResult;
import com.wsoteam.diet.common.networking.food.POJO.FoodResult;
import com.wsoteam.diet.common.networking.food.POJO.Result;
import com.wsoteam.diet.common.networking.food.suggest.Suggest;
import com.wsoteam.diet.presentation.search.ParentActivity;
import com.wsoteam.diet.presentation.search.basket.BasketActivity;
import com.wsoteam.diet.presentation.search.basket.db.BasketDAO;
import com.wsoteam.diet.presentation.search.basket.db.BasketEntity;
import com.wsoteam.diet.presentation.search.basket.db.HistoryDAO;
import com.wsoteam.diet.presentation.search.basket.db.HistoryEntity;
import com.wsoteam.diet.presentation.search.inspector.Inspector;
import com.wsoteam.diet.presentation.search.inspector.InspectorAlert;
import com.wsoteam.diet.presentation.search.results.controllers.BasketUpdater;
import com.wsoteam.diet.presentation.search.results.controllers.ResultAdapter;
import com.wsoteam.diet.presentation.search.results.controllers.suggestions.ISuggest;
import com.wsoteam.diet.presentation.search.results.controllers.suggestions.SuggestAdapter;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ResultsFragment extends MvpAppCompatFragment implements ResultsView {
  private final String EMPTY_BRAND = "null";

  ResultsPresenter presenter;
  Unbinder unbinder;
  @BindView(R.id.rvBlocks)
  RecyclerView rvBlocks;
  @BindView(R.id.ivSearchImage)
  ImageView ivSearchImage;
  @BindView(R.id.tvTitleEmptySearch)
  TextView tvTitleEmptySearch;
  @BindView(R.id.tvTextEmptySearch)
  TextView tvTextEmptySearch;
  @BindView(R.id.btnAddCustomFood)
  Button btnAddCustomFood;
  @BindView(R.id.tvCounter)
  TextView tvCounter;
  @BindView(R.id.cvBasket)
  CardView cvBasket;
  @BindView(R.id.tvAddToBasket)
  TextView tvAddToBasket;

  @BindView(R.id.rvSuggestionsList)
  RecyclerView rvSuggestionsList;
  @BindView(R.id.flSuggestParent)
  FrameLayout flSuggestParent;
  @BindView(R.id.clRoot)
  ConstraintLayout clRoot;

  private ProgressBar pbLoad;
  private Spinner parentSpinner;

  private ResultAdapter itemAdapter;
  private SuggestAdapter suggestAdapter;
  private FoodResultAPI foodResultAPI = FoodSearch.getInstance().getFoodSearchAPI();
  private BasketDAO basketDAO = App.getInstance().getFoodDatabase().basketDAO();
  private HistoryDAO historyDAO = App.getInstance().getFoodDatabase().historyDAO();
  private Animation finalSave;
  private int spinnerId;
  private boolean isWatcherDead = false;
  private boolean isOneWordSearch = true;
  private boolean isRestoreCall = false;
  private final String EMPTY_TAG = "EMPTY_TAG";
  private final String BASKET_VISIBILITY_TAG = "BASKET_VISIBILITY_TAG";

  @Override
  public void changeSpinner(int position) {
    if (isWatcherDead) {
      isWatcherDead = false;
    } else {
      if (BasketWork.isNeedShow(itemAdapter.getParams(), position)) {
        InspectorAlert.askChangeEatingId(itemAdapter.getParams(), position, getActivity(),
            new Inspector() {
              @Override
              public void getPermission(boolean isCanChange) {
                if (!isCanChange) {
                  isWatcherDead = true;
                  parentSpinner.setSelection(spinnerId);
                } else {
                  spinnerId = position;
                }
              }
            });
      } else {
        spinnerId = position;
      }
    }
  }

  @Override
  public void updateSearchField(String currentString) {
    if (isRestoreCall) {
      isRestoreCall = false;
    } else {
      currentString.trim();
      if (currentString.length() == 0) {
        hideSuggestView();
      } else {
        showSuggestions(currentString);
      }
    }
  }

  @Override
  public void updateBasket() {
    Single.fromCallable(() -> {
      List<BasketEntity> savedItems = getSavedItems();
      return savedItems;
    })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(t -> itemAdapter.setNewBasket(t), Throwable::printStackTrace);
  }

  private void showSuggestions(String currentString) {
    showSuggestView();
    switch (Locale.getDefault().getLanguage()) {
      case Config.RU:
        foodResultAPI
            .getSuggestions(currentString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> updateSuggestions(t, currentString), Throwable::printStackTrace);
        break;
      case Config.DE:
        foodResultAPI
            .getSuggestionsDe(currentString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> updateSuggestions(t, currentString), Throwable::printStackTrace);
        break;
      case Config.PT:
        foodResultAPI
            .getSuggestionsPt(currentString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> updateSuggestions(t, currentString), Throwable::printStackTrace);
        break;
      case Config.ES:
        foodResultAPI
            .getSuggestionsEs(currentString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> updateSuggestions(t, currentString), Throwable::printStackTrace);
        break;
      default:
      case Config.EN:
        foodResultAPI
            .getSuggestionsEn(currentString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> updateSuggestions(t, currentString), Throwable::printStackTrace);
        break;
    }
  }

  private void showSuggestView() {
    if (flSuggestParent.getVisibility() == View.GONE) {
      flSuggestParent.setVisibility(View.VISIBLE);
    }
  }

  private void hideSuggestView() {
    if (flSuggestParent.getVisibility() == View.VISIBLE) {
      flSuggestParent.setVisibility(View.GONE);
    }
  }

  @Override
  public void sendClearSearchField() {

  }

  @Override
  public void sendSearchQuery(String query) {
    isOneWordSearch = query.split(" ").length < 2;
    showLoad();
    hideSuggestView();
    if (isOneWordSearch) {
      oneWordSearch(query.trim());
    } else {
      manyWordSearch(query.trim());
    }
    Events.logSearch(query);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_results, container, false);
    unbinder = ButterKnife.bind(this, view);
    pbLoad = getActivity().findViewById(R.id.pbLoad);
    parentSpinner = getActivity().findViewById(R.id.spnEatingList);
    presenter = new ResultsPresenter();
    presenter.attachView(this);
    finalSave = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_meas_update);
    updateUI();
    if (savedInstanceState != null) {
      isRestoreCall = true;
      cvBasket.setVisibility(savedInstanceState.getInt(BASKET_VISIBILITY_TAG));
      itemAdapter.refreshBasket();
    } else {
      showHistory();
    }
    spinnerId = ((ParentActivity) getActivity()).spinnerId;
    return view;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    try {
      outState.putString(EMPTY_TAG, EMPTY_BRAND);
      outState.putInt(BASKET_VISIBILITY_TAG, cvBasket.getVisibility());
    }catch (Exception ex) {
      Log.e("LOL", "null view");
    }
    super.onSaveInstanceState(outState);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  private void updateSuggestions(Suggest t, String currentString) {
    suggestAdapter = null;
    suggestAdapter = new SuggestAdapter(t, currentString, new ISuggest() {
      @Override
      public void choiceSuggest(String suggestName) {
        ((ParentActivity) getActivity()).edtSearch.setText(suggestName);
        ((ParentActivity) getActivity()).edtSearch.clearFocus();

        InputMethodManager inputManager =
            (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(clRoot.getWindowToken(),
            InputMethodManager.HIDE_NOT_ALWAYS);

        sendSearchQuery(suggestName);
      }
    });
    rvSuggestionsList.setLayoutManager(new LinearLayoutManager(getActivity()));
    rvSuggestionsList.setAdapter(suggestAdapter);
  }

  private void showLoad() {
    if (pbLoad.getVisibility() == View.INVISIBLE) {
      pbLoad.setVisibility(View.VISIBLE);
    }
  }

  private void hideLoad() {
    if (pbLoad.getVisibility() == View.VISIBLE) {
      pbLoad.setVisibility(View.INVISIBLE);
    }
  }

  private void updateUI() {
    List<ISearchResult> foods = new ArrayList<>();
    if (itemAdapter == null) {
      itemAdapter = new ResultAdapter(foods, getActivity(), new ArrayList<BasketEntity>(), "",
          new BasketUpdater() {
            @Override
            public void getCurrentSize(int size) {
              updateBasket(size);
            }

            @Override
            public void handleUndoCard(boolean isShow) {

            }

            @Override
            public int getCurrentEating() {
              return 0;
            }
          });
    }
    rvBlocks.setLayoutManager(new LinearLayoutManager(getContext()));
    rvBlocks.setAdapter(itemAdapter);
  }

  private void updateBasket(int size) {
    if (cvBasket != null) {
      if (size > 0) {
        if (cvBasket.getVisibility() != View.VISIBLE) {
          cvBasket.setVisibility(View.VISIBLE);
        }
        tvCounter.setText(getPaintedString(size));
      } else if (cvBasket.getVisibility() == View.VISIBLE) {
        cvBasket.setVisibility(View.GONE);
      }
    }
  }

  private Spannable getPaintedString(int size) {
    String string = getActivity().getResources().getString(R.string.srch_basket_card, size);
    int positionPaint = string.indexOf(" ") + 1;
    Spannable spannable = new SpannableString(string);
    spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.srch_painted_string)),
        positionPaint,
        string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    spannable.setSpan(new UnderlineSpan(), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    return spannable;
  }

  private void hideMessageUI() {
    ivSearchImage.setVisibility(View.GONE);
    tvTitleEmptySearch.setVisibility(View.GONE);
    tvTextEmptySearch.setVisibility(View.GONE);
    btnAddCustomFood.setVisibility(View.GONE);
  }

  private void showNoHistory() {
    if (ivSearchImage != null) {
      Glide.with(getActivity()).load(R.drawable.scrh_first_search).into(ivSearchImage);
      tvTextEmptySearch.setText(getResources().getString(R.string.srch_empty_history));
      ivSearchImage.setVisibility(View.VISIBLE);
      tvTextEmptySearch.setVisibility(View.VISIBLE);
    }
  }

  private void showNoFind() {
    if (ivSearchImage != null) {
      Glide.with(getActivity()).load(R.drawable.empty_search).into(ivSearchImage);
      tvTextEmptySearch.setText(getResources().getString(R.string.srch_text_empty));
      ivSearchImage.setVisibility(View.VISIBLE);
      tvTitleEmptySearch.setVisibility(View.VISIBLE);
      tvTextEmptySearch.setVisibility(View.VISIBLE);
      btnAddCustomFood.setVisibility(View.VISIBLE);
    }
  }

  private void showHistory() {
    Single.fromCallable(() -> {
      List<HistoryEntity> historyEntities = getHistoryItems();
      return historyEntities;
    }).map(new Function<List<HistoryEntity>, List<ISearchResult>>() {
      @Override
      public List<ISearchResult> apply(List<HistoryEntity> historyEntities) {
        List<ISearchResult> list = new ArrayList<>();
        for (int i = 0; i < historyEntities.size(); i++) {
          list.add(historyEntities.get(i));
        }
        Collections.reverse(list);
        return list;
      }
    })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<List<ISearchResult>>() {
          @Override
          public void onSubscribe(Disposable d) {
          }

          @Override
          public void onSuccess(List<ISearchResult> iSearchResults) {
            if (iSearchResults.size() > 0 && rvBlocks != null) {
              updateAdapter(iSearchResults, new ArrayList<>(), "");
            } else {
              showNoHistory();
            }
          }

          @Override
          public void onError(Throwable e) {
          }
        });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  private void oneWordSearch(String searchString) {
    switch (Locale.getDefault().getLanguage()) {
      default:
      case Config.EN:
        foodResultAPI
            .searchEnNoBrand(searchString, 1, EMPTY_BRAND)
            .flatMap(foodResult -> foodResultAPI.searchEn(searchString, 1),
                (foodResult, foodResult1) -> mergeLists(foodResult, foodResult1))
            .map(foodResult -> dropBrands(foodResult))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> refreshAdapter(toISearchResult(t.getResults()), searchString),
                Throwable::printStackTrace);
        break;
      case Config.RU:
        foodResultAPI
            .searchNoBrand(searchString, 1, EMPTY_BRAND)
            .flatMap(foodResult -> foodResultAPI.search(searchString, 1),
                (foodResult, foodResult1) -> mergeLists(foodResult, foodResult1))
            .map(foodResult -> dropBrands(foodResult))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> refreshAdapter(toISearchResult(t.getResults()), searchString),
                Throwable::printStackTrace);
        break;
      case Config.DE:
        foodResultAPI
            .searchDeNoBrand(searchString, 1, EMPTY_BRAND)
            .flatMap(foodResult -> foodResultAPI.searchDe(searchString, 1),
                (foodResult, foodResult1) -> mergeLists(foodResult, foodResult1))
            .map(foodResult -> dropBrands(foodResult))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> refreshAdapter(toISearchResult(t.getResults()), searchString),
                Throwable::printStackTrace);
        break;
      case Config.PT:
        foodResultAPI
            .searchPtNoBrand(searchString, 1, EMPTY_BRAND)
            .flatMap(foodResult -> foodResultAPI.searchPt(searchString, 1),
                (foodResult, foodResult1) -> mergeLists(foodResult, foodResult1))
            .map(foodResult -> dropBrands(foodResult))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> refreshAdapter(toISearchResult(t.getResults()), searchString),
                Throwable::printStackTrace);
        break;
      case Config.ES:
        foodResultAPI
            .searchEsNoBrand(searchString, 1, EMPTY_BRAND)
            .flatMap(foodResult -> foodResultAPI.searchEs(searchString, 1),
                (foodResult, foodResult1) -> mergeLists(foodResult, foodResult1))
            .map(foodResult -> dropBrands(foodResult))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> refreshAdapter(toISearchResult(t.getResults()), searchString),
                Throwable::printStackTrace);
        break;
    }
  }

  private void manyWordSearch(String searchString) {
    switch (Locale.getDefault().getLanguage()) {
      default:
      case Config.EN:
        foodResultAPI
            .searchEn(searchString, 1)
            .map(foodResult -> dropBrands(foodResult))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> refreshAdapter(toISearchResult(t.getResults()), searchString),
                Throwable::printStackTrace);
        break;
      case Config.RU:
        foodResultAPI
            .search(searchString, 1)
            .map(foodResult -> dropBrands(foodResult))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> refreshAdapter(toISearchResult(t.getResults()), searchString),
                Throwable::printStackTrace);
        break;
      case Config.DE:
        foodResultAPI
            .searchDe(searchString, 1)
            .map(foodResult -> dropBrands(foodResult))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> refreshAdapter(toISearchResult(t.getResults()), searchString),
                Throwable::printStackTrace);
        break;
      case Config.PT:
        foodResultAPI
            .searchPt(searchString, 1)
            .map(foodResult -> dropBrands(foodResult))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> refreshAdapter(toISearchResult(t.getResults()), searchString),
                Throwable::printStackTrace);
        break;
      case Config.ES:
        foodResultAPI
            .searchEs(searchString, 1)
            .map(foodResult -> dropBrands(foodResult))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> refreshAdapter(toISearchResult(t.getResults()), searchString),
                Throwable::printStackTrace);
        break;
    }
  }

  private FoodResult mergeLists(FoodResult foodResult, FoodResult foodResult1) {
    foodResult.getResults().addAll(foodResult1.getResults());
    return foodResult;
  }

  private FoodResult dropBrands(FoodResult foodResult) {
    if (foodResult.getResults().size() > 0) {
      for (int i = 0; i < foodResult.getResults().size(); i++) {
        try {
          if (foodResult.getResults().get(i).getName() == null) {
            foodResult.getResults().remove(i);
            if (foodResult.getResults().size() != 0) {
              i--;
            }
          } else if (foodResult.getResults()
              .get(i)
              .getBrand()
              .getName()
              .equalsIgnoreCase(EMPTY_BRAND)) {
            foodResult.getResults().get(i).getBrand().setName("");
          }
        } catch (Exception e) {
          Log.e("LOL", e.getMessage());
        }
      }
    }
    return foodResult;
  }

  private List<ISearchResult> toISearchResult(List<Result> results) {
    List<ISearchResult> list = new ArrayList<>();
    for (int i = 0; i < results.size(); i++) {
      list.add(results.get(i));
    }
    return list;
  }

  private void refreshAdapter(List<ISearchResult> list, String searchString) {
    Single.fromCallable(() -> {
      List<BasketEntity> savedItems = getSavedItems();
      return savedItems;
    })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(t -> updateAdapter(list, t, searchString), Throwable::printStackTrace);
    if (list.size() > 0) {
      hideMessageUI();
    } else {
      showNoFind();
    }
  }

  private List<BasketEntity> getSavedItems() {
    List<BasketEntity> entities = basketDAO.getAll();
    return entities;
  }

  private List<HistoryEntity> getHistoryItems() {
    List<HistoryEntity> entities = historyDAO.getAll();
    return entities;
  }

  private void updateAdapter(List<ISearchResult> t, List<BasketEntity> basketEntities,
      String searchString) {
    rvBlocks.setAdapter(itemAdapter = new ResultAdapter(createHeadersArray(t), getActivity(),
        basketEntities, searchString, new BasketUpdater() {
      @Override
      public void getCurrentSize(int size) {
        updateBasket(size);
      }

      @Override
      public void handleUndoCard(boolean isShow) {

      }

      @Override
      public int getCurrentEating() {
        return ((ParentActivity) getActivity()).spinnerId;
      }
    }));
    hideLoad();
  }

  private List<ISearchResult> createHeadersArray(List<ISearchResult> t) {
    List<ISearchResult> iSearchResults = new ArrayList<>();
    if (t.size() > 0) {
      if (t.get(0) instanceof HistoryEntity) {
        iSearchResults.add(
            new HeaderObj(getResources().getString(R.string.srch_history_header), true));
      } else {
        iSearchResults.add(
            new HeaderObj(getResources().getString(R.string.srch_search_results), false));
      }
    }
    for (int i = 0; i < t.size(); i++) {
      iSearchResults.add(t.get(i));
    }

    return iSearchResults;
  }

  @OnClick({ R.id.btnAddCustomFood, R.id.tvCounter, R.id.tvAddToBasket })
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.btnAddCustomFood:
        break;
      case R.id.tvCounter:
        getActivity().startActivityForResult(
            new Intent(getActivity(), BasketActivity.class).putExtra(Config.INTENT_DATE_FOR_SAVE,
                getActivity().getIntent().getStringExtra(Config.INTENT_DATE_FOR_SAVE)),
            Config.RC_BASKET_LIST);
        break;
      case R.id.tvAddToBasket:
        tvAddToBasket.setEnabled(false);
        itemAdapter.save(getActivity().getIntent().getStringExtra(Config.INTENT_DATE_FOR_SAVE));
        runCountdown();
        break;
    }
  }

  private void runCountdown() {
    Toast toast = new Toast(getActivity());
    toast.setView(LayoutInflater.from(getActivity()).inflate(R.layout.toast_meas_update, null));
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.setDuration(Toast.LENGTH_SHORT);
    TextView title = toast.getView().findViewById(R.id.title);
    ImageView ellipse = toast.getView().findViewById(R.id.ivEllipse);
    title.setText(getResources().getString(R.string.srch_save_list));
    ellipse.setAnimation(finalSave);
    toast.show();
    CountDownTimer timer = new CountDownTimer(2000, 100) {
      @Override
      public void onTick(long l) {

      }

      @Override
      public void onFinish() {
        getActivity().finish();
      }
    }.start();
  }
}
