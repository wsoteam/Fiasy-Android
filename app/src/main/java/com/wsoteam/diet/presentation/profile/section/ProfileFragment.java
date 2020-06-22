package com.wsoteam.diet.presentation.profile.section;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.squareup.picasso.Picasso;
import com.wsoteam.diet.AmplitudaEvents;
import com.wsoteam.diet.Authenticate.POJO.Box;
import com.wsoteam.diet.DietPlans.POJO.DietPlan;
import com.wsoteam.diet.InApp.ActivitySubscription;
import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Sync.UserDataHolder;
import com.wsoteam.diet.ads.FiasyAds;
import com.wsoteam.diet.ads.nativetemplates.NativeTemplateStyle;
import com.wsoteam.diet.ads.nativetemplates.TemplateView;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.views.graph.BarRenderer;
import com.wsoteam.diet.common.views.graph.LineRenderer;
import com.wsoteam.diet.common.views.graph.formater.XMonthFormatter;
import com.wsoteam.diet.common.views.graph.formater.XWeekFormatter;
import com.wsoteam.diet.common.views.graph.formater.XYearFormatter;
import com.wsoteam.diet.common.views.graph.marker.BarMarker;
import com.wsoteam.diet.presentation.auth.AuthUtil;
import com.wsoteam.diet.presentation.profile.settings.ProfileSettingsActivity;

import com.wsoteam.diet.utils.DrawableUtilsKt;
import com.wsoteam.diet.utils.Subscription;
import com.wsoteam.diet.utils.ViewUtils;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.concat;

