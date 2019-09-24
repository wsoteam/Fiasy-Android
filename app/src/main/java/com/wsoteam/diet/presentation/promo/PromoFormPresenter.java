package com.wsoteam.diet.presentation.promo;

import android.content.Context;

import androidx.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wsoteam.diet.Sync.POJO.UserData;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.promo.Config;
import com.wsoteam.diet.common.promo.POJO.Promo;
import com.wsoteam.diet.common.promo.POJO.UserPromo;

import java.util.Calendar;

@InjectViewState
public class PromoFormPresenter extends MvpPresenter<PromoFormView> {
    private Context context;


    public PromoFormPresenter(Context context) {
        this.context = context;
    }

    public void checkPromo(String promoId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.promoStoragePath).child(promoId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Promo promo = dataSnapshot.getValue(Promo.class);
                if (promo != null){
                    getPromo(promo);
                }else {
                    getViewState().resultCheckedPromo(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPromo(Promo promo) {
        if (UserDataHolder.getUserData() != null && UserDataHolder.getUserData().getProfile() != null){
            if (!promo.isActivated()){
                UserPromo userPromo = new UserPromo();
                userPromo.setDuration(promo.getDuration());
                userPromo.setId(promo.getId());
                userPromo.setStartActivated(Calendar.getInstance().getTimeInMillis());
                userPromo.setType(promo.getType());

                WorkWithFirebaseDB.setUserPromo(userPromo);

                promo.setActivated(true);
                promo.setUserOwner(FirebaseAuth.getInstance().getCurrentUser().getUid());

                WorkWithFirebaseDB.changePromo(promo);
                getViewState().resultCheckedPromo(true);
            }else {
                getViewState().resultCheckedPromo(false);
            }
        }
    }
}
