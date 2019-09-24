package com.wsoteam.diet.Articles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.wsoteam.diet.Articles.POJO.Article;
import com.wsoteam.diet.Articles.POJO.ArticlesHolder;
import com.wsoteam.diet.Articles.POJO.ListArticles;
import com.wsoteam.diet.Articles.POJO.SectionArticles;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.Analytics.Events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static android.content.Context.MODE_PRIVATE;

public class ListArticlesFragment extends Fragment implements Observer {

    @BindView(R.id.clFailSearch) ConstraintLayout constraintLayout;
    @BindView(R.id.rvArticle) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    private Unbinder unbinder;
    private ListArticlesAdapter adapter;
    private VerticalArticlesAdapter verticalArticlesAdapter;
    private SectionArticles sectionArticles;

    private boolean isSubSection;
    private List<Article> subList;

    private void startDetailActivity(Article article) {
        Intent intent;

        for (int i = 0; i < ArticlesHolder.getListArticles().getListArticles().size(); i++) {
            if (article.getTitle()
                    .equals(ArticlesHolder.getListArticles().getListArticles().get(i).getTitle())) {
                if (!checkSubscribe() && ArticlesHolder.getListArticles()
                        .getListArticles()
                        .get(i)
                        .isPremium()) {
                    intent = new Intent(getActivity(), ItemArticleWithoutPremActivity.class);
                } else {
                    intent = new Intent(getActivity(), ItemArticleActivity.class);
                }
                intent.putExtra(Config.ARTICLE_INTENT, i);
                getActivity().startActivity(intent);
                break;
            }
        }
    }

    private boolean checkSubscribe() {
        SharedPreferences sharedPreferences =
                getActivity().getSharedPreferences(Config.STATE_BILLING, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(Config.STATE_BILLING, false)) {
            return true;
        } else {
            return false;
        }
    }

    HorizontalArticlesAdapter.OnItemClickListener onItemClickListener =
            new HorizontalArticlesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, Article article) {
                    startDetailActivity(article);
                }

                @Override
                public void onClickAll(View view, int position, ListArticles listArticles) {
                    subList = listArticles.getListArticles();
                    isSubSection = true;
                    adapter.updateData(listArticles.getListArticles());
                    if (listArticles.getName().equalsIgnoreCase(getResources().getString(R.string.eating_trigger))){
                        Events.logViewArticlesDiet();
                    }else {
                        Events.logViewArticlesTraining();
                    }
                    recyclerView.setAdapter(adapter);
                    mToolbar.setNavigationIcon(R.drawable.back_arrow_icon_white);
                    mToolbar.setTitle(listArticles.getName());
                }
            };

    private ListArticlesAdapter.OnItemClickListener clickListener = new ListArticlesAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, Article article) {
            startDetailActivity(article);
        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isSubSection = false;
            recyclerView.setAdapter(verticalArticlesAdapter);
            mToolbar.setTitle("Статьи");
            mToolbar.setNavigationIcon(null);
        }
    };

    private SearchView.OnQueryTextListener textListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            search(s);
            return false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#9C5E21"));

        View view = inflater.inflate(R.layout.fragment_list_articles, container, false);
        unbinder = ButterKnife.bind(this, view);

        mToolbar.setTitle("Статьи");
        mToolbar.inflateMenu(R.menu.toolbar_menu);
        mToolbar.setNavigationIcon(null);
        mToolbar.setNavigationOnClickListener(onClickListener);
        mToolbar.setTitleTextColor(0xFFFFFFFF);

        Menu menu = mToolbar.getMenu();
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setOnQueryTextListener(textListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (ArticlesHolder.getListArticles() != null) {

            sectionArticles = new SectionArticles(ArticlesHolder.getListArticles().getListArticles());
            verticalArticlesAdapter = new VerticalArticlesAdapter(sectionArticles.getGroups());
            verticalArticlesAdapter.SetOnItemClickListener(onItemClickListener);

            adapter = new ListArticlesAdapter(ArticlesHolder.getListArticles().getListArticles(), clickListener);
            recyclerView.setAdapter(verticalArticlesAdapter);
        } else {

            ArticlesHolder.subscribe(this);
            recyclerView.setAdapter(null);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void search(String str) {
        String key = str.toLowerCase();
        List<Article> result = new ArrayList<>();
        List<Article> articles;
        if (isSubSection) {
            articles = subList;
        } else {
            articles = ArticlesHolder.getListArticles().getListArticles();
        }

        if (key.equals("")) {
            constraintLayout.setVisibility(View.INVISIBLE);
            if (isSubSection) {
                adapter.updateData(subList);
                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setAdapter(verticalArticlesAdapter);
            }
        } else {
            for (Article article :
                    articles) {
                if (article.getTitle().toLowerCase().contains(key)) {
                    result.add(article);
                }
            }
            if (result.size() == 0) {
                constraintLayout.setVisibility(View.VISIBLE);
            } else {
                constraintLayout.setVisibility(View.INVISIBLE);
            }
            ListArticlesAdapter newAdapter = new ListArticlesAdapter(result, clickListener);
            recyclerView.setAdapter(newAdapter);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        adapter =
                new ListArticlesAdapter(ArticlesHolder.getListArticles().getListArticles(), clickListener);
        sectionArticles = new SectionArticles(ArticlesHolder.getListArticles().getListArticles());
        verticalArticlesAdapter = new VerticalArticlesAdapter(sectionArticles.getGroups());
        recyclerView.setAdapter(verticalArticlesAdapter);
        ArticlesHolder.unsubscribe(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Article> list = ArticlesHolder.getListArticles().getListArticles();
        if (list != null) Collections.shuffle(list);
    }

    @Override
    public void onPause() {
        ArticlesHolder.unsubscribe(this);
        super.onPause();
    }
}
