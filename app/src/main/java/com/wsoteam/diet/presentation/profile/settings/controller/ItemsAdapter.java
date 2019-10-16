package com.wsoteam.diet.presentation.profile.settings.controller;

import android.content.Context;
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

public class ItemsAdapter extends RecyclerView.Adapter<ItemsViewHolders> {

    private final Context context;
    private final int[] ids;
    private final String[] names;

    private final TypedArray drawablesLeft;
    private final boolean isNotPrem;
    private boolean isLogOut;
    private int textColor;
    private int drawableArrow;

    private final static int PREMIUM = 0, PERSONAL = 1, KCAL = 2,
    //NOTIFICATIONS = 3,
    //TARGET = 4,
    HELP = 3, LOGOUT = 4;

    public ItemsAdapter(Context context, boolean isNotPrem) {
        this.context = context;
        this.isNotPrem = isNotPrem;

        if (isNotPrem) {
            names = context.getResources().getStringArray(R.array.settings_items);
            ids = new int[] { PREMIUM, PERSONAL, HELP, LOGOUT };
            this.drawablesLeft = context.getResources().obtainTypedArray(R.array.left_drawables);
        } else {
            names = context.getResources().getStringArray(R.array.settings_items_prem);
            ids = new int[] { PERSONAL, HELP, LOGOUT };
            this.drawablesLeft = context.getResources().obtainTypedArray(R.array.left_drawables_prem);
        }
    }

    @NonNull
    @Override
    public ItemsViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new ItemsViewHolders(layoutInflater, viewGroup);
    }

    @Override public void onViewAttachedToWindow(@NonNull ItemsViewHolders holder) {
      super.onViewAttachedToWindow(holder);

      holder.itemView.setOnClickListener(v -> {
        goToItemSettings(holder.getAdapterPosition());
      });
    }

    @Override public void onViewDetachedFromWindow(@NonNull ItemsViewHolders holder) {
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

                new AlertDialog.Builder(context)
                  .setTitle("Выход")
                  .setMessage("Вы хотите выйти из учетной записи?")
                  .setPositiveButton("Да", (arg0, arg1) -> exitUser())
                  .setNegativeButton("Нет", null)
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
