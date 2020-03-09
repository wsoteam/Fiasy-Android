package com.wsoteam.diet.model;

import android.util.Log;

import com.wsoteam.diet.model.rest.WaterDTO;
import com.wsoteam.diet.utils.NetworkService;


import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WaterViewModel extends ViewModel {

    private static MutableLiveData<ApiResult<WaterDTO>> data;

    public LiveData<ApiResult<WaterDTO>> getData() {
        if (data == null) {
            data = new MutableLiveData<>();
            loadData();
        }
        return data;
    }

    private void loadData() {
        //Log.d("kkk", "loadData: load");

        NetworkService.getTokenLivedata().observeForever(s -> {
            NetworkService.getInstance().getApi().getWater()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        data.setValue(result);
                        Log.d("kkk", "loadData: " + result.getNext());
                        Log.d("kkk", "loadData: " + result.getResults());
                        if (result.getNext() != null){

                            Uri uri = Uri.parse(result.getNext().toString());
                            loadData(uri.getQueryParameter("offset"),
                            uri.getQueryParameter("limit"));
                        }

                    }, Throwable::printStackTrace);

        });
    }

    private void loadData(String limit, String offset) {
        Log.d("kkk", "loadData: limit = " + limit);
        Log.d("kkk", "loadData: offset = " + offset);


            NetworkService.getInstance().getApi().getWater(limit, offset)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        data.setValue(result);
                        Log.d("kkk", "loadData: " + result.getResults());
                    }, Throwable::printStackTrace);


    }

}
