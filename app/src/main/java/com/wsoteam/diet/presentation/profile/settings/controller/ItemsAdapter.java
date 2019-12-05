package com.wsoteam.diet.presentation.profile.settings.controller;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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
import com.wsoteam.diet.presentation.profile.NotificationsSettingsActivity;
import com.wsoteam.diet.presentation.profile.about.AboutActivity;
import com.wsoteam.diet.presentation.profile.help.HelpActivity;
import com.wsoteam.diet.presentation.profile.norm.ChangeNormActivity;
import com.wsoteam.diet.presentation.promo.PromoFormActivity;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsViewHolders> {

    private final Context context;
    private final int[] ids;
    private final String[] names;

    private final TypedArray drawablesLeft;
    private final boolean isNotPrem;
    private boolean isLogOut;
    private int textColor;
    private int drawableArrow;

    private final int
            PREMIUM = 0,
    // FOOD = 1,
    PROMO = 1,
            PERSONAL = 2,
            KCAL = 3,
            NOTIFICATIONS = 4,
    //TARGET = 4,
    HELP = 5,
            LOGOUT = 6;

    public ItemsAdapter(Context context, boolean isNotPrem) {
        this.context = context;
        this.isNotPrem = isNotPrem;

        if (Locale.getDefault().getLanguage().equals("ru")) {
            if (isNotPrem) {
                names = context.getResources().getStringArray(R.array.settings_items_ru);
                ids = new int[]{PREMIUM, PROMO, PERSONAL, KCAL, NOTIFICATIONS, HELP, LOGOUT};
                this.drawablesLeft = context.getResources().obtainTypedArray(R.array.left_drawables);
            } else {
                names = context.getResources().getStringArray(R.array.settings_items_prem_ru);
                ids = new int[]{PROMO, PERSONAL, KCAL, NOTIFICATIONS, HELP, LOGOUT};
                this.drawablesLeft = context.getResources().obtainTypedArray(R.array.left_drawables_prem);
            }
        } else {
            if (isNotPrem) {
                names = context.getResources().getStringArray(R.array.settings_items);
                ids = new int[]{PREMIUM, PROMO, PERSONAL, KCAL, NOTIFICATIONS, LOGOUT};
                this.drawablesLeft = context.getResources().obtainTypedArray(R.array.left_drawables_trans);
            } else {
                names = context.getResources().getStringArray(R.array.settings_items_prem);
                ids = new int[]{PROMO, PERSONAL, KCAL, NOTIFICATIONS, LOGOUT};
                this.drawablesLeft = context.getResources().obtainTypedArray(R.array.left_drawables_prem_trans);
            }
        }
    }

    @NonNull
    @Override
    public ItemsViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new ItemsViewHolders(layoutInflater, viewGroup);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ItemsViewHolders holder) {
        super.onViewAttachedToWindow(holder);

        holder.itemView.setOnClickListener(v -> {
            goToItemSettings(holder.getAdapterPosition());
        });
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ItemsViewHolders holder) {
        super.onViewDetachedFromWindow(holder);

        holder.itemView.setOnClickListener(null);
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
        itemsViewHolders.bind(names[i], drawablesLeft.getResourceId(i, -1),
                textColor, drawableArrow, isLogOut);
    }

    private void goToItemSettings(int position) {
        int actionId = ids[position];

        switch (actionId) {
            case PREMIUM:
                final Box box = new Box();
                box.setComeFrom(AmplitudaEvents.view_prem_settings);
                box.setBuyFrom(EventProperties.trial_from_settings);
                box.setOpenFromPremPart(true);
                box.setOpenFromIntrodaction(false);

                final Intent intent = new Intent(context, ActivitySubscription.class);
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
            case NOTIFICATIONS:
                context.startActivity(new Intent(context, NotificationsSettingsActivity.class));
                break;
            /*case TARGET:
                Toast.makeText(context, "Раздел в разработке :(", Toast.LENGTH_SHORT).show();
                break;*/
            case HELP:
                context.startActivity(new Intent(context, HelpActivity.class));
                break;
            case LOGOUT:
                Events.logLogout();

                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.exit))
                        .setMessage(context.getString(R.string.log_off_question))
                        .setPositiveButton(context.getString(R.string.yes), (arg0, arg1) -> exitUser())
                        .setNegativeButton(context.getString(R.string.no), null)
                        .show();

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
