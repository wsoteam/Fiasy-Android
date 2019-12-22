package com.wsoteam.diet.articles.recycler;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.model.Article;
import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


  private SeriesCallback callback;
  private List<Article> articles;

  private final int VH_DEFF = 0, VH_TOP = 1;

  public SeriesAdapter(List<Article> articles, @NonNull SeriesCallback callback){

    this.articles = articles;
    this.callback = callback;

  }

  public void setListArticles(List<Article> articles){
    this.articles = articles;
    notifyDataSetChanged();
  }


  @NonNull @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    switch (viewType){
      case VH_TOP: return new SeriesTopViewHolder(parent);
      default: return new SeriesViewHolder(parent, callback);
    }

  }

  @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (position == 0) return;

    boolean isActive = false;
    if (UserDataHolder.getUserData() != null &&
        UserDataHolder.getUserData().getArticleSeries() != null){
      //long day = (new Date().getTime() - UserDataHolder.getUserData().getArticleSeries().get("burlakov")) / (60*60*24*1000);
      int day = UserDataHolder.getUserData().getArticleSeries().get("burlakov").getUnlockedArticles();
       isActive = day > position - 1;
    }

    ((SeriesViewHolder)holder).bind(articles.get(position - 1), position, isActive,
        position == 1, position == articles.size());
  }

  @Override public int getItemCount() {
    if (articles == null) return 1;
    else return articles.size() + 1;

  }

  @Override public int getItemViewType(int position) {
    if (position == 0) return VH_TOP;
    else return VH_DEFF;
  }
}
