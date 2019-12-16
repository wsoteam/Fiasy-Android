package com.wsoteam.diet.presentation.search.sections.custom;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wsoteam.diet.BranchOfAnalyzer.Const;
import com.wsoteam.diet.BranchOfAnalyzer.Controller.CustomFoodViewPagerAdapter;
import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.CustomFood;
import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.CustomViewPager;

import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.presentation.search.sections.custom.fragments.FragmentBonusOutlay;
import com.wsoteam.diet.presentation.search.sections.custom.fragments.FragmentMain;
import com.wsoteam.diet.presentation.search.sections.custom.fragments.FragmentOutlay;
import com.wsoteam.diet.presentation.search.sections.custom.fragments.FragmentResult;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityCreateFood extends AppCompatActivity {

    public CustomFood customFood;
    public boolean isPublicFood = false;
    @BindView(R.id.vpFragmentContainer)
    CustomViewPager vpFragmentContainer;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.btnForward)
    Button btnForward;
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    private CustomFoodViewPagerAdapter vpAdapter;
    private final int FRAGMENT_RESULT = 2, FRAGMENT_OUTLAY = 1, FRAGMENT_MAIN = 0;
    private final int COUNT_GRAMM = 100;
    private final double EMPTY_PARAM = -1.0;
    public boolean isEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_food_new);
        ButterKnife.bind(this);
        if (getIntent().getSerializableExtra(Const.EDIT_CUSTOM_FOOD) != null) {
            customFood = (CustomFood) getIntent().getSerializableExtra(Const.EDIT_CUSTOM_FOOD);
            isEdit = true;
        } else {
            customFood = new CustomFood();
        }
        btnBack.setVisibility(View.GONE);
        vpFragmentContainer.disableScroll(true);
        updateUI();
    }

    private void updateUI() {
        vpAdapter = new CustomFoodViewPagerAdapter(getSupportFragmentManager(), createFragmentList());
        vpFragmentContainer.setAdapter(vpAdapter);
        vpFragmentContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                updateUIAfterScrolled(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void updateUIAfterScrolled(int i) {
        if (isEdit) {
            tvTitle.setText(getResources().getStringArray(R.array.fragment_names)[i]);
        } else {

        }
        if (i == FRAGMENT_RESULT) {
            btnForward.setText(getString(R.string.ok));
        }
        if (i == FRAGMENT_OUTLAY && btnForward.getText().toString().equals(getString(R.string.ok))) {
            btnForward.setText(getString(R.string.next2));
        }
        if (i == FRAGMENT_MAIN && btnBack.getVisibility() == View.VISIBLE) {
            btnBack.setVisibility(View.GONE);
        } else {
            btnBack.setVisibility(View.VISIBLE);
        }
    }


    private List<Fragment> createFragmentList() {
        List<Fragment> fragments = new ArrayList<>();
        if (isEdit) {
            fragments.add(FragmentMain.newInstance(customFood));
            fragments.add(FragmentOutlay.newInstance(customFood));
            fragments.add(FragmentBonusOutlay.newInstance(customFood));
            fragments.add(new FragmentResult());
        } else {
            fragments.add(new FragmentMain());
            fragments.add(new FragmentOutlay());
            fragments.add(new FragmentBonusOutlay());
            fragments.add(new FragmentResult());
        }
        return fragments;
    }

    private boolean isCanMoveForward() {
        return ((SayForward) vpAdapter.getItem(vpFragmentContainer.getCurrentItem())).forward();
    }

    @OnClick({R.id.ibClose, R.id.btnBack, R.id.btnForward})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ibClose:
                finish();
                break;
            case R.id.btnBack:
                back();
                break;
            case R.id.btnForward:
                if (isCanMoveForward() && vpFragmentContainer.getCurrentItem() < vpAdapter.getCount() - 1) {
                    vpFragmentContainer.setCurrentItem(vpFragmentContainer.getCurrentItem() + 1);
                    Log.e("LOL", customFood.toString());
                } else if (vpFragmentContainer.getCurrentItem() == vpAdapter.getCount() - 1) {
                    saveFood();
                    if (isEdit) {
                        Toast.makeText(this, getString(R.string.edit_food_completed), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, getString(R.string.food_saved), Toast.LENGTH_LONG).show();
                    }
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        if (vpFragmentContainer.getCurrentItem() > 0) {
            vpFragmentContainer.setCurrentItem(vpFragmentContainer.getCurrentItem() - 1);
        } else {
            finish();
        }
    }

    private void saveFood() {
        Events.logCreateCustomFood(getIntent().getStringExtra(EventProperties.product_from), customFood.getName());
        customFood.setCalories(customFood.getCalories() / COUNT_GRAMM);
        customFood.setFats(customFood.getFats() / COUNT_GRAMM);
        customFood.setCarbohydrates(customFood.getCarbohydrates() / COUNT_GRAMM);
        customFood.setProteins(customFood.getProteins() / COUNT_GRAMM);

        if (customFood.getCellulose() != EMPTY_PARAM) {
            customFood.setCellulose(customFood.getCellulose() / COUNT_GRAMM);
        }

        if (customFood.getSugar() != EMPTY_PARAM) {
            customFood.setSugar(customFood.getSugar() / COUNT_GRAMM);
        }

        if (customFood.getCholesterol() != EMPTY_PARAM) {
            customFood.setCholesterol(customFood.getCholesterol() / COUNT_GRAMM);
        }

        if (customFood.getSodium() != EMPTY_PARAM) {
            customFood.setSodium(customFood.getSodium() / COUNT_GRAMM);
        }

        if (customFood.getPottassium() != EMPTY_PARAM) {
            customFood.setPottassium(customFood.getPottassium() / COUNT_GRAMM);
        }

        if (customFood.getSaturatedFats() != EMPTY_PARAM) {
            customFood.setSaturatedFats(customFood.getSaturatedFats() / COUNT_GRAMM);
        }

        if (customFood.getMonoUnSaturatedFats() != EMPTY_PARAM) {
            customFood.setMonoUnSaturatedFats(customFood.getMonoUnSaturatedFats() / COUNT_GRAMM);
        }

        if (customFood.getPolyUnSaturatedFats() != EMPTY_PARAM) {
            customFood.setPolyUnSaturatedFats(customFood.getPolyUnSaturatedFats() / COUNT_GRAMM);
        }
        if (isEdit) {
            WorkWithFirebaseDB.rewriteCustomFood(customFood);
        } else {
            WorkWithFirebaseDB.addCustomFood(customFood);
        }
        if (isPublicFood) {
            WorkWithFirebaseDB.shareCustomFood(customFood);
        }
    }
}

