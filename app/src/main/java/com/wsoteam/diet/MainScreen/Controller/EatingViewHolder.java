package com.wsoteam.diet.MainScreen.Controller;

import android.content.Context;
import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wsoteam.diet.BranchOfAnalyzer.ActivityListAndSearch;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.model.Eating;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EatingViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvTitleOfEatingCard)
    TextView tvTitleOfEatingCard;
    @BindView(R.id.rvListOfFoodEatingCard)
    RecyclerView rvListOfFoodEatingCard;
    @BindView(R.id.ibtnOpenList)
    ImageButton ibtnOpenList;
    @BindView(R.id.tvSumOfKcal)
    TextView tvSumOfKcal;
    @BindView(R.id.tvSumProt)
    TextView tvSumProt;
    @BindView(R.id.tvSumFats)
    TextView tvSumFats;
    @BindView(R.id.tvSumCarbo)
    TextView tvSumCarbo;
    @BindView(R.id.ivEatingIcon)
    ImageView ivEatingIcon;
    @BindView(R.id.tvRecommendation)
    TextView tvRecommendation;
    @BindView(R.id.tvEatingLabelProt)
    TextView tvEatingLabelProt;
    @BindView(R.id.tvEatingLabelFats)
    TextView tvEatingLabelFats;
    @BindView(R.id.tvEatingLabelCarbo)
    TextView tvEatingLabelCarbo;
    @BindView(R.id.tvEatingLabelKcal)
    TextView tvEatingLabelKcal;
    @BindView(R.id.tvCount)
    TextView tvCount;
    @BindView(R.id.ibtnOpenMenu)
    ImageButton ibtnOpenMenu;
    @BindView(R.id.clReminderBack)
    ConstraintLayout clReminderBack;
    @BindView(R.id.layoutCommonInfo)
    ConstraintLayout layoutCommonInfo;
    private boolean isButtonPressed = true;
    private final int BREAKFAST = 0, LUNCH = 1, DINNER = 2, SNACK = 3;
    private final int BREAKFAST_TIME = 9, LUNCH_TIME = 13, DINNER_TIME = 18, SNACK_TIME = 24;

    private Context context;
    private String data;
    private List<Eating> eatingGroup;
    private int endTime;

    private UpdateCallback updateCallback;


    public EatingViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup, Context context, String data, UpdateCallback updateCallback) {
        super(layoutInflater.inflate(R.layout.ms_item_eating_list, viewGroup, false));
        ButterKnife.bind(this, itemView);
        this.context = context;
        this.data = data;
        this.updateCallback = updateCallback;
    }

    public void bind(List<Eating> eatingGroup, Context context, String nameOfEatingGroup) {
        this.eatingGroup = eatingGroup;
        setCPFC();
        setIconAndTime();
        tvTitleOfEatingCard.setText(nameOfEatingGroup);
        rvListOfFoodEatingCard.setLayoutManager(new LinearLayoutManager(context));
        rvListOfFoodEatingCard.setAdapter(new InsideAdapter(eatingGroup,
                context, true, getAdapterPosition(), this::refreshUI).setUpdateCallback(updateCallback));
        setExpandableView();
//        if (isNeedRemind()) remind(nameOfEatingGroup);
    }

    private void remind(String eatingGroup) {
        ArrayList<String> phrases = new ArrayList<>();
        phrases.add("Вы сегодня " + eatingGroup.toLowerCase() + "али?\n Добавьте продукты, которые вы ели");
        phrases.add("Занесите " + eatingGroup.toLowerCase() + " сейчас\n и достигните своей цели быстрее");
        Random random = new Random();
        tvRecommendation.setText(phrases.get(random.nextInt(2)));
        tvRecommendation.setVisibility(View.VISIBLE);
        tvRecommendation.setGravity(Gravity.CENTER);
        clReminderBack.setBackgroundColor(context.getResources().getColor(R.color.reminder_back_color));
    }

    private boolean isNeedRemind() {
        int day = Integer.parseInt(data.split("\\.")[0]);
        int month = Integer.parseInt(data.split("\\.")[1]) - 1;
        int year = Integer.parseInt(data.split("\\.")[2]);

        if (day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) &&
                month == Calendar.getInstance().get(Calendar.MONTH) &&
                year == Calendar.getInstance().get(Calendar.YEAR) &&
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > endTime &&
                eatingGroup.size() < 1) {
            Log.e("LOL", "KEK");
            return true;
        } else {
            return false;
        }
    }


    private void setExpandableView() {
        if (eatingGroup.size() < 1) {
            ibtnOpenList.setVisibility(View.GONE);
        } else {
            ibtnOpenList.setOnClickListener(view -> {
                if (!isButtonPressed) {
                    Glide.with(context).load(R.drawable.close_eating_list).into(ibtnOpenList);
                } else {
                    Glide.with(context).load(R.drawable.open_eating_list).into(ibtnOpenList);
                }
                rvListOfFoodEatingCard.setAdapter(new InsideAdapter(eatingGroup,
                        context, !isButtonPressed, getAdapterPosition(), this::refreshUI).setUpdateCallback(updateCallback));
                isButtonPressed = !isButtonPressed;
            });
        }
    }

    private void setIconAndTime() {
        switch (getAdapterPosition()) {
            case BREAKFAST:
                Glide.with(context).load(R.drawable.breakfast_icon).into(ivEatingIcon);
                endTime = BREAKFAST_TIME;
                break;
            case LUNCH:
                Glide.with(context).load(R.drawable.lunch_icon).into(ivEatingIcon);
                endTime = LUNCH_TIME;
                break;
            case DINNER:
                Glide.with(context).load(R.drawable.dinner_icon).into(ivEatingIcon);
                endTime = DINNER_TIME;
                break;
            case SNACK:
                Glide.with(context).load(R.drawable.snack_icon).into(ivEatingIcon);
                endTime = SNACK_TIME;
                break;
        }
    }

    private void setCPFC() {
        int sumKcal = 0, sumProt = 0, sumFats = 0, sumCarbo = 0;

        tvCount.setText(String.valueOf(eatingGroup.size()) + " шт.");

        if (eatingGroup.size() > 0) {
            for (int i = 0; i < eatingGroup.size(); i++) {
                sumKcal += eatingGroup.get(i).getCalories();
                sumProt += eatingGroup.get(i).getProtein();
                sumFats += eatingGroup.get(i).getFat();
                sumCarbo += eatingGroup.get(i).getCarbohydrates();
            }
            //layoutCommonInfo.setVisibility(View.VISIBLE);
            tvSumOfKcal.setText(String.valueOf(sumKcal));

            tvSumProt.setText(String.valueOf(sumProt) + " г");
            tvSumFats.setText(String.valueOf(sumFats) + " г");
            tvSumCarbo.setText(String.valueOf(sumCarbo) + " г");
        } else {
            //layoutCommonInfo.setVisibility(View.GONE);
            //tvRecommendation.setVisibility(View.VISIBLE);
            tvSumOfKcal.setVisibility(View.GONE);
            tvSumProt.setVisibility(View.GONE);
            tvSumFats.setVisibility(View.GONE);
            tvSumCarbo.setVisibility(View.GONE);

            tvEatingLabelProt.setVisibility(View.GONE);
            tvEatingLabelFats.setVisibility(View.GONE);
            tvEatingLabelCarbo.setVisibility(View.GONE);
            tvEatingLabelKcal.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.ibtnOpenMenu, R.id.imbAddFood})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ibtnOpenMenu:
                createPopupMenu(context, ibtnOpenMenu);
                break;
            case R.id.imbAddFood:
                openSearch();
                Events.logDiaryNext(getAdapterPosition());
                break;
        }
    }

    private void openSearch() {
        Intent intent = new Intent(context, ActivityListAndSearch.class);
        intent.putExtra(Config.INTENT_DATE_FOR_SAVE, data);
        intent.putExtra(Config.TAG_CHOISE_EATING, getAdapterPosition());
        context.startActivity(intent);
    }

    private void createPopupMenu(Context context, ImageButton ibtnOpenMenu) {
        PopupMenu popupMenu = new PopupMenu(context, ibtnOpenMenu);
        popupMenu.inflate(R.menu.dots_popup_menu);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.add_food_dots:
                    openSearch();
                    Events.logDiaryNext(getAdapterPosition());
                    break;
            }
            return false;
        });
    }

    private void refreshUI(int position) {
        setCPFC();
        if (eatingGroup.size() < 1) {
            ibtnOpenList.setVisibility(View.GONE);
        }
    }
}
