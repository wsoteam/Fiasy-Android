package com.wsoteam.diet.presentation.promo;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.wsoteam.diet.R;
import com.wsoteam.diet.utils.IntentUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class PromoFormActivity extends MvpAppCompatActivity implements PromoFormView {
    @BindView(R.id.btnSendPromo) Button btnSendPromo;
    @BindView(R.id.edtPromo) EditText edtPromo;
    @BindView(R.id.tilPromo) TextInputLayout tilPromo;
    private PromoFormPresenter presenter;
    private boolean isError = false;

    @Override
    public void resultCheckedPromo(boolean isSuccess) {
        if (isSuccess){
            showSuccesDialog();
        }else {
            showError();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_form);
        ButterKnife.bind(this);

        presenter = new PromoFormPresenter(this);
        presenter.attachView(this);
        tilPromo.setError("Введите промокод");
        tilPromo.setErrorTextColor(getResources().getColorStateList(R.color.hint_promo));
    }

    @OnTextChanged(value = R.id.edtPromo, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void promoChanged(CharSequence text) {
        if (text.length() > 0) {
            onSendMode();
        } else {
            offSendMode();
        }
        if (isError) {
            hideError();
        }
    }

    private void onSendMode() {
        if (!btnSendPromo.isEnabled()) {
            btnSendPromo.setEnabled(true);
            btnSendPromo.setBackgroundTintList(getResources().getColorStateList(R.color.active_promo_btn));
        }
    }

    private void offSendMode() {
        if (btnSendPromo.isEnabled()) {
            btnSendPromo.setEnabled(false);
            btnSendPromo.setBackgroundTintList(getResources().getColorStateList(R.color.unactive_promo_btn));
        }
    }

    private void showError() {
        isError = true;
        tilPromo.setErrorEnabled(true);
        tilPromo.setErrorTextColor(getResources().getColorStateList(R.color.hint_error));
        tilPromo.setError("Промокод введен не верно");
    }

    private void hideError() {
        isError = false;
        tilPromo.setErrorTextColor(getResources().getColorStateList(R.color.hint_promo));
        tilPromo.setError("Введите промокод");
    }

    @OnClick({R.id.ibBack, R.id.btnSendPromo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ibBack:
                onBackPressed();
                break;
            case R.id.btnSendPromo:
                presenter.checkPromo(edtPromo.getText().toString());
                break;
        }
    }

    private void showSuccesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_promo_success, null);
        Button btnOk = view.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.openMainActivity(PromoFormActivity.this);
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.setView(view);
        alertDialog.show();
    }
}
