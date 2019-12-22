package com.wsoteam.diet.articles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.articles.POJO.SectionArticles;
import com.wsoteam.diet.articles.recycler.SeriesAdapter;
import com.wsoteam.diet.articles.recycler.SeriesCallback;
import com.wsoteam.diet.model.ApiResult;
import com.wsoteam.diet.model.Article;
import com.wsoteam.diet.model.ArticleViewModel;
import com.wsoteam.diet.model.OpenArticles;
import com.wsoteam.diet.utils.Subscription;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class ArticleSeriesFragment extends Fragment {

  @BindView(R.id.recycler) RecyclerView recyclerView;
  SeriesAdapter adapter;

  private ArticleViewModel model;
  private LiveData<ApiResult<Article>> data;

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    try {
        long day = (new Date().getTime() - UserDataHolder.getUserData()
            .getArticleSeries()
            .get("burlakov")
            .getDate()) / (60 * 60 * 24 * 1000);
        if (day >= 1.0 && Subscription.check(getContext())) {
          OpenArticles openArticles = UserDataHolder.getUserData().getArticleSeries().get("burlakov");
          openArticles.setDate(new Date().getTime());
          openArticles.setUnlockedArticles(openArticles.getUnlockedArticles() + 1);
          WorkWithFirebaseDB.addArticleSeries(openArticles);
        }
    }catch (Exception e){
      Log.d("kkk", "onCreateView: ", e);
    }

    View view = inflater.inflate(R.layout.activity_article_series, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    adapter = new SeriesAdapter(null, new SeriesCallback() {
      @Override public void Clicked(View v, Article article) {
     startDetailActivity(article);
      }
    });

    model = ViewModelProviders.of(this).get(ArticleViewModel.class);
    data = model.getData();
    data.observe(this,
        new androidx.lifecycle.Observer<ApiResult<Article>>() {
          @Override
          public void onChanged(ApiResult<Article> articleApiResult) {
            adapter.setListArticles(new SectionArticles(articleApiResult.getResults(), getContext()).getGroups().get(0).getListArticles());
          }
        });


    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter);
  }

  private void startDetailActivity(Article article) {

    Intent intent;

    if (!Subscription.check(getContext()) && article.isPremium()) {
      intent = new Intent(getActivity(), ItemArticleWithoutPremActivity.class);
    } else {
      intent = new Intent(getActivity(), ItemArticleActivity.class);
    }
    intent.putExtra(Config.ARTICLE_INTENT, article.getId());

    getActivity().startActivity(intent);
  }
}
