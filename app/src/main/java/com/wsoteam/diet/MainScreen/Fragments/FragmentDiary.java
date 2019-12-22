package com.wsoteam.diet.MainScreen.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.MainScreen.Controller.UpdateCallback;
import com.wsoteam.diet.MainScreen.Dialogs.SublimePickerDialogFragment;
import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.presentation.plans.detail.day.CurrentDayPlanFragment;
import com.wsoteam.diet.presentation.search.ParentActivity;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

public class FragmentDiary extends Fragment
    implements SublimePickerDialogFragment.OnDateChoosedListener {
  private final String TAG_COUNT_OF_RUN_FOR_ALERT_DIALOG = "COUNT_OF_RUN";
  @BindView(R.id.pbProt) ProgressBar pbProgressProt;
  @BindView(R.id.pbCarbo) ProgressBar pbProgressCarbo;
  @BindView(R.id.pbFat) ProgressBar pbProgressFat;
  @BindView(R.id.pbCalories) ProgressBar pbProgressCalories;
  @BindView(R.id.tvCarbo) TextView tvCarbo;
  @BindView(R.id.tvFat) TextView tvFat;
  @BindView(R.id.tvProt) TextView tvProt;
  @BindView(R.id.tvCaloriesNeed) TextView tvCaloriesNeed;
  @BindView(R.id.textView138) TextView tvDaysAtRow;
  @BindView(R.id.mainappbar) AppBarLayout mainappbar;
  @BindView(R.id.cvParams) CardView cvParams;
  @BindView(R.id.collapsingToolbarLayout) CollapsingToolbarLayout collapsingToolbarLayout;
  @BindView(R.id.vpEatingTimeLine) ViewPager vpEatingTimeLine;
  @BindView(R.id.llSum) LinearLayout llSum;
  @BindView(R.id.btnNotification) ImageView btnNotification;
  @BindView(R.id.flCurrentDayPlan) FrameLayout containerCurrentDayPlan;
  private Unbinder unbinder;
  private int dayPosition = Config.COUNT_PAGE;
  private SharedPreferences countOfRun;
  private AlertDialog alertDialogBuyInfo;
  private LinearLayout.LayoutParams layoutParams;
  private FragmentTransaction transaction;
  private BottomNavigationView bnvMain;

  private CurrentDayPlanFragment currentDayPlanFragment;
  UpdateCallback updateCallback = new UpdateCallback() {
    @Override public void update() {
      if (currentDayPlanFragment != null) {
        currentDayPlanFragment.onResume();
      }
    }
  };

  private UpdateCallback currentDayPlanCallback = new UpdateCallback() {
    @Override public void update() {
      Log.d("kkk", "update: ");
      vpEatingTimeLine.getAdapter().notifyDataSetChanged();
    }
  };
  private ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
      if (dayPosition < i) {

      } else if (dayPosition > i) {

      }

      dayPosition = i;
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }
  };

  public FragmentDiary() {
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View mainView = inflater.inflate(R.layout.activity_main_new, container, false);
    unbinder = ButterKnife.bind(this, mainView);
    transaction = getActivity().getSupportFragmentManager().beginTransaction();
    getActivity().setTitle("");
    /** on your logout method:**/
    Intent broadcastIntent = new Intent();
    broadcastIntent.setAction("com.wsoteam.diet.ACTION_LOGOUT");
    getActivity().sendBroadcast(broadcastIntent);

    currentDayPlanFragment = new CurrentDayPlanFragment();
    currentDayPlanFragment.setUpdateCallback(currentDayPlanCallback);

    transaction = getChildFragmentManager().beginTransaction();
    transaction.add(R.id.flCurrentDayPlan, currentDayPlanFragment).commit();

    bnvMain = getActivity().findViewById(R.id.bnv_main);

    WorkWithFirebaseDB.setFirebaseStateListener();

    boolean isPremAlert =
        getActivity().getSharedPreferences(Config.ALERT_BUY_SUBSCRIPTION, MODE_PRIVATE)
            .getBoolean(Config.ALERT_BUY_SUBSCRIPTION, false);

    if (isPremAlert) {
      View view = getLayoutInflater().inflate(R.layout.alert_dialog_buy_sub_info, null);
      Button button = view.findViewById(R.id.alerd_buy_info_btn);
      button.setOnClickListener(view1 -> {
        if (alertDialogBuyInfo != null) {
          alertDialogBuyInfo.dismiss();
        }
      });

      alertDialogBuyInfo = new AlertDialog.Builder(getActivity())
          .setView(view).create();

      alertDialogBuyInfo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
      alertDialogBuyInfo.show();

      SharedPreferences sharedPreferences =
          getActivity().getSharedPreferences(Config.ALERT_BUY_SUBSCRIPTION, MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putBoolean(Config.ALERT_BUY_SUBSCRIPTION, false);
      editor.apply();
    }

    bindViewPager();


    return mainView;
  }

  private void bindViewPager() {
    vpEatingTimeLine.addOnPageChangeListener(viewPagerListener);
    vpEatingTimeLine.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        FragmentEatingScroll fragmentEatingScroll = FragmentEatingScroll.newInstance(position).setUpdateCallback(updateCallback);
        return fragmentEatingScroll;
      }

      @Override
      public int getCount() {
        return Config.COUNT_PAGE + 1;
      }

      @Override public int getItemPosition(@NonNull Object object) {

        FragmentEatingScroll fragment = (FragmentEatingScroll) object;

        if (fragment != null){
          fragment.update();
        }

        return super.getItemPosition(object);
        //return POSITION_NONE;
      }
    });
  }

  @Override
  public void dateChoosed(Calendar calendar, int dayOfMonth, int month, int year) {

  }


  private void attachCaloriesPopup() {
    View popupView = getLayoutInflater().inflate(R.layout.layout_notification_calories_over, null);
    final PopupWindow popupWindow = new PopupWindow(popupView,
        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
    popupWindow.setTouchable(true);
    popupWindow.setFocusable(true);
    popupWindow.setContentView(popupView);
    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    popupWindow.showAsDropDown(tvCaloriesNeed, tvCaloriesNeed.getWidth() / 2, 0, Gravity.BOTTOM);
  }

  private void setMaxParamsInProgressBars(Profile profile) {
    pbProgressCalories.setMax(profile.getMaxKcal());
    pbProgressProt.setMax(profile.getMaxProt());
    pbProgressCarbo.setMax(profile.getMaxCarbo());
    pbProgressFat.setMax(profile.getMaxFat());
  }

  private void additionOneToSharedPreference() {
    countOfRun = getActivity().getPreferences(MODE_PRIVATE);
    SharedPreferences.Editor editor = countOfRun.edit();
    int COUNT_OF_RUN = 0;
    editor.putInt(TAG_COUNT_OF_RUN_FOR_ALERT_DIALOG,
        countOfRun.getInt(TAG_COUNT_OF_RUN_FOR_ALERT_DIALOG,
            COUNT_OF_RUN) + 1);
    editor.apply();
  }

  private int getDaysAtRow() {
    countOfRun = getActivity().getPreferences(MODE_PRIVATE);
    return countOfRun.getInt(TAG_COUNT_OF_RUN_FOR_ALERT_DIALOG, 0);
  }

  @OnClick({ R.id.fabAddEating, R.id.btnNotification, R.id.tvReports })
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.fabAddEating:
                /*AlertDialogChoiceEating.createChoiceEatingAlertDialog(getActivity(),
                        tvDateForMainScreen.getText().toString()).show();*/
        startActivity(new Intent(getActivity(), ParentActivity.class));
        break;
      case R.id.btnNotification:
        attachCaloriesPopup();
        break;
      case R.id.tvReports:
        Toast.makeText(getActivity(), "Раздел в разработке", Toast.LENGTH_SHORT).show();
        openProfile();
        break;
    }
  }

  private void openProfile() {
    //window = getActivity().getWindow();
    //window.setStatusBarColor(Color.parseColor("#2E4E4E"));
    //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    //Amplitude.getInstance().logEvent(Events.VIEW_PROFILE);
    //Intercom.client().logEvent(Events.VIEW_PROFILE);
    //transaction.replace(R.id.flFragmentContainer, new ProfileFragment()).commit();
    //bnvMain.setSelectedItemId(R.id.bnv_main_profile);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override
  public void onPause() {
    super.onPause();
    //IntercomFactory.hide();
  }

  @Override
  public void onResume() {
    super.onResume();
    //IntercomFactory.show();
    if (UserDataHolder.getUserData() != null && UserDataHolder.getUserData().getProfile() != null) {
      Profile profile = UserDataHolder.getUserData().getProfile();
      setMaxParamsInProgressBars(profile);
      tvDaysAtRow.setText(getActivity().getResources()
          .getQuantityString(R.plurals.daysAtRow, getDaysAtRow(), getDaysAtRow()));
    }


    if (currentDayPlanFragment != null) {
      currentDayPlanFragment.onResume();
    }
  }
}
