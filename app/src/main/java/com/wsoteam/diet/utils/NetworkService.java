package com.wsoteam.diet.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wsoteam.diet.BuildConfig;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static MutableLiveData<String> tokenLiveData = new MutableLiveData<>();

    private static NetworkService mInstance;
    // test server
    private static final String BASE_URL = "http://78.47.35.187:8000";
    // main server
    //  private static final String BASE_URL = "http://116.203.193.111:8000";
    private Retrofit mRetrofit;

    private NetworkService() {
        init();

    }

    private static void loadToken() {

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser != null)
            mUser.getIdToken(true)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            tokenLiveData.setValue(idToken);
                            Log.d("idToken","" + idToken);
                        }
                    });
    }

    public static LiveData<String> getTokenLivedata() {

        String token = tokenLiveData.getValue();

        if (token == null || token.equals("")) {
            loadToken();
        }

        return tokenLiveData;
    }

    private void init() {


        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel((BuildConfig.DEBUG) ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE));

        String token = tokenLiveData.getValue();

        if (token != null) {
            //Create a new Interceptor.
            Interceptor headerAuthorizationInterceptor = chain -> {
                okhttp3.Request request = chain.request();
                Headers headers = request.headers().newBuilder().add("Authorization", "JWT " + token).build();
                request = request.newBuilder().headers(headers).build();
                return chain.proceed(request);
            };

            okHttpBuilder.addInterceptor(headerAuthorizationInterceptor);
        }

        OkHttpClient okHttpClient = okHttpBuilder.build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

    }

    public static NetworkService getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkService();
        }
        return mInstance;
    }

    public ApiRequest getApi() {

        if (mRetrofit != null) {
            return mRetrofit.create(ApiRequest.class);
        } else {
            return null;
        }

    }
}
