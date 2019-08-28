package com.wsoteam.diet.presentation.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.activity.ActivitiesAdapter.UserActivityView;
import com.wsoteam.diet.presentation.activity.ExercisesSource.AssetsSource;
import com.wsoteam.diet.utils.Metrics;
import com.wsoteam.diet.utils.RandomTool;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wsoteam.diet.presentation.activity.ActivitiesAdapter.VIEW_TYPE_ACTIVITY;

public class UserActivityFragment extends Fragment implements
    // Loool
    Toolbar.OnMenuItemClickListener,
    PopupMenu.OnMenuItemClickListener {

  private Toolbar toolbar;

  private RecyclerView container;
  private ActivitiesAdapter adapter;

  private SparseArrayCompat<ExercisesSource> sources = new SparseArrayCompat<>();
  private CompositeDisposable disposables = new CompositeDisposable();

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_user_activity, container, false);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    toolbar = view.findViewById(R.id.toolbar);
    toolbar.inflateMenu(R.menu.fragment_user_activity_toolbar_menu);
    toolbar.setOnMenuItemClickListener(this);

    container = view.findViewById(R.id.container);

    container.setAdapter(adapter = new ActivitiesAdapter());
    container.addItemDecoration(new DividerDecoration(requireContext()));
    container.setLayoutManager(new LinearLayoutManager(requireContext()));

    adapter.setSearchListener(this::search);
    adapter.addItemClickListener(new ActivitiesAdapter.AdapterItemsClickListener() {
      @Override
      public void onSectionClick(ActivitiesAdapter.HeaderView view, int sectionId) {
        adapter.toggleSection(sectionId);
      }

      @Override
      public void onItemClick(RecyclerView.ViewHolder view, int sectionId) {

      }

      @Override public void onItemMenuClick(@NonNull RecyclerView.ViewHolder view, int sectionId) {
        final UserActivityView v = (UserActivityView) view;

        final PopupMenu menu =
            new PopupMenu(v.overflowMenu.getContext(), v.overflowMenu, Gravity.BOTTOM);

        if (sectionId == R.string.user_activity_section_defaults) {
          menu.getMenu().add(0, R.id.action_make_favorite, 1, R.string.action_add_to_favorite);
        } else if (sectionId == R.string.user_activity_section_favorite) {
          menu.getMenu().add(0, R.id.action_delete, 1, R.string.contextMenuDelete);
        } else if (sectionId == R.string.user_activity_section_my) {
          menu.getMenu().add(0, R.id.action_edit, 1, R.string.contextMenuEdit);
          menu.getMenu().add(0, R.id.action_delete, 1, R.string.contextMenuDelete);
        }

        menu.setOnMenuItemClickListener(item -> {
          final UserActivityExercise target = adapter.getItem(v.getAdapterPosition());

          switch (item.getItemId()) {
            case R.id.action_make_favorite:
              adapter.addItem(R.string.user_activity_section_favorite, target);
              break;

            case R.id.action_delete:
              adapter.removeItem(sectionId, v.getAdapterPosition());
              break;
          }
          return true;
        });
        menu.show();
      }
    });

    sources.put(R.string.user_activity_section_defaults,
        new AssetsSource(getResources().getAssets()));

    adapter.createSection(R.string.user_activity_section_my);
    adapter.createSection(R.string.user_activity_section_favorite);
    adapter.createSection(R.string.user_activity_section_defaults);

    fetchSources();
  }

  private void search(CharSequence q) {
    disposables.clear();

    final List<Single<List<UserActivityExercise>>> streams = new ArrayList<>();

    for (int i = 0; i < sources.size(); i++) {
      final int sourceId = sources.keyAt(i);

      streams.add(
          sources.valueAt(i).search(q)
              .subscribeOn(Schedulers.io())
              .onErrorReturn(error -> {
                error.printStackTrace();

                // default for now
                return Collections.emptyList();
              })
              .observeOn(AndroidSchedulers.mainThread())
              .doOnSuccess(
                  exercises -> {
                    final DiffUtil.DiffResult diff = ExercisesSource.calculateDiff(
                        adapter.getItemsBySection(sourceId), exercises);

                    if (adapter.isExpanded(sourceId)) {
                      adapter.addItems(sourceId, exercises, diff);
                    } else {
                      adapter.clearSection(sourceId);
                      adapter.addItems(sourceId, exercises);
                    }
                  }
              )
      );
    }

    disposables.add(Single.concat(streams).subscribe());
  }

  private void fetchSources() {
    disposables.clear();

    final List<Single<List<UserActivityExercise>>> streams = new ArrayList<>();

    for (int i = 0; i < sources.size(); i++) {
      final int sourceId = sources.keyAt(i);

      streams.add(
          sources.valueAt(i).getExercises()
              .subscribeOn(Schedulers.io())
              .onErrorReturn(error -> {
                error.printStackTrace();

                // default for now
                return Collections.emptyList();
              })
              .observeOn(AndroidSchedulers.mainThread())
              .doOnSuccess(
                  exercises -> {
                    adapter.clearSection(sourceId);
                    adapter.addItems(sourceId, exercises);
                  }
              )
      );
    }

    disposables.add(Single.concat(streams).subscribe());
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    disposables.clear();
  }

  @Override public boolean onMenuItemClick(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_open_menu:
        final PopupMenu menu = new PopupMenu(requireContext(),
            toolbar.findViewById(R.id.action_open_menu));

        menu.inflate(R.menu.fragment_user_activity_menu);
        menu.setOnMenuItemClickListener(this);
        menu.show();
        break;

      case R.id.action_add_user_activity:
        int[] sections = new int[] {
            R.string.user_activity_section_my,
            R.string.user_activity_section_favorite,
        };

        final int targetSection = RandomTool.getAnythingRandomItDoesntMatter(sections);
        adapter.addItem(targetSection, new UserActivityExercise("Fuck you Sher", 1, 1));

        break;

      default:
        return false;
    }

    return true;
  }

  static class DividerDecoration extends RecyclerView.ItemDecoration {
    final int dividerHeight;
    final Paint p = new Paint();

    public DividerDecoration(Context context) {
      dividerHeight = Metrics.dp(context, 1f);
      p.setColor(0xFFD3D3D3);
      p.setStrokeWidth(dividerHeight);
      p.setStyle(Paint.Style.STROKE);
    }

    @Override public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent,
        @NonNull RecyclerView.State state) {
      super.onDrawOver(c, parent, state);

      for (int i = 0; i < parent.getChildCount(); i++) {
        final View view = parent.getChildAt(i);
        final RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);

        if (holder.getItemViewType() != VIEW_TYPE_ACTIVITY) {
          continue;
        }

        c.drawLine(parent.getLeft(), view.getBottom(), parent.getRight(), view.getBottom(), p);
      }
    }

    @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
        @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
      super.getItemOffsets(outRect, view, parent, state);
      outRect.set(0, 0, 0, dividerHeight);
    }
  }
}