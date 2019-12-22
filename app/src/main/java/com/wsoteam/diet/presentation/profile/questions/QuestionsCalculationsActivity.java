package com.wsoteam.diet.presentation.profile.questions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.squareup.picasso.Picasso;
import com.wsoteam.diet.ABConfig;
import com.wsoteam.diet.AmplitudaEvents;
import com.wsoteam.diet.Authenticate.POJO.Box;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.InApp.ActivitySubscription;
import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Recipes.POJO.GroupsHolder;
import com.wsoteam.diet.Recipes.POJO.RecipeItem;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.UserProperty;
import com.wsoteam.diet.common.helpers.BodyCalculates;
import com.wsoteam.diet.utils.NetworkService;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;

public class QuestionsCalculationsActivity extends AppCompatActivity {
    @BindView(R.id.loader)
    ImageView loader;

    private boolean isNeedShowOnboard, createUser;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_calculations);
        ButterKnife.bind(this);

        final Drawable drawable = loader.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }

        final SharedPreferences sp = getSharedPreferences(Config.ONBOARD_PROFILE, MODE_PRIVATE);

        final boolean isFemale = sp.getBoolean(Config.ONBOARD_PROFILE_SEX, true);
        final int age = sp.getInt(Config.ONBOARD_PROFILE_YEARS, BodyCalculates.DEFAULT_AGE);
        final int height = sp.getInt(Config.ONBOARD_PROFILE_HEIGHT, BodyCalculates.DEFAULT_HEIGHT);
        final double weight =
                (double) sp.getInt(Config.ONBOARD_PROFILE_WEIGHT, (int) BodyCalculates.DEFAULT_WEIGHT);
        final String activity = sp.getString(Config.ONBOARD_PROFILE_ACTIVITY, getString(R.string.level_none));
        final String diff = sp.getString(Config.ONBOARD_PROFILE_PURPOSE, getString(R.string.dif_level_easy));

        final Profile profileFinal =
                BodyCalculates.calculate(this, weight, height, age, isFemale, activity, diff);

        profileFinal.setFirstName(sp.getString(Config.ONBOARD_PROFILE_NAME,
                BodyCalculates.DEFAULT_FIRST_NAME));

        if (getSharedPreferences(Config.IS_NEED_SHOW_ONBOARD, MODE_PRIVATE).getBoolean(
                Config.IS_NEED_SHOW_ONBOARD, false)) {
            isNeedShowOnboard = true;
            getSharedPreferences(Config.IS_NEED_SHOW_ONBOARD, MODE_PRIVATE).edit()
                    .putBoolean(Config.IS_NEED_SHOW_ONBOARD, false)
                    .apply();
        }
        createUser = getIntent().getBooleanExtra(Config.CREATE_PROFILE, true);

        final Disposable d = NetworkService.getInstance().getApi()
                .getArticles()
                .flatMap(response -> Observable.fromIterable(response.getResults()))
                .doOnNext(article ->
                        Picasso.get()
                                .load(article.getImage())
                                .resizeDimen(R.dimen.article_card_width, R.dimen.article_card_height)
                                .centerCrop()
                                .config(Bitmap.Config.RGB_565)
                                .fetch()
                )
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

        disposable.add(d);

        GroupsHolder.loadRecipes(this, receipts -> {
            final Disposable r = Flowable.fromIterable(receipts.getListrecipes())
                    .doOnNext(receipt -> {
                        Picasso.get()
                                .load(receipt.getUrl())
                                .resizeDimen(R.dimen.receipt_container_width, R.dimen.receipt_container_height)
                                .centerCrop()
                                .fetch();
                    })
                    .subscribe();

            disposable.add(r);
        });

        loader.postDelayed(() -> saveProfile(isNeedShowOnboard, profileFinal, createUser), 4000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    void saveProfile(boolean isNeedShowOnboard, Profile profile, boolean createProfile) {
        if (createProfile) {
            //Intent intent = new Intent(this, MainAuthNewActivity.class);
            Intent intent = new Intent(this, AfterQuestionsActivity.class);
            if (isNeedShowOnboard) {
                Box box = new Box();
                box.setBuyFrom(EventProperties.trial_from_onboard);
                box.setComeFrom(AmplitudaEvents.view_prem_free_onboard);
                box.setOpenFromIntrodaction(true);
                box.setOpenFromPremPart(false);
                intent.putExtra(Config.CREATE_PROFILE, true)
                        .putExtra(Config.INTENT_PROFILE, profile);
            } else {
                intent.putExtra(Config.INTENT_PROFILE, profile);
            }
            //startActivity(intent);
            moveNext(profile);
            finish();
        }

        if (profile != null) {
            UserProperty.setUserProperties(profile, this, false);
            WorkWithFirebaseDB.putProfileValue(profile);
        }
    }

    private void moveNext(Profile profile) {
        String abVersion = getSharedPreferences(ABConfig.KEY_FOR_SAVE_STATE, MODE_PRIVATE).
                getString(ABConfig.KEY_FOR_SAVE_STATE, "default");
        Intent intent = new Intent(this, ActivitySubscription.class);
        Box box = new Box();
        box.setBuyFrom(EventProperties.trial_from_onboard);
        box.setComeFrom(AmplitudaEvents.view_prem_free_onboard);
        box.setOpenFromIntrodaction(true);
        box.setOpenFromPremPart(false);
        box.setProfile(profile);
        intent.putExtra(Config.TAG_BOX, box);
        startActivity(intent);
    }

    private void markAfterPremRoad() {
        getSharedPreferences(Config.AFTER_PREM_ROAD, MODE_PRIVATE).edit().putBoolean(Config.AFTER_PREM_ROAD, true).commit();
    }
}