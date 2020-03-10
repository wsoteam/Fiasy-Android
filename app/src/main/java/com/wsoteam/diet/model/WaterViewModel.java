package com.wsoteam.diet.model;

import android.util.Log;

import com.wsoteam.diet.model.rest.WaterDTO;
import com.wsoteam.diet.utils.LiveDataExtKt;
import com.wsoteam.diet.utils.NetworkService;


import android.net.Uri;

import java.util.Stack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WaterViewModel extends ViewModel {

    private static MutableLiveData<ApiResult<WaterDTO>> data;

    private Stack<Observer<String>> observerStack = new Stack<>();

    public LiveData<ApiResult<WaterDTO>> getData() {
        if (data == null) {
            data = new MutableLiveData<>();
            loadData("100", "0");
        }
        return data;
    }

    private void loadData(String limit, String offset) {
        Log.d("kkk", "loadData: limit = " + limit);
        Log.d("kkk", "loadData: offset = " + offset);

        Observer<String> observer = s -> {
            if (s == null) {

            } else {
                NetworkService.getTokenLivedata().removeObserver(observerStack.pop());

                NetworkService.getInstance().getApi().getWater(limit, offset)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {


                            if (data.getValue() == null) {
                                data.setValue(result);
                            } else {
                                data.getValue().getResults().addAll(result.getResults());
                                LiveDataExtKt.notifyObserver(data);
                            }

                            Log.d("kkk", "loadData: " + result.getResults().size());
                            if (result.getNext() != null) {

                                Uri uri = Uri.parse(result.getNext().toString());
                                loadData(uri.getQueryParameter("limit"),
                                        uri.getQueryParameter("offset"));
                            }

                        }, Throwable::printStackTrace);
            }
        };

        observerStack.push(observer);

        NetworkService.getTokenLivedata().observeForever(observer);

    }

}
