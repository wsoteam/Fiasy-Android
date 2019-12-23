package com.wsoteam.diet.presentation.search.sections.custom;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.wsoteam.diet.BranchOfAnalyzer.Controller.CustomFoodViewPagerAdapter;
import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.CustomViewPager;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.common.networking.food.POJO.Result;
import com.wsoteam.diet.presentation.search.sections.custom.fragments.FragmentMain;
import com.wsoteam.diet.presentation.search.sections.custom.fragments.FragmentOutlay;
import com.wsoteam.diet.presentation.search.sections.custom.fragments.FragmentPortions;
import com.wsoteam.diet.presentation.search.sections.custom.fragments.FragmentResult;
import java.util.ArrayList;
import java.util.List;

public class ActivityCreateFood extends AppCompatActivity {

  public Result customFood;
  public boolean isPublicFood = false;
  @BindView(R.id.vpFragmentContainer)
  CustomViewPager vpFragmentContainer;
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.btnForward) Button btnForward;
  @BindView(R.id.btnBack) ImageButton btnBack;
  @BindView(R.id.ibClose) ImageButton ibClose;
  private CustomFoodViewPagerAdapter vpAdapter;
  private final int FRAGMENT_RESULT = 3, FRAGMENT_OUTLAY = 2, FRAGMENT_PORTIONS = 1, FRAGMENT_MAIN =
      0;
  private final int COUNT_GRAMM = 100;
  private final double EMPTY_PARAM = -1.0;
  public boolean isEdit = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_food_new);
    ButterKnife.bind(this);
    customFood = new Result();
    fillNewProduct();
    btnBack.setVisibility(View.GONE);
    vpFragmentContainer.disableScroll(true);
    updateUI();
  }

  private void fillNewProduct() {
    customFood.setCellulose(EMPTY_PARAM);
    customFood.setCholesterol(EMPTY_PARAM);
    customFood.setId(0);
    customFood.setKilojoules(EMPTY_PARAM);
    customFood.setMonounsaturatedFats(EMPTY_PARAM);
    customFood.setPolyunsaturatedFats(EMPTY_PARAM);
    customFood.setPortion(COUNT_GRAMM);
    customFood.setPottasium(EMPTY_PARAM);
    customFood.setSaturatedFats(EMPTY_PARAM);
    customFood.setSodium(EMPTY_PARAM);
    customFood.setSugar(EMPTY_PARAM);
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
        if (((SayForward) vpAdapter.getItem(i)).checkForwardPossibility()) {
          activatedButtonForward();
        } else {
          deactivatedButtonForward();
        }
      }

      @Override
      public void onPageScrollStateChanged(int i) {

      }
    });
  }

  private void deactivatedButtonForward() {
    btnForward.setBackground(getResources().getDrawable(R.drawable.shape_gray));
  }

  private void activatedButtonForward() {
    btnForward.setBackground(getResources().getDrawable(R.drawable.shape_orange));
  }

  private void updateUIAfterScrolled(int i) {
    if (isEdit) {
      tvTitle.setText(getResources().getStringArray(R.array.fragment_names)[i]);
    } else {

    }
    if (i == FRAGMENT_RESULT) {
      btnForward.setText(getResources().getString(R.string.cst_save));
    }
    if (i == FRAGMENT_OUTLAY && btnForward.getText().toString().equals(getString(R.string.cst_save))) {
      btnForward.setText(getString(R.string.next2));
    }
    if (i == FRAGMENT_MAIN && btnBack.getVisibility() == View.VISIBLE) {
      btnBack.setVisibility(View.GONE);
    } else {
      btnBack.setVisibility(View.VISIBLE);
    }

    switch (i) {
      case FRAGMENT_MAIN:
        tvTitle.setText(getResources().getString(R.string.cst_create_food));
        ibClose.setVisibility(View.VISIBLE);
        break;
      case FRAGMENT_PORTIONS:
        tvTitle.setText(getResources().getString(R.string.cst_change_portion));
        ibClose.setVisibility(View.VISIBLE);
        break;
      case FRAGMENT_OUTLAY:
        tvTitle.setText(
            getResources().getString(R.string.cst_food_price, ((int) customFood.getPortion())));
        ibClose.setVisibility(View.GONE);
        break;
      case FRAGMENT_RESULT:
        tvTitle.setText(getResources().getString(R.string.cst_check));
        ibClose.setVisibility(View.VISIBLE);
        break;
    }
  }

  private List<Fragment> createFragmentList() {
    List<Fragment> fragments = new ArrayList<>();
    if (isEdit) {
      fragments.add(FragmentMain.newInstance(customFood));
      //fragments.add(FragmentOutlay.newInstance(customFood));
      //fragments.add(FragmentBonusOutlay.newInstance(customFood));
      fragments.add(new FragmentResult());
    } else {
      fragments.add(new FragmentMain());
      fragments.add(new FragmentPortions());
      fragments.add(new FragmentOutlay());
      fragments.add(new FragmentResult());
    }
    return fragments;
  }

  private boolean isCanMoveForward() {
    return ((SayForward) vpAdapter.getItem(vpFragmentContainer.getCurrentItem())).forward();
  }

  @OnClick({ R.id.ibClose, R.id.btnBack, R.id.btnForward })
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.ibClose:
        askExit();
        break;
      case R.id.btnBack:
        back();
        break;
      case R.id.btnForward:
        if (isCanMoveForward() && vpFragmentContainer.getCurrentItem() < vpAdapter.getCount() - 1) {
          vpFragmentContainer.setCurrentItem(vpFragmentContainer.getCurrentItem() + 1);
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
      askExit();
    }
  }

  private void saveFood() {
    Events.logCreateCustomFood(getIntent().getStringExtra(EventProperties.product_from),
        customFood.getName());
  }

  private void askExit() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    AlertDialog alertDialog = builder.create();
    LayoutInflater layoutInflater =
        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = layoutInflater.inflate(R.layout.alert_dialog_custom_exit, null);
    TextView tvCancel = view.findViewById(R.id.tvCancel);
    TextView tvExit = view.findViewById(R.id.tvExit);

    tvCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        alertDialog.cancel();
      }
    });
    tvExit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        alertDialog.cancel();
        finish();
      }
    });

    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
    alertDialog.setView(view);
    alertDialog.show();
  }
}

