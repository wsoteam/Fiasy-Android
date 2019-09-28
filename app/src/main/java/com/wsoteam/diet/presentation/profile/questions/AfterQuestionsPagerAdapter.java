package com.wsoteam.diet.presentation.profile.questions;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.wsoteam.diet.Authenticate.POJO.Box;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.InApp.Fragments.FragmentSubscriptionOrangeOneButton;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.presentation.profile.questions.fragments.IndividualPlanFragments;
import com.wsoteam.diet.presentation.profile.questions.fragments.WeOfferFragments;

public class AfterQuestionsPagerAdapter extends FragmentPagerAdapter {

  private static int NUM_ITEMS = 2;

  public AfterQuestionsPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override public Fragment getItem(int position) {
    switch (position) {
      case 0:
        return IndividualPlanFragments.newInstance();
      case 1:
        //return WeOfferFragments.newInstance();
        Box box = new Box();
        box.setOpenFromIntrodaction(true);
        box.setBuyFrom(EventProperties.trial_from_onboard);
        return FragmentSubscriptionOrangeOneButton.newInstance(box);
      //case 2:

      default:
        return null;
    }
  }

  @Override public int getCount() {
    return NUM_ITEMS;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return "";
  }
}
