package com.wsoteam.diet.presentation.measurment.days;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.measurment.ConfigMeasurment;
import com.wsoteam.diet.presentation.measurment.POJO.Weight;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DaysFragment extends MvpAppCompatFragment implements DaysView {
    @InjectPresenter
    DaysPresenter daysPresenter;
    private static final String POSITION = "POSITION";
    @BindView(R.id.tvMonday) TextView tvMonday;
    @BindView(R.id.tvWednesday) TextView tvWednesday;
    @BindView(R.id.tvTuesday) TextView tvTuesday;
    @BindView(R.id.tvThursday) TextView tvThursday;
    @BindView(R.id.tvFriday) TextView tvFriday;
    @BindView(R.id.tvSaturday) TextView tvSaturday;
    @BindView(R.id.tvSunday) TextView tvSunday;
    Unbinder unbinder;
    private int currentPosition;
    @BindViews({R.id.tvMonday, R.id.tvWednesday, R.id.tvTuesday, R.id.tvThursday, R.id.tvFriday,
            R.id.tvSaturday, R.id.tvSunday})
    List<TextView> daysViewsList;
    private TextView tvMediumWeight;
    private TextView tvTopText;
    private TextView tvBottomText;
    private String topText, bottomText, weekAverage;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            tvTopText.setText(topText);
            tvBottomText.setText(bottomText);
            tvMediumWeight.setText(weekAverage + " " + getString(R.string.meas_kg));
        }
    }

    public static DaysFragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, position);
        DaysFragment daysFragment = new DaysFragment();
        daysFragment.setArguments(bundle);
        return daysFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.days_fragment, container, false);
        currentPosition = getArguments().getInt(POSITION);
        Log.e("LOL", String.valueOf(currentPosition));
        unbinder = ButterKnife.bind(this, view);
        tvTopText = getActivity().findViewById(R.id.tvYear);
        tvBottomText = getActivity().findViewById(R.id.tvDateInterval);
        tvMediumWeight = getActivity().findViewById(R.id.tvMediumWeight);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        daysPresenter.updateUI(currentPosition);
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
    }

    @Override
    public void updateUI(List<Weight> weightsForShow, String topText, String bottomText, String weekAverage) {
        setDays(weightsForShow);
        saveTexts(topText, bottomText, weekAverage);
    }

    private void saveTexts(String topText, String bottomText, String weekAverage) {
        this.topText = topText;
        this.bottomText = bottomText;
        this.weekAverage = weekAverage;
    }

    private void setDays(List<Weight> weightsForShow) {
        for (int i = 0; i < weightsForShow.size(); i++) {
            if (weightsForShow.get(i).getWeight() != ConfigMeasurment.EMPTY_DAY){
                daysViewsList.get(i).setText(String.valueOf(weightsForShow.get(i).getWeight()));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}