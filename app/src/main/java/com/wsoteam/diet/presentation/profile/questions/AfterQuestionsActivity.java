package com.wsoteam.diet.presentation.profile.questions;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.Events;

public class AfterQuestionsActivity extends AppCompatActivity {

  @BindView(R.id.pager) CustomViewPager viewPager;
  Profile profile;
  private final int CALCULATE = 0, FEATURE = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_after_questions);
    ButterKnife.bind(this);


    getWindow().getDecorView().setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    getWindow().setStatusBarColor(Color.parseColor("#382c2c2d"));

    //final View touchView = findViewById(R.id.pager);
    //touchView.setOnTouchListener(new View.OnTouchListener() {
    //  @Override
    //  public boolean onTouch(View v, MotionEvent event) {
    //    return true;
    //  }
    //});
    //viewPager.onInterceptTouchEvent(null);
    //viewPager.onTouchEvent(null);
    viewPager.disableScroll(true);
    viewPager.setAdapter(new AfterQuestionsPagerAdapter(getSupportFragmentManager()));
    Events.logMoveQuestions(EventProperties.question_calculate);
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int i, float v, int i1) {
      }

      @Override
      public void onPageSelected(int i) {
        if (i == 1){
          //Events.logMoveQuestions(EventProperties.question_feature);
        }
      }

      @Override
      public void onPageScrollStateChanged(int i) {
      }
    });

  }


  public void nextQuestion() {
    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
  }

  public void prevQuestion() {
    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
  }
}
