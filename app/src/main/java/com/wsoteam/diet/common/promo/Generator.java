package com.wsoteam.diet.common.promo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wsoteam.diet.common.promo.POJO.Promo;

import java.util.List;

public class Generator {
    public static long duration = 31536000000l;
    public static long durationTest = 180000l;
    public static int type = 0;
    public static boolean isActivated = false;
    public static String userOwner = "";

    public static void generate(List<String> ids){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.promoStoragePath);

        for (int i = 0; i < ids.size(); i++) {
            Promo promo = new Promo(ids.get(i), type, durationTest, isActivated, userOwner);
            myRef.child(ids.get(i)).setValue(promo);
        }
    }
}
