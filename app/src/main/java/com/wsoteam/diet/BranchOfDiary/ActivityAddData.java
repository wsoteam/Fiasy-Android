package com.wsoteam.diet.BranchOfDiary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.wsoteam.diet.POJOForDB.DiaryData;
import com.wsoteam.diet.R;
import com.yandex.metrica.YandexMetrica;

import java.util.ArrayList;

public class ActivityAddData extends AppCompatActivity {
    private FloatingActionButton fabSaveData;
    private EditText edtWeight, edtChest, edtWaist, edtHips, edtNote;
    private DiaryData diaryData;
    private DatePicker datePicker;
    private int maxDay, maxMonth, maxYear;
    private boolean isReadyToClose = false;
    InterstitialAd interstitialAd;

    //TODO add main icon and split input form on four square

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        fabSaveData = findViewById(R.id.fabSaveInputDataDiary);
        edtWeight = findViewById(R.id.edtInputDataDiaryWeight);
        edtChest = findViewById(R.id.edtInputDataDiaryChest);
        edtWaist = findViewById(R.id.edtInputDataDiaryWaist);
        edtHips = findViewById(R.id.edtInputDataDiaryHips);
        datePicker = findViewById(R.id.datePicker);
        diaryData = new DiaryData();

        maxDay = datePicker.getDayOfMonth();
        maxMonth = datePicker.getMonth();
        maxYear = datePicker.getYear();


        fabSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getDate()) {
                    getWeight();
                    getOtherData();
                    saveInToDB();
                    if (isReadyToClose) {
                        if (interstitialAd.isLoaded()) {
                            interstitialAd.show();
                        }
                        finish();
                    }
                }
            }
        });

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        YandexMetrica.reportEvent("Открыт экран: Добавление данных в дневник");

    }

    private void saveInToDB() {
        ArrayList<DiaryData> diaryDataArrayList = (ArrayList<DiaryData>) DiaryData.listAll(DiaryData.class);
        for (int i = 0; i < diaryDataArrayList.size(); i++) {
            if (diaryDataArrayList.get(i).getOwnId() == diaryData.getOwnId()) {
                diaryDataArrayList.remove(i);
            }
        }
        diaryDataArrayList.add(diaryData);
        DiaryData[] arrayForWrite = new DiaryData[diaryDataArrayList.size()];
        for (int i = 0; i < diaryDataArrayList.size(); i++) {
            arrayForWrite[i] = diaryDataArrayList.get(i);
        }

        DiaryData.deleteAll(DiaryData.class);

        if (diaryDataArrayList.size() == 1) {
            diaryData.save();
        } else {
            int lenght = arrayForWrite.length;
            for (int i = 0; i < lenght; i++) {
                for (int j = 0; j < lenght - i - 1; j++) {
                    if (arrayForWrite[j].getOwnId() < arrayForWrite[j + 1].getOwnId()) {
                        DiaryData temp = arrayForWrite[j];
                        arrayForWrite[j] = arrayForWrite[j + 1];
                        arrayForWrite[j + 1] = temp;
                    }
                }
            }


            for (int i = 0; i < arrayForWrite.length; i++) {
                DiaryData diaryData = arrayForWrite[i];
                diaryData.save();
                diaryData = null;
            }

        }
    }

    private void getOtherData() {
        if (!edtChest.getText().toString().equals("")) {
            diaryData.setChest(Integer.parseInt(edtChest.getText().toString()));
        }
        if (!edtWaist.getText().toString().equals("")) {
            diaryData.setWaist(Integer.parseInt(edtWaist.getText().toString()));
        }
        if (!edtHips.getText().toString().equals("")) {
            diaryData.setHips(Integer.parseInt(edtHips.getText().toString()));
        }
        //diaryData.setNote(edtNote.getText().toString());
    }

    private boolean getDate() {
        if (maxDay < datePicker.getDayOfMonth() && maxMonth <= datePicker.getMonth() && maxYear <= datePicker.getYear()) {
            Toast.makeText(this, "Неправильно введена дата", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            int weightOfDate = datePicker.getDayOfMonth() + datePicker.getMonth() * 100 + datePicker.getYear() * 2000;
            diaryData.setNumberOfDay(datePicker.getDayOfMonth());
            diaryData.setMonth(datePicker.getMonth());
            diaryData.setNameOfMonth(datePicker.getMonth());
            diaryData.setYear(datePicker.getYear());
            diaryData.setOwnId(weightOfDate);
            return true;
        }
    }

    private void getWeight() {
        if (edtWeight.getText().toString().equals("")) {
            Toast.makeText(this, "Введите пожалуйста Ваш вес", Toast.LENGTH_SHORT).show();
        } else {
            diaryData.setWeight(Double.parseDouble(edtWeight.getText().toString()));
            isReadyToClose = true;
        }
    }
}
