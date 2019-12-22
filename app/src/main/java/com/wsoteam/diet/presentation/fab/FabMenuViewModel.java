package com.wsoteam.diet.presentation.fab;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FabMenuViewModel extends ViewModel {
    public final static String FAB_HIDE = "FAB_HIDE";
    public final static String FAB_SHOW = "FAB_SHOW";

    public static MutableLiveData<Boolean> isNeedClose = new MutableLiveData<>();
    public static MutableLiveData<String> fabState = new MutableLiveData<>();
}
