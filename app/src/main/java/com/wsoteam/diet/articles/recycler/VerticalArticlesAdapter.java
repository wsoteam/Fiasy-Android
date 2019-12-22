package com.wsoteam.diet.articles.recycler;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.wsoteam.diet.Recipes.EmptyViewHolder;
import com.wsoteam.diet.articles.POJO.ListArticles;
import com.wsoteam.diet.R;
import com.wsoteam.diet.articles.recycler.BurlakovViewHolder;
import com.wsoteam.diet.articles.recycler.HorizontalArticlesAdapter;
import java.util.List;

public class VerticalArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  List<ListArticles> sectionsArticles;
  private SparseIntArray listPosition = new SparseIntArray();
  private HorizontalArticlesAdapter.OnItemClickListener mItemClickListener;
  private Context mContext;
  private RecyclerView.RecycledViewPool viewPool;

  private final int VH_DEFF = 0, VH_BANNER = 1, VH_EMPTY = 2;

  public VerticalArticlesAdapter(
      List<ListArticles> sectionsArticles) {
    this.sectionsArticles = sectionsArticles;
    viewPool = new RecyclerView.RecycledViewPool();
  }

  @NonNull @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    mContext = viewGroup.getContext();
    switch (viewType) {
      case VH_EMPTY: return new EmptyViewHolder(viewGroup);
      case VH_BANNER: return new BurlakovViewHolder(viewGroup, new View.OnClickListener() {
        @Override public void onClick(View v) {
            mItemClickListener.onClickBanner(v);
        }
      });
      default: {
        View v0 = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.group_articles_item, viewGroup, false);
        return new VerticalViewHolder(v0);
      }
    }
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

    switch (viewHolder.getItemViewType()) {
      case VH_EMPTY: break;
      case VH_BANNER: break;
      default: {
        VerticalViewHolder holder = (VerticalViewHolder) viewHolder;

        holder.setData(sectionsArticles.get(position - 1));

        int lastSeenFirstPosition = listPosition.get(position - 1, 0);
        if (lastSeenFirstPosition >= 0) {
          holder.layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, 0);
        }
        break;
      }
    }
  }

  @Override
  public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {

    if (viewHolder instanceof VerticalViewHolder) {
      final int position = viewHolder.getAdapterPosition();
      VerticalViewHolder cellViewHolder = (VerticalViewHolder) viewHolder;
      int firstVisiblePosition = cellViewHolder.layoutManager.findFirstVisibleItemPosition();
      listPosition.put(position, firstVisiblePosition);
    }
    super.onViewRecycled(viewHolder);
  }

  public void updateData(List<ListArticles> sectionsArticles){
    this.sectionsArticles = sectionsArticles;
    notifyDataSetChanged();
  }

  @Override public int getItemCount() {
    if (sectionsArticles == null) return 1;

    return sectionsArticles.size() + 1;
  }

  @Override public int getItemViewType(int position) {
    switch (position){
      case 0: return VH_EMPTY;
      case 1: return VH_BANNER;
      default: return VH_DEFF;
    }
  }

  // for both short and long click
  public void SetOnItemClickListener(
      final HorizontalArticlesAdapter.OnItemClickListener mItemClickListener) {
    this.mItemClickListener = mItemClickListener;
  }

  class VerticalViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvGroupName) TextView tvGroupName;
    @BindView(R.id.recycler) RecyclerView recyclerView;
    private HorizontalArticlesAdapter adapter;
    private LinearLayoutManager layoutManager;

    public VerticalViewHolder(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      recyclerView.setRecycledViewPool(viewPool);
      recyclerView.setHasFixedSize(true);
      layoutManager = new LinearLayoutManager(mContext);
      layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
      recyclerView.setLayoutManager(layoutManager);

      adapter = new HorizontalArticlesAdapter();
      adapter.SetOnItemClickListener(mItemClickListener);
      recyclerView.setAdapter(adapter);
    }

    @OnClick({ R.id.llShowAll })
    void onClicked(View view) {
      mItemClickListener.onClickAll(view, getAdapterPosition() - 1, sectionsArticles.get(getAdapterPosition() - 1));
    }

    public void setData(ListArticles listArticles) {
      tvGroupName.setText(listArticles.getName());
      adapter.updateList(listArticles.getListArticles());
    }
  }
}