public class ProfileFragment extends MvpAppCompatFragment implements ProfileView {
    @BindView(R.id.ibSettings)
    ImageButton ibProfileEdit;
    @BindView(R.id.civProfile)
    CircleImageView civProfile;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvKcalMax)
    TextView tvKcalMax;
    @BindView(R.id.tvCarboCount)
    TextView tvCarboCount;
    @BindView(R.id.tvFatCount)
    TextView tvFatCount;
    @BindView(R.id.tvProtCount)
    TextView tvProtCount;
    @BindView(R.id.tvPlan)
    TextView tvPlanName;
    private static final int CAMERA_REQUEST = 1888;
    Unbinder unbinder;
    ProfilePresenter profilePresenter;

    @BindView(R.id.ibExpandable)
    ImageButton ibExpandable;
    @BindView(R.id.donutProgress)
    DonutProgress donutProgress;
    @BindView(R.id.gv)
    CombinedChart gv;
    @BindView(R.id.tvTopDateSwitcher)
    TextView tvTopDateSwitcher;
    @BindView(R.id.tvBottomDateSwitcher)
    TextView tvBottomDateSwitcher;
    @BindView(R.id.rgrpInterval)
    RadioGroup rgrpInterval;

    @BindView(R.id.profilePremium)
    View profilePremium;

    @BindView(R.id.nativeAd)
    TemplateView nativeAd;
    private boolean isOpen = false;
    private float MAX_PROGRESS = 100;
    private long time = 500, periodTick = 5, countTick = 100;
    private CountDownTimer countDownTimer;
    private int counterMove = 0;
    private int INTERVAL_CHOISE = 0; //0 - week, 1 - month, 2 - year
    private final int CHOISED_WEEK = 0, CHOISED_MONTH = 1, CHOISED_YEAR = 2;
    private ArrayList<String> days = new ArrayList<>();


    @Override
    public void bindCircleProgressBar(float progress) {
        if (progress <= 100) {
            animFillProgressBar(progress, false);
        } else {
            animFillProgressBar(MAX_PROGRESS, true);
        }
    }

    private void animFillProgressBar(float progress, boolean isMax) {
        countDownTimer = new CountDownTimer(time, periodTick) {
            @Override
            public void onTick(long millisUntilFinished) {
                int stepNumber = (int) ((time - millisUntilFinished) / periodTick);
                double stepValue = ((double) progress) / ((double) countTick);
                donutProgress.setProgress(((float) stepValue * stepNumber));
            }

            @Override
            public void onFinish() {
                if (isMax) {
                    donutProgress.setProgress(MAX_PROGRESS);
                    donutProgress.setFinishedStrokeColor(
                            getResources().getColor(R.color.main_calories_left_over));
                }
            }
        }.start();
    }

    @Override
    public void drawYearGraphs(List<BarEntry> pairs, int[] colors, String bottomText,
                               String topText) {
        XYearFormatter xYearFormatter = new XYearFormatter();
        drawGraph(pairs, colors, xYearFormatter, topText, bottomText, 0.6f);
    }

    @Override
    public void drawMonthGraphs(List<BarEntry> pairs, int[] colors, String bottomText, String topText,
                                ArrayList<String> namesIntervals) {

        XMonthFormatter xMonthFormatter = new XMonthFormatter(namesIntervals);
        drawGraph(pairs, colors, xMonthFormatter, topText, bottomText, 0.3f);
    }

    @Override
    public void drawWeekGraphs(List<BarEntry> pairs, int[] colors, String bottomText,
                               String topText) {
        XWeekFormatter xWeekFormatter = new XWeekFormatter();
        drawGraph(pairs, colors, xWeekFormatter, topText, bottomText, 0.4f);
    }

    private void drawGraph(List<BarEntry> pairs, int[] colors, ValueFormatter valueFormatter,
                           String topText, String bottomText, float width) {
        if (gv.getBarData() != null) {
            gv.invalidate();
            gv.clearValues();
            gv.notifyDataSetChanged();
        }
        BarDataSet barDataSet = new BarDataSet(pairs, "");
        barDataSet.setColors(colors);
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        gv.setDrawValueAboveBar(true);
        barData.setBarWidth(width);

        List<Entry> limitLine = new ArrayList<>();
        int limit = 0;
        try {
            limit = UserDataHolder.getUserData().getProfile().getMaxKcal();
        } catch (NullPointerException e){
            Log.e("error", "UserDataHolder.getUserData().getProfile().getMaxKcal() == null", e);
        }

        limitLine.add(new Entry(-1, limit));
        for (int i = 0; i < pairs.size(); i++) {
            Entry entry = new Entry(pairs.get(i).getX(), limit);
            limitLine.add(entry);
        }
        limitLine.add(new Entry(pairs.size(), limit));
        LineDataSet lineDataSet = new LineDataSet(limitLine, "");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(getResources().getColor(R.color.color_line_chart));
        lineDataSet.setLineWidth(3f);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        LineData lineData = new LineData(lineDataSet);
        lineData.setDrawValues(false);
        lineDataSet.setHighLightColor(getResources().getColor(R.color.color_line_chart));
        lineDataSet.setHighlightLineWidth(2);
        lineDataSet.setCircleRadius(3);

        XAxis xAxis = gv.getXAxis();
        xAxis.setGranularity(1);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(valueFormatter);
        xAxis.setAxisMinimum(barData.getXMin() - .5f);
        xAxis.setAxisMaximum(barData.getXMax() + .5f);

        YAxis yAxisRight = gv.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisRight.setAxisMinimum(0f);

        YAxis yAxisLeft = gv.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);
        if (barData.getYMax() >= limit) {
            yAxisLeft.setAxisMaximum(barData.getYMax() + 500);
        } else {
            yAxisLeft.setAxisMaximum(limit + 500);
        }
        gv.setMarker(new MarkerView(getActivity(), R.layout.marker_calories));
        BarMarker barMarker = new BarMarker(getActivity(), R.layout.marker_calories, colors, pairs,
                getResources().getColor(R.color.color_line_chart));

        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);

        Bitmap starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_line);

        List<DataRenderer> subRenderers = new ArrayList<>();
        DataRenderer dataRenderer =
                new BarRenderer(gv, gv.getAnimator(), gv.getViewPortHandler(), colors, 18);
        subRenderers.add(dataRenderer);
        DataRenderer lineRenderer =
                new LineRenderer(gv, gv.getAnimator(), gv.getViewPortHandler(), starBitmap);
        subRenderers.add(lineRenderer);
        CombinedChartRenderer combinedChartRenderer =
                new CombinedChartRenderer(gv, gv.getAnimator(), gv.getViewPortHandler());
        combinedChartRenderer.setSubRenderers(subRenderers);

        gv.setDrawBarShadow(false);
        gv.getDescription().setEnabled(false);
        gv.setDrawGridBackground(false);
        gv.getLegend().setEnabled(false);
        gv.setData(combinedData);
        gv.setMarker(barMarker);
        gv.setDragEnabled(false);
        gv.setScaleEnabled(false);
        gv.setRenderer(combinedChartRenderer);
        gv.animateY(1000, Easing.EaseOutBack);
        gv.notifyDataSetChanged();
        gv.invalidate();

        tvTopDateSwitcher.setText(topText);
        tvBottomDateSwitcher.setText(bottomText);
    }

    @Override
    public void onPause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onPause();
    }

    @Override
    public void fillViewsIfProfileNotNull(Profile profile, DietPlan plan) {
        tvKcalMax.setText(String.format(getString(R.string.format_int_kcal), profile.getMaxKcal()));
        tvCarboCount.setText(String.format(getString(R.string.n_g), profile.getMaxCarbo()));
        tvFatCount.setText(String.format(getString(R.string.n_g), profile.getMaxFat()));
        tvProtCount.setText(String.format(getString(R.string.n_g), profile.getMaxProt()));
        tvPlanName.setText(plan != null ? concat(getString(R.string.nutrition_plan), " - ", plan.getName()) : "");

        if (TextUtils.isEmpty(profile.getFirstName())
          || profile.getFirstName().toLowerCase().equals("default")) {
            tvUserName.setText(getString(R.string.your_name));
        } else {
          if (profile.getLastName().toLowerCase().equals("default")) {
            tvUserName.setText(profile.getFirstName());
          } else {
            tvUserName.setText(profile.getFirstName() + " " + profile.getLastName());
          }
        }

        setPhoto(profile);
    }

    @ProvidePresenter
    ProfilePresenter providePresenter() {
        return profilePresenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        Subscription.setVisibility(profilePremium);
        profilePresenter.attachPresenter();
        clearSwitch();
    }

    private void clearSwitch() {
        rgrpInterval.check(R.id.rbtnWeek);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        profilePresenter = new ProfilePresenter(requireContext());
        profilePresenter.attachView(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AuthUtil.Companion.prepareLogInView(getContext(), view.findViewById(R.id.profileLogIn));
        profilePremium.setOnClickListener(v -> {
            Box box = new Box();
            box.setSubscribe(false);
            box.setOpenFromPremPart(true);
            box.setOpenFromIntrodaction(false);
            box.setComeFrom(AmplitudaEvents.view_prem_content);
            box.setBuyFrom(EventProperties.trial_from_header);  // TODO проверить правильность флагов
            Intent intent = new Intent(getContext(), ActivitySubscription.class).putExtra(com.wsoteam.diet.Config.TAG_BOX, box);
            startActivity(intent);
        });

        FiasyAds.getLiveDataAdView().observe(this, ad -> {
            if (ad != null && !Subscription.check(getContext())) {
                nativeAd.setVisibility(View.VISIBLE);
                nativeAd.setStyles(new NativeTemplateStyle.Builder().build());
                nativeAd.setNativeAd(ad);
            }else {
                nativeAd.setVisibility(View.GONE);
            }
        });

        FiasyAds.adStatus.observe(getViewLifecycleOwner(), isOpen ->{
            if (!isOpen) nativeAd.setVisibility(View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setPhoto(Profile profile) {
        if (profile.getPhotoUrl() != null
                && !profile.getPhotoUrl().toLowerCase().equals("default")
                && !profile.getPhotoUrl().equals("")) {
            Picasso.get().load(profile.getPhotoUrl()).into(civProfile);
        } else {
            final Drawable d;

            if (profile.isFemale()) {
                d = VectorDrawableCompat.create(getResources(), R.drawable.female_avatar,
                    getContext().getTheme());
            } else {
                d = VectorDrawableCompat.create(getResources(), R.drawable.male_avatar,
                    getContext().getTheme());
            }

            civProfile.setImageDrawable(d);
        }
    }

    @OnClick({R.id.ibSettings, R.id.ibExpandable, R.id.ibLeft, R.id.ibRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ibSettings:
                FiasyAds.openInter();
                startActivity(new Intent(getActivity(), ProfileSettingsActivity.class));
                break;
            case R.id.ibExpandable:
                openParams();
                break;
            case R.id.ibLeft:
                counterMove--;
                drawChoisedGraph();
                break;
            case R.id.ibRight:
                counterMove++;
                drawChoisedGraph();
                break;
        }
    }

    @OnClick({R.id.rbtnWeek, R.id.rbtnMonth, R.id.rbtnYear})
    public void onRadioButtonClicked(RadioButton radioButton) {
        boolean checked = radioButton.isChecked();
        switch (radioButton.getId()) {
            case R.id.rbtnWeek:
                if (checked) {
                    counterMove = 0;
                    INTERVAL_CHOISE = 0;
                    drawChoisedGraph();
                }
                break;
            case R.id.rbtnMonth:
                if (checked) {
                    counterMove = 0;
                    INTERVAL_CHOISE = 1;
                    drawChoisedGraph();
                }
                break;
            case R.id.rbtnYear:
                if (checked) {
                    counterMove = 0;
                    INTERVAL_CHOISE = 2;
                    drawChoisedGraph();
                }
                break;
        }
    }

    private void drawChoisedGraph() {
        switch (INTERVAL_CHOISE) {
            case CHOISED_WEEK:
                profilePresenter.getWeekGraph(counterMove);
                break;
            case CHOISED_MONTH:
                profilePresenter.getMonthGraph(counterMove);
                break;
            case CHOISED_YEAR:
                profilePresenter.getYearGraph(counterMove);
                break;
        }
    }

    private void openParams() {
        if (isOpen) {
            ViewUtils.apply(getView(), new int[]{
                R.id.tvCarboCount, R.id.tvFatCount, R.id.tvProtCount, R.id.tvLabelProt, R.id.tvLabelCarbo,
                R.id.tvLabelFats
            }, view -> view.setVisibility(View.GONE));

            isOpen = false;

            ibExpandable.setImageDrawable(DrawableUtilsKt.getVectorIcon(getContext(),
                R.drawable.ic_open_detail_profile));
        } else {
            ViewUtils.apply(getView(), new int[]{
                R.id.tvCarboCount, R.id.tvFatCount, R.id.tvProtCount, R.id.tvLabelProt, R.id.tvLabelCarbo,
                R.id.tvLabelFats
            }, view -> view.setVisibility(View.VISIBLE));

            isOpen = true;

            ibExpandable.setImageDrawable(DrawableUtilsKt.getVectorIcon(getContext(),
                R.drawable.ic_close_detail_profile));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            profilePresenter.uploadPhoto((Bitmap) data.getExtras().get("data"));
        }
    }
}
