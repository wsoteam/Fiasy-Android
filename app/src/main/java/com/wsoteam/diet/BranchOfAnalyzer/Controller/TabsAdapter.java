package com.wsoteam.diet.BranchOfAnalyzer.Controller;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class TabsAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments;

    public TabsAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);

    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
