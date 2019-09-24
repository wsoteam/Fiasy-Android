package com.wsoteam.diet.presentation.profile.settings.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.wsoteam.diet.AmplitudaEvents;
import com.wsoteam.diet.Authenticate.POJO.Box;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.EntryPoint.ActivitySplash;
import com.wsoteam.diet.InApp.ActivitySubscription;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.presentation.auth.AuthStrategy;
import com.wsoteam.diet.presentation.profile.about.AboutActivity;
import com.wsoteam.diet.presentation.profile.help.HelpActivity;
import com.wsoteam.diet.presentation.profile.norm.ChangeNormActivity;
import com.wsoteam.diet.presentation.promo.PromoFormActivity;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsViewHolders> {

    Context context;
    String[] names;
    TypedArray drawablesLeft;
    boolean isNotPrem;
    boolean isLogOut;
    int textColor;
    int drawableArrow;
    private final int PREMIUM = 0,
    // FOOD = 1,
    PROMO = 1,
            PERSONAL = 2,
            KCAL = 3,
    //NOTIFICATIONS = 3,
    //TARGET = 4,
    HELP = 4,
            LOGOUT = 5;

    public ItemsAdapter(Context context, boolean isNotPrem) {
        this.context = context;
        this.isNotPrem = isNotPrem;
        if (isNotPrem) {
            names = context.getResources().getStringArray(R.array.settings_items);
            this.drawablesLeft = context.getResources().obtainTypedArray(R.array.left_drawables);
        } else {
            names = context.getResources().getStringArray(R.array.settings_items_prem);
            this.drawablesLeft = context.getResources().obtainTypedArray(R.array.left_drawables_prem);
        }
    }

    @NonNull
    @Override
    public ItemsViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new ItemsViewHolders(layoutInflater, viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolders itemsViewHolders, int i) {
        if (isNotPrem && i == 0) {
            textColor = context.getResources().getColor(R.color.prem_settings);
            drawableArrow = R.drawable.ic_settings_item_prem;
        } else if (i != names.length - 1) {
            textColor = context.getResources().getColor(R.color.unprem_settings);
            drawableArrow = R.drawable.ic_settings_item_arrow_normal;
        } else {
            textColor = context.getResources().getColor(R.color.logout_settings);
            drawableArrow = R.drawable.ic_settings_item_arrow_normal;
        }
        if (i == names.length - 1) {
            isLogOut = true;
        } else {
            isLogOut = false;
        }
        itemsViewHolders.bind(names[i], drawablesLeft.getResourceId(i, -1), textColor, drawableArrow, isNotPrem, isLogOut, new ClickCallback() {
                    @Override
                    public void clickItem(int position) {
                        goToItemSettings(position);
                    }
                }
        );
    }

    private void goToItemSettings(int position) {
        if (!isNotPrem) position += 1;
        Intent intent;
        switch (position) {
            case PREMIUM:
                intent = new Intent(context, ActivitySubscription.class);
                Box box = new Box();
                box.setComeFrom(AmplitudaEvents.view_prem_settings);
                box.setBuyFrom(EventProperties.trial_from_settings);
                box.setOpenFromPremPart(true);
                box.setOpenFromIntrodaction(false);
                intent.putExtra(Config.TAG_BOX, box);
                context.startActivity(intent);
                break;
            /*case FOOD:
                Toast.makeText(context, "Раздел в разработке :(", Toast.LENGTH_SHORT).show();
                break;*/
            case PROMO:
                context.startActivity(new Intent(context, PromoFormActivity.class));
                break;
            case PERSONAL:
                context.startActivity(new Intent(context, AboutActivity.class));
                break;
            case KCAL:
                context.startActivity(new Intent(context, ChangeNormActivity.class));
                break;
            /*case NOTIFICATIONS:
                Toast.makeText(context, "Раздел в разработке :(", Toast.LENGTH_SHORT).show();
                break;
            case TARGET:
                Toast.makeText(context, "Раздел в разработке :(", Toast.LENGTH_SHORT).show();
                break;*/
            case HELP:
                context.startActivity(new Intent(context, HelpActivity.class));
                break;
            case LOGOUT:
                Events.logLogout();
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(context);
                myAlertDialog.setTitle("Выход");
                myAlertDialog.setMessage("Вы хотите выйти из учетной записи?");

                myAlertDialog.setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                exitUser();
                            }
                        });

                myAlertDialog.setNegativeButton("Нет",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                myAlertDialog.show();
                break;
        }

    }

    private void exitUser() {
        AuthStrategy.signOut(context);
        UserDataHolder.clearObject();
        Intent intent = new Intent(context, ActivitySplash.class).
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }
}
