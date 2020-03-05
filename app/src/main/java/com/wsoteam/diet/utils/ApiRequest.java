package com.wsoteam.diet.utils;

import com.wsoteam.diet.model.ApiResult;
import com.wsoteam.diet.model.Article;
import com.wsoteam.diet.model.Author;
import com.wsoteam.diet.model.rest.UserDTO;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiRequest {

  @GET("/api/v1/articles/")
  Observable<ApiResult<Article>> getArticles();

  @GET("/api/v1/authors")
  Observable<ApiResult<Author>> getAuthors();

  @POST("/api/v1/sendsay/set")
  @FormUrlEncoded
  Single<ResponseBody> sign2Newsletters(@Field("email") String email, @Field("os") String os);

  @GET("/api/v1/users/{user_id}/")
  Observable<UserDTO> getUser(@Path("user_id") String userId);
}