package com.wsoteam.diet.MainScreen.Dialogs;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amplitude.api.Amplitude;
import com.google.common.util.concurrent.AtomicDouble;
import com.wsoteam.diet.AmplitudaEvents;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.WorkWithFirebaseDB;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.model.Grade;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class RateDialogs {

    private static void saveData(Context context, int status ){
        context.getSharedPreferences(Config.IS_GRADE_APP, Context.MODE_PRIVATE).
                edit().putInt(Config.IS_GRADE_APP, status).
                commit();
        context.getSharedPreferences(Config.STARTING_POINT, Context.MODE_PRIVATE).
                edit().putLong(Config.STARTING_POINT, Calendar.getInstance().getTimeInMillis()).
                commit();
    }

    public static void showWhiteDialog(Context context){



        Grade grade = new Grade(new Date().getTime());
        AtomicDouble atomicRating = new AtomicDouble(0.0);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.create();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.alert_dialog_grade_white, null);
        view.findViewById(R.id.gradeClose).setOnClickListener(v -> alertDialog.cancel());
        ((MaterialRatingBar)view.findViewById(R.id.ratingBarM)).setOnRatingChangeListener((ratingBar, rating) -> atomicRating.set(rating));
        view.findViewById(R.id.btnGradeSend).setOnClickListener(v -> {
            if (atomicRating.get() >= 4){
                final String appPackageName = context.getPackageName();
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException exp) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                saveData(context, Config.GRADED);
                Events.logReview(atomicRating.get(), EventProperties.review_comment_store);
            } else {
                grade.setRating(atomicRating.get());
                msgAlert(context, grade);
                Events.logReview(atomicRating.get(), EventProperties.review_comment_server);
            }
            alertDialog.cancel();
        });

        saveData(context, Config.NOT_GRADED);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.setView(view);
        alertDialog.show();
    }

    private static void msgAlert(Context context, Grade grade){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.create();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.alert_dialog_msg, null);
        view.findViewById(R.id.gradeClose).setOnClickListener(v -> alertDialog.cancel());
        MultiAutoCompleteTextView textView = view.findViewById(R.id.multiAutoCompleteTextView);
        view.findViewById(R.id.btnGradeSend).setOnClickListener(v -> {
            grade.setMsg(textView.getText().toString());
            WorkWithFirebaseDB.saveGrade(grade);
            doneAlert(context);
            alertDialog.cancel();
        });
        saveData(context, Config.NOT_GRADED);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.setView(view);
        alertDialog.show();

    }

    private static void doneAlert(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.create();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.alert_dialog_done, null);

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setRotation(180);
        ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
        animation.setDuration (1900); //in milliseconds
        animation.setInterpolator (new DecelerateInterpolator());
        animation.start ();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.setView(view);
        alertDialog.show();
        saveData(context, Config.GRADED);
        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        };

        alertDialog.setOnDismissListener(dialog ->
                handler.removeCallbacks(runnable));

        handler.postDelayed(runnable, 2000);

    }

    public static void showGradeDialog(Context context, boolean isForceCall) {
        if (!isForceCall) {
            context.getSharedPreferences(Config.IS_GRADE_APP, Context.MODE_PRIVATE).
                    edit().putInt(Config.IS_GRADE_APP, Config.NOT_GRADED).
                    commit();
            context.getSharedPreferences(Config.STARTING_POINT, Context.MODE_PRIVATE).
                    edit().putLong(Config.STARTING_POINT, Calendar.getInstance().getTimeInMillis()).
                    commit();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.create();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_grade, null);

        Animation movFromLeft = AnimationUtils.loadAnimation(context, R.anim.anim_moving_from_left);
        Animation movOutToRight = AnimationUtils.loadAnimation(context, R.anim.anim_moving_out_to_right);
        Animation movFromBottom = AnimationUtils.loadAnimation(context, R.anim.moving_from_bottom);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText edtReport = view.findViewById(R.id.edtRatingReport);
        TextView tvThank = view.findViewById(R.id.tvForGrade);
        ImageButton btnGradeClose = view.findViewById(R.id.btnGradeClose);
        Button btnGradeSend = view.findViewById(R.id.btnGradeSend);

        btnGradeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        btnGradeSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getSharedPreferences(Config.IS_GRADE_APP, Context.MODE_PRIVATE).
                        edit().putInt(Config.IS_GRADE_APP, Config.GRADED).
                        commit();
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "sav@wsoteam.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Отзыв");
                intent.putExtra(Intent.EXTRA_TEXT, edtReport.getText().toString());
                context.startActivity(Intent.createChooser(intent, "Send Email"));
                alertDialog.cancel();
            }
        });

        btnGradeSend.setVisibility(View.GONE);
        tvThank.setVisibility(View.GONE);
        edtReport.setVisibility(View.GONE);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (v >= 4) {
                    context.getSharedPreferences(Config.IS_GRADE_APP, Context.MODE_PRIVATE).
                            edit().putInt(Config.IS_GRADE_APP, Config.GRADED).
                            commit();
                    final String appPackageName = context.getPackageName();
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException exp) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    alertDialog.cancel();
                } else {
                    if (edtReport.getVisibility() == View.GONE) {
                        edtReport.setVisibility(View.VISIBLE);
                        edtReport.startAnimation(movFromLeft);
                        ratingBar.startAnimation(movOutToRight);
                        ratingBar.setVisibility(View.GONE);
                        tvThank.setVisibility(View.VISIBLE);
                        tvThank.startAnimation(movFromLeft);
                        btnGradeSend.setVisibility(View.VISIBLE);
                        btnGradeSend.startAnimation(movFromBottom);
                    }
                }
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.setView(view);
        alertDialog.show();
    }

    public static void showGradeDialogFruits(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.create();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_grade_fruits, null);

        Animation movFromLeft = AnimationUtils.loadAnimation(context, R.anim.anim_moving_from_left);
        Animation movOutToRight = AnimationUtils.loadAnimation(context, R.anim.anim_moving_out_to_right);
        Animation movFromBottom = AnimationUtils.loadAnimation(context, R.anim.moving_from_bottom);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText edtReport = view.findViewById(R.id.edtRatingReport);
        TextView tvThank = view.findViewById(R.id.tvForGrade);
        ImageButton btnGradeClose = view.findViewById(R.id.btnGradeClose);
        Button btnGradeSend = view.findViewById(R.id.btnGradeSend);

        btnGradeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        btnGradeSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "sav@wsoteam.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Отзыв");
                intent.putExtra(Intent.EXTRA_TEXT, edtReport.getText().toString());
                context.startActivity(Intent.createChooser(intent, "Send Email"));
                alertDialog.cancel();
            }
        });

        btnGradeSend.setVisibility(View.GONE);
        tvThank.setVisibility(View.GONE);
        edtReport.setVisibility(View.GONE);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (v >= 4) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));
                    context.startActivity(intent);
                    alertDialog.cancel();
                } else {
                    if (edtReport.getVisibility() == View.GONE) {
                        edtReport.setVisibility(View.VISIBLE);
                        edtReport.startAnimation(movFromLeft);
                        ratingBar.startAnimation(movOutToRight);
                        ratingBar.setVisibility(View.GONE);
                        tvThank.setVisibility(View.VISIBLE);
                        tvThank.startAnimation(movFromLeft);
                        btnGradeSend.setVisibility(View.VISIBLE);
                        btnGradeSend.startAnimation(movFromBottom);
                    }
                }
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.setView(view);
        alertDialog.show();
    }





}
