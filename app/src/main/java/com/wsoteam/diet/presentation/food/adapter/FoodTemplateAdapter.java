package com.wsoteam.diet.presentation.food.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.EventLog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.wsoteam.diet.BranchOfAnalyzer.POJOFoodSQL.Food;
import com.wsoteam.diet.BranchOfAnalyzer.templates.POJO.FoodTemplate;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.presentation.food.helper.NounsDeclension;
import com.wsoteam.diet.presentation.food.template.browse.BrowseFoodTemplatePresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FoodTemplateAdapter extends RecyclerView.Adapter<FoodTemplateAdapter.ViewHolder> {

    private Context context;
    private List<FoodTemplate> templateList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private BrowseFoodTemplatePresenter presenter;

    public FoodTemplateAdapter(Context context, BrowseFoodTemplatePresenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    public void setListContent(List<FoodTemplate> templateList) {
        if (templateList != null) {
            this.templateList = templateList;
        } else {
            this.templateList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_food_template, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return templateList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        @BindView(R.id.ivTemplate) ImageView imageView;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvCountFoods) TextView tvCountFoods;
        @BindView(R.id.colapseFoods) ToggleButton tbShowFoods;
        @BindView(R.id.llFoodsContainer) LinearLayout linearLayout;
        @BindView(R.id.ivTogle) ImageView ivTogle;
        @BindView(R.id.tvPlansEating) TextView tvEating;

        final int menuEditID = 66;
        final int menuDeleteID = 321;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tbShowFoods.setOnCheckedChangeListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @OnClick({R.id.ivAddTemplate})
        public void onViewClicked(View view) {
            presenter.addToDiary(templateList.get(getAdapterPosition()));
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            menu.setHeaderTitle("Select The Action");
            menu.add(0, menuEditID, 0, R.string.contextMenuEdit)
                    .setOnMenuItemClickListener(this);//groupId, itemId, order, title
            menu.add(0, menuDeleteID, 0, R.string.contextMenuDelete)
                    .setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case menuEditID:
                    presenter.editTemplate(templateList.get(getAdapterPosition()));
                    return true;
                case menuDeleteID:
                    WorkWithFirebaseDB.deleteFoodTemplate(templateList.get(getAdapterPosition()).getKey());
                    templateList.remove(templateList.get(getAdapterPosition()));
                    notifyDataSetChanged();
                    if (templateList.size() == 0) {
                        presenter.getViewState().showBtn();
                    }
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (templateList.get(getAdapterPosition()).isShowFoods() != isChecked) {
                WorkWithFirebaseDB.setVisibilityFoodTemplate(templateList.get(getAdapterPosition()).getKey(), isChecked);
                templateList.get(getAdapterPosition()).setShowFoods(isChecked);
            }

            if (isChecked) {
                linearLayout.setVisibility(View.VISIBLE);
                ivTogle.setImageResource(R.drawable.ic_slide_switch_on);
            } else {
                linearLayout.setVisibility(View.GONE);
                ivTogle.setImageResource(R.drawable.ic_slide_switch_off);
            }
        }

        void bind(int position) {
            imageView.setImageResource(getImgID(templateList.get(position).getEating()));
            tvName.setText(templateList.get(position).getName());
            tvEating.setText(templateList.get(position).getEating().toLowerCase());
            linearLayout.removeAllViews();
            int numbers = templateList.get(position).getFoodList().size();
            tvCountFoods.setText(numbers
                    + NounsDeclension.check(numbers, " продукт", " продукта", " продуктов"));

            for (Food food :
                    templateList.get(position).getFoodList()) {
                View view = layoutInflater.inflate(R.layout.item_food_for_template, linearLayout, false);
                TextView foodName = view.findViewById(R.id.tvNameTemplate);
                TextView foodCalories = view.findViewById(R.id.tvCalories);
                foodName.setText(food.getName());
                foodCalories.setText(String.valueOf((int) (food.getCalories() * food.getPortion())));

                linearLayout.addView(view);
            }

            tbShowFoods.setChecked(templateList.get(position).isShowFoods());
        }

        int getImgID(String str) {
            switch (str) {
                case "Завтрак":
                    return R.drawable.ic_breakfast_template;
                case "Обед":
                    return R.drawable.ic_lunch_template;
                case "Перекус":
                    return R.drawable.ic_snack_template;
                case "Ужин":
                    return R.drawable.ic_dinner_template;

                default:
                    return R.drawable.ic_snack_template_2;
            }
        }

    }
}
