package com.wsoteam.diet.presentation.profile.norm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.wsoteam.diet.AmplitudaEvents;
import com.wsoteam.diet.Authenticate.POJO.Box;
import com.wsoteam.diet.InApp.ActivitySubscription;
import com.wsoteam.diet.POJOProfile.Profile;
import com.wsoteam.diet.R;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.Analytics.Events;
import com.wsoteam.diet.presentation.profile.norm.choise.activity.ActivActivity;
import com.wsoteam.diet.presentation.profile.norm.choise.goal.GoalActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ChangeNormActivity extends MvpAppCompatActivity implements ChangeNormView {

    @BindView(R.id.edtHeight) EditText edtHeight;
    @BindView(R.id.tilHeight) TextInputLayout tilHeight;
    @BindView(R.id.edtWeight) EditText edtWeight;
    @BindView(R.id.tilWeight) TextInputLayout tilWeight;
    @BindView(R.id.edtAge) EditText edtAge;
    @BindView(R.id.tilAge) TextInputLayout tilAge;
    @BindView(R.id.edtSex) EditText edtSex;
    @BindView(R.id.edtActivity) EditText edtActivity;
    @BindView(R.id.edtGoal) EditText edtGoal;
    private ChangeNormPresenter presenter;

    @BindView(R.id.tvPropPremium) TextView tvPropPremium;
    @BindView(R.id.tvClickablePrem) TextView tvClickablePrem;
    @BindView(R.id.edtKcal) EditText edtKcal;
    @BindView(R.id.edtProt) EditText edtProt;
    @BindView(R.id.edtCarbo) EditText edtCarbo;
    @BindView(R.id.edtFats) EditText edtFats;
    @BindView(R.id.tvFormulaHint) TextView tvFormulaHint;
    @BindView(R.id.btnReturnParametrs) Button btnReturnParametrs;

    @BindView(R.id.tilKcal) TextInputLayout tilKcal;
    @BindView(R.id.tilProt) TextInputLayout tilProt;
    @BindView(R.id.tilCarbo) TextInputLayout tilCarbo;
    @BindView(R.id.tilFats) TextInputLayout tilFats;
    private boolean isPremUser = false;

    private final double PROTEIN_COUNT = 0.1, FAT_COUNT = 0.027, CARBO_COUNT = 0.0875;
    private TextWatcher kcalTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().equals("-")) {
                edtKcal.setText("");
            } else if (charSequence.toString().equals("")) {
                recountMainParams(0);
            } else {
                recountMainParams(Integer.parseInt(charSequence.toString()));
            }

            if (tvFormulaHint.getVisibility() == View.VISIBLE) {
                setDropModeOn();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher protTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            removeError(tilProt);
            if (tvFormulaHint.getVisibility() == View.VISIBLE) {
                setDropModeOn();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher carboTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            removeError(tilCarbo);
            if (tvFormulaHint.getVisibility() == View.VISIBLE) {
                setDropModeOn();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher fatTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            removeError(tilFats);
            if (tvFormulaHint.getVisibility() == View.VISIBLE) {
                setDropModeOn();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };




    @Override
    public void setDefaultPremParams(String kcal, String fat, String carbo, String c) {
        edtKcal.removeTextChangedListener(kcalTW);
        removeOtherParamsListener();
        edtKcal.setText(kcal);
        edtFats.setText(fat);
        edtCarbo.setText(carbo);
        edtProt.setText(carbo);
        edtKcal.addTextChangedListener(kcalTW);
        addOtherParamsListener();
    }

    @Override
    public void bindFields(Profile profile, String goal, String activity) {
        edtAge.setText(String.valueOf(profile.getAge()));
        edtWeight.setText(String.valueOf(profile.getWeight()));
        edtHeight.setText(String.valueOf(profile.getHeight()));
        if (profile.isFemale()) {
            edtSex.setText(getResources().getString(R.string.profile_female));
        } else {
            getResources().getString(R.string.profile_male);
        }
        edtActivity.setText(activity);
        edtGoal.setText(goal);
        edtKcal.setText(String.valueOf(profile.getMaxKcal()));
        edtCarbo.setText(String.valueOf(profile.getMaxCarbo()));
        edtFats.setText(String.valueOf(profile.getMaxFat()));
        edtProt.setText(String.valueOf(profile.getMaxProt()));
        bindPremiumUI();
        if (!presenter.isDefaultParams()) {
            setDropModeOn();
        }
        addOtherParamsListener();
        edtKcal.addTextChangedListener(kcalTW);

    }

    private void addOtherParamsListener() {
        edtFats.addTextChangedListener(fatTW);
        edtCarbo.addTextChangedListener(carboTW);
        edtProt.addTextChangedListener(protTW);
    }

    private void removeOtherParamsListener() {
        edtFats.removeTextChangedListener(fatTW);
        edtCarbo.removeTextChangedListener(carboTW);
        edtProt.removeTextChangedListener(protTW);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_norm);
        ButterKnife.bind(this);

        presenter = new ChangeNormPresenter(this);
        presenter.attachView(this);
    }

    private void bindPremiumUI() {
        isPremUser = getSharedPreferences(com.wsoteam.diet.Config.STATE_BILLING, MODE_PRIVATE)
                .getBoolean(com.wsoteam.diet.Config.STATE_BILLING, false);
        if (isPremUser) {
            tvPropPremium.setVisibility(View.GONE);
            tvClickablePrem.setVisibility(View.GONE);
            edtKcal.setEnabled(true);
            edtProt.setEnabled(true);
            edtCarbo.setEnabled(true);
            edtFats.setEnabled(true);
        } else {
            tvPropPremium.setVisibility(View.VISIBLE);
            tvClickablePrem.setVisibility(View.VISIBLE);
            edtKcal.setEnabled(false);
            edtProt.setEnabled(false);
            edtCarbo.setEnabled(false);
            edtFats.setEnabled(false);
        }
    }

    @OnTextChanged(value = R.id.edtHeight, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void heightChanged(CharSequence text) {
        removeError(tilHeight);
    }

    @OnTextChanged(value = R.id.edtWeight, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void weightChanged(CharSequence text) {
        removeError(tilWeight);
    }

    @OnTextChanged(value = R.id.edtAge, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void ageChanged(CharSequence text) {
        removeError(tilAge);
    }

    private void setDropModeOn() {
        tvFormulaHint.setVisibility(View.GONE);
        btnReturnParametrs.setBackgroundTintList(this.getResources().getColorStateList(R.color.active_drop));
        btnReturnParametrs.setEnabled(true);
    }

    private void setDropModeOff() {
        tvFormulaHint.setVisibility(View.VISIBLE);
        btnReturnParametrs.setBackgroundTintList(this.getResources().getColorStateList(R.color.inactive_drop));
        btnReturnParametrs.setEnabled(false);
    }

    private void recountMainParams(int parseInt) {
        double fat = parseInt * FAT_COUNT;
        double prot = parseInt * PROTEIN_COUNT;
        double carbo = parseInt * CARBO_COUNT;

        edtProt.setText(String.valueOf(Math.round(prot)));
        edtFats.setText(String.valueOf(Math.round(fat)));
        edtCarbo.setText(String.valueOf(Math.round(carbo)));
    }

    private void removeError(TextInputLayout currentTextInputLayout) {
        if (currentTextInputLayout.getError() != null) {
            currentTextInputLayout.setError("");
        }
    }

    @OnClick({R.id.ibSave, R.id.ibBack, R.id.edtSex, R.id.edtActivity, R.id.edtGoal, R.id.tvPropPremium, R.id.tvClickablePrem, R.id.btnReturnParametrs})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.ibSave:
                if (isNoErrorInMainParams() && isNoErrorInPremParams()) {
                    if (isPremUser && isDropModeOn()) {
                        presenter.onlySave(edtHeight.getText().toString(), edtWeight.getText().toString(),
                                edtAge.getText().toString(), edtSex.getText().toString(), edtActivity.getText().toString(),
                                edtGoal.getText().toString(), edtKcal.getText().toString(), edtProt.getText().toString(),
                                edtCarbo.getText().toString(), edtFats.getText().toString());
                    } else {
                        presenter.calculateAndSave(edtHeight.getText().toString(), edtWeight.getText().toString(),
                                edtAge.getText().toString(), edtSex.getText().toString(), edtActivity.getText().toString(), edtGoal.getText().toString());
                    }
                    Toast.makeText(this, R.string.profile_saved, Toast.LENGTH_SHORT).show();
                    Events.logChangeGoal();
                    finish();
                }
                break;
            case R.id.ibBack:
                onBackPressed();
                break;
            case R.id.edtSex:
                break;
            case R.id.edtActivity:
                hideKeayboard();
                startActivityForResult(new Intent(this, ActivActivity.class).putExtra(Config.ACTIVITY, edtActivity.getText().toString()), Config.ACTIVITY_CHANGE);
                break;
            case R.id.edtGoal:
                hideKeayboard();
                startActivityForResult(new Intent(this, GoalActivity.class).putExtra(Config.GOAL, edtGoal.getText().toString()), Config.GOAL_CHANGE);
                break;
            case R.id.tvPropPremium:
            case R.id.tvClickablePrem:
                openPrem();
                break;
            case R.id.btnReturnParametrs:
                setDefaultPremParameters();
                break;
        }
    }

    private void hideKeayboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(edtActivity.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean isDropModeOn() {
        return btnReturnParametrs.isEnabled() && tvFormulaHint.getVisibility() == View.GONE;
    }

    private void setDefaultPremParameters() {
        setDropModeOff();
        presenter.dropParams();
    }

    private void openPrem() {
        Box box = new Box();
        box.setComeFrom(AmplitudaEvents.view_prem_settings);
        box.setBuyFrom(EventProperties.trial_from_norm);
        box.setOpenFromPremPart(true);
        box.setOpenFromIntrodaction(false);
        startActivity(new Intent(this, ActivitySubscription.class).putExtra(com.wsoteam.diet.Config.TAG_BOX, box));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == Config.GOAL_CHANGE) {
            presenter.convertAndSetGoal(data.getIntExtra(Config.GOAL_CHANGE_RESULT, 0));
        } else if (data != null && requestCode == Config.ACTIVITY_CHANGE) {
            presenter.convertAndSetActivity(data.getIntExtra(Config.ACTIVITY_CHANGE_RESULT, 0));
        } else {
            Log.e("LOL", "error");
        }
    }

    @Override
    public void setGoal(String goal) {
        edtGoal.setText(goal);
    }

    @Override
    public void setActivity(String activity) {
        edtActivity.setText(activity);
    }

    private boolean isNoErrorInMainParams() {
        if (!edtAge.getText().toString().equals("")
                && Integer.parseInt(edtAge.getText().toString()) >= 9
                && Integer.parseInt(edtAge.getText().toString()) <= 200) {
            if (!edtHeight.getText().toString().equals("")
                    && Integer.parseInt(edtHeight.getText().toString()) >= 100
                    && Integer.parseInt(edtHeight.getText().toString()) <= 300) {
                if (!edtWeight.getText().toString().equals("")
                        && Double.parseDouble(edtWeight.getText().toString()) >= 30
                        && Double.parseDouble(edtWeight.getText().toString()) <= 300) {
                    return true;
                } else {
                    tilWeight.setError(getString(R.string.spk_check_weight));
                    return false;
                }
            } else {
                tilHeight.setError(getString(R.string.spk_check_your_height));
                return false;
            }
        } else {
            tilAge.setError(getString(R.string.spk_check_your_age));
            return false;
        }
    }

    private boolean isNoErrorInPremParams() {
        if (!edtKcal.getText().toString().equals("")) {
            if (!edtProt.getText().toString().equals("") && !edtProt.getText().toString().contains("-")) {
                if (!edtCarbo.getText().toString().equals("") && !edtCarbo.getText().toString().contains("-")) {
                    if (!edtFats.getText().toString().equals("") && !edtFats.getText().toString().contains("-")) {
                        return true;
                    } else {
                        tilFats.setError(getString(R.string.enter_multi_params));
                        return false;
                    }
                } else {
                    tilCarbo.setError(getString(R.string.enter_multi_params));
                    return false;
                }
            } else {
                tilProt.setError(getString(R.string.enter_multi_params));
                return false;
            }
        } else {
            tilKcal.setError(getResources().getString(R.string.enter_multi_params));
            return false;
        }
    }
}
