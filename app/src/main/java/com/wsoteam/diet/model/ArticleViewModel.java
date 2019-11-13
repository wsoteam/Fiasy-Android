package com.wsoteam.diet.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.wsoteam.diet.utils.NetworkService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ArticleViewModel extends ViewModel {

  private static MutableLiveData<ApiResult<Article>> data;

  public LiveData<ApiResult<Article>> getData() {
    if (data == null) {
      data = new MutableLiveData<>();
      loadData();
    }
    return data;
  }

  private void loadData() {
    //Log.d("kkk", "loadData: load");
    NetworkService.getInstance().getApi().getArticles()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> {
              data.setValue(t);
              //Log.d("kkk", "loadData: " + t.getCount());
              }, Throwable::printStackTrace);
  }

}
