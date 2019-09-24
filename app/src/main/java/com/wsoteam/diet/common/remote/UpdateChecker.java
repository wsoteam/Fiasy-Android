package com.wsoteam.diet.common.remote;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.remote.POJO.StoreVersion;

import java.util.Calendar;

public class UpdateChecker {
    private Context context;

    public UpdateChecker(Context context) {
        this.context = context;
    }


    public void runChecker() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.UPDATE_PATH);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    compareVersions(dataSnapshot.getValue(StoreVersion.class));
                } catch (Exception ex) {
                    ex.getLocalizedMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void compareVersions(StoreVersion value) {
        int userVersion = checkUserVersion();
        int marketVersion = value.getVersionCode();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long timeAfterStart = currentTime - getStartPoint();
        long timeLastShow = currentTime - getPeriodPoint();

        if (userVersion < marketVersion) {
            if (getStartPoint() == Config.SP_START_POINT_EMPTY) {
                setStartPoint(currentTime);
                setPeriodPoint(currentTime);
                //showWeakDialog();
            } else {
                if (timeAfterStart > value.getTimeUntilHardUpdate()) {
                    showHardDialog();
                } else {
                    if (timeLastShow > value.getWeekPeriod()) {
                        setPeriodPoint(currentTime);
                        showWeakDialog();
                    }
                }
            }
        } else {
            clearSavedPoints();
        }
    }

    private void clearSavedPoints() {
        if (getPeriodPoint() != Config.SP_PERIOD_SHOW_POINT_EMPTY) {
            setPeriodPoint(Config.SP_PERIOD_SHOW_POINT_EMPTY);
        }
        if (getStartPoint() != Config.SP_PERIOD_SHOW_POINT_EMPTY) {
            setStartPoint(Config.SP_START_POINT_EMPTY);
        }
    }

    private void setPeriodPoint(long time) {
        context.getSharedPreferences(Config.SP_PERIOD_SHOW_POINT, Context.MODE_PRIVATE).edit().putLong(Config.SP_PERIOD_SHOW_POINT, time).commit();
    }

    private void setStartPoint(long time) {
        context.getSharedPreferences(Config.SP_START_POINT, Context.MODE_PRIVATE).edit().putLong(Config.SP_START_POINT, time).commit();
    }

    private long getStartPoint() {
        return context.getSharedPreferences(Config.SP_START_POINT, Context.MODE_PRIVATE).getLong(Config.SP_START_POINT, Config.SP_START_POINT_EMPTY);
    }

    private long getPeriodPoint() {
        return context.getSharedPreferences(Config.SP_PERIOD_SHOW_POINT, Context.MODE_PRIVATE).getLong(Config.SP_PERIOD_SHOW_POINT, Config.SP_PERIOD_SHOW_POINT_EMPTY);
    }


    private int checkUserVersion() {
        int codeVersion = -1;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            codeVersion = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return codeVersion;
    }


    private void showWeakDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.create();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_update, null);

        TextView tvGo = view.findViewById(R.id.tvOk);
        ImageButton ibClose = view.findViewById(R.id.ibClose);

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        tvGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = context.getPackageName();
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException exp) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.setView(view);
        alertDialog.show();
    }

    private void showHardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.create();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_update, null);

        TextView tvGo = view.findViewById(R.id.tvOk);
        ImageButton ibClose = view.findViewById(R.id.ibClose);
        ibClose.setVisibility(View.GONE);

        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK &&
                        keyEvent.getAction() == KeyEvent.ACTION_UP &&
                        !keyEvent.isCanceled()) {
                    ((AppCompatActivity) context).finish();
                    return true;
                }
                return false;
            }
        });

        tvGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = context.getPackageName();
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException exp) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.setView(view);
        alertDialog.show();
    }
}
