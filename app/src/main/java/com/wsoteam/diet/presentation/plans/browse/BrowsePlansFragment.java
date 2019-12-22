package com.wsoteam.diet.presentation.plans.browse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.appbar.AppBarLayout;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.DietPlans.POJO.DietPlan;
import com.wsoteam.diet.DietPlans.POJO.DietPlansHolder;
import com.wsoteam.diet.DietPlans.POJO.DietsList;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.presentation.plans.adapter.HorizontalBrowsePlansAdapter;
import com.wsoteam.diet.presentation.plans.adapter.VerticalBrowsePlansAdapter;
import com.wsoteam.diet.presentation.plans.detail.DetailPlansActivity;
import com.wsoteam.diet.presentation.plans.detail.blocked.BlockedDetailPlansActivity;
import com.wsoteam.diet.utils.Subscription;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static android.content.Context.MODE_PRIVATE;

public class BrowsePlansFragment extends MvpAppCompatFragment implements BrowsePlansView, Observer {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler) RecyclerView recyclerView;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @InjectPresenter
    BrowsePlansPresenter presenter;

    VerticalBrowsePlansAdapter adapter;

    public static String currentPlanProperti = "CURRENT_PLAN";

    @ProvidePresenter
    BrowsePlansPresenter providePresenter() {
        return new BrowsePlansPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_plans,
                container, false);
        ButterKnife.bind(this, view);
        appBarLayout.setLiftable(true);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.highlight_line_color));
        mToolbar.setTitle(getString(R.string.plans));

        if (getArguments() != null && getArguments().getBoolean("addBackButton")) {
            mToolbar.setNavigationIcon(R.drawable.arrow_back_gray);
            mToolbar.setNavigationOnClickListener(navigationListener);
        }

        if (DietPlansHolder.get() == null) DietPlansHolder.subscribe(this);

        adapter = new VerticalBrowsePlansAdapter(prepareList());
        adapter.SetOnItemClickListener(onItemClickListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItem = 0;
                if (linearLayoutManager1 != null)
                    firstVisibleItem = linearLayoutManager1.findFirstVisibleItemPosition();
                appBarLayout.setLiftable(firstVisibleItem == 0);
            }
        });

        return view;
    }

    private List<DietsList> prepareList(){

        List<DietsList> listGroups;
        try {
            listGroups = DietPlansHolder.get().getListGroups();
            if (listGroups.get(0).getProperties().equals(currentPlanProperti)) {
                listGroups.remove(0);
            }
        } catch (NullPointerException e){
            Log.d("TAG", "com.wsoteam.diet.presentation.plans.browse.BrowsePlansFragment \n" +
                    "private List<DietsList> prepareList()", e);
            listGroups = new ArrayList<>();
        }


        if (UserDataHolder.getUserData().getPlan() != null && UserDataHolder.getUserData().getPlan().getDaysAfterStart() <
                UserDataHolder.getUserData().getPlan().getCountDays()) {
            DietsList dietsList = new DietsList();
            dietsList.setName(getString(R.string.my_plan));
            dietsList.setProperties(currentPlanProperti);
            List<DietPlan> plan = new LinkedList<>();
            plan.add(UserDataHolder.getUserData().getPlan());

            dietsList.setDietPlans(plan);

            listGroups.add(0, dietsList);
        }
        return listGroups;
    }

    private View.OnClickListener navigationListener = v -> getActivity().onBackPressed();

    HorizontalBrowsePlansAdapter.OnItemClickListener onItemClickListener = new HorizontalBrowsePlansAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position, DietPlan dietPlan) {
            Intent intent;

            if (!Subscription.check(getContext()) && dietPlan.isPremium()){
                intent = new Intent(getContext(), BlockedDetailPlansActivity.class);
            }else {
                intent = new Intent(getContext(), DetailPlansActivity.class);
            }

            intent.putExtra(Config.DIETS_PLAN_INTENT, dietPlan);
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(View view, int position, DietPlan dietPlan) {
            //Intent intent = new Intent(getContext(), DetailPlansActivity.class);
            //intent.putExtra(Config.DIETS_PLAN_INTENT, dietPlan);
            //startActivity(intent);
        }
    };

    @Override
    public void showProgress(boolean show) {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override public void onResume() {
        super.onResume();
        adapter.updateList(prepareList());
    }

    @Override
    public void update(Observable o, Object arg) {
        adapter.updateList(prepareList());
        DietPlansHolder.unsubscribe(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        DietPlansHolder.unsubscribe(this);
    }
}
