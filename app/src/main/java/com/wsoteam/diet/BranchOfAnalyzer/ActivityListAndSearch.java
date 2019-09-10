package com.wsoteam.diet.BranchOfAnalyzer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wsoteam.diet.BranchOfAnalyzer.Controller.TabsAdapter;
import com.wsoteam.diet.BranchOfAnalyzer.CustomFood.ActivityCreateFood;
import com.wsoteam.diet.BranchOfAnalyzer.Fragments.FragmentFavoriteContainer;
import com.wsoteam.diet.BranchOfAnalyzer.Fragments.FragmentSearch;
import com.wsoteam.diet.Config;
import com.wsoteam.diet.R;
import com.wsoteam.diet.Recipes.adding.AddingRecipeActivity;
import com.wsoteam.diet.Recipes.helper.FragmentRecipeContainer;
import com.wsoteam.diet.common.Analytics.EventProperties;
import com.wsoteam.diet.common.backward.OldFavoriteConverter;
import com.wsoteam.diet.presentation.food.template.browse.BrowseFoodTemplateFragment;
import com.wsoteam.diet.presentation.food.template.create.CreateFoodTemplateActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityListAndSearch extends AppCompatActivity {
    @BindView(R.id.spnEatingList)
    Spinner spinner;
    @BindView(R.id.edtActivityListAndSearchCollapsingSearchField)
    EditText edtSearchField;
    @BindView(R.id.ibStartAction)
    ImageButton ibStartAction;
    @BindView(R.id.searchFragmentContainer)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.ibActivityListAndSearchCollapsingCancelButton)
    ImageView ibSpeakAndClear;
    private TabsAdapter tabsAdapter;
    public int spinnerId = 0;
    private boolean isCanSpeak = true;

    List<Fragment> fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_and_search);
        ButterKnife.bind(this);
        bindSpinnerChoiceEating();
        updateUI();

        getSupportFragmentManager().beginTransaction().add(R.id.searchFragmentContainer, new FragmentSearch()).commit();
        edtSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeSpeakButton(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ((TabsFragment) tabsAdapter.getItem(viewPager.getCurrentItem())).
                            sendString(edtSearchField.getText().toString().replaceAll("\\s+", " "));
                    edtSearchField.clearFocus();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (edtSearchField.getText().toString().length() != 0) {
                    edtSearchField.setText("");
                }
                tabs.getTabAt(i).select();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    private void changeSpeakButton(CharSequence charSequence) {
        if (charSequence.length() > 0 && isCanSpeak) {
            isCanSpeak = false;
            Glide.with(this).load(R.drawable.ic_cancel).into(ibSpeakAndClear);
        } else if (charSequence.length() == 0 && !isCanSpeak) {
            isCanSpeak = true;
            Glide.with(this).load(R.drawable.ic_speak).into(ibSpeakAndClear);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        edtSearchField.clearFocus();
        if (fragments.get(viewPager.getCurrentItem()) instanceof BrowseFoodTemplateFragment) {
            ((BrowseFoodTemplateFragment) fragments.get(viewPager.getCurrentItem()))
                    .setUserVisibleHint(true);
        }

    }

    private void updateUI() {
        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), createFragmentsList());
        viewPager.setAdapter(tabsAdapter);
    }

    private List<Fragment> createFragmentsList() {
        fragments = new ArrayList<>();
        fragments.add(new FragmentSearch());
        fragments.add(new FragmentFavoriteContainer());
        fragments.add(new BrowseFoodTemplateFragment());
        fragments.add(new FragmentRecipeContainer());
        return fragments;
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // намерение для вызова формы обработки речи (ОР)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); // сюда он слушает и запоминает
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите!");
        startActivityForResult(intent, 1234); // вызываем активность ОР
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> commandList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            edtSearchField.setText(commandList.get(0));
            ((TabsFragment) tabsAdapter.getItem(viewPager.getCurrentItem())).
                    sendString(edtSearchField.getText().toString().replaceAll("\\s+", " "));
            edtSearchField.clearFocus();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void bindSpinnerChoiceEating() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                R.layout.item_spinner_food_search, getResources().getStringArray(R.array.eatingList));
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown_food_search);
        spinner.setAdapter(adapter);
        spinner.setSelection(getIntent().getIntExtra(Config.TAG_CHOISE_EATING, 0));
        //spinnerId = spinner.getSelectedItemPosition();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @OnClick({R.id.ibActivityListAndSearchCollapsingCancelButton, R.id.ivBack, R.id.ibStartAction})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ibActivityListAndSearchCollapsingCancelButton:
                if (isCanSpeak) {
                    speak();
                } else {
                    edtSearchField.setText("");
                    ((TabsFragment) tabsAdapter.getItem(viewPager.getCurrentItem())).
                            sendClearSearchField();
                    edtSearchField.clearFocus();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ibStartAction:
                createPopUp();
                break;
        }
    }

    private void createPopUp() {
        PopupMenu popupMenu = new PopupMenu(this, ibStartAction);
        popupMenu.inflate(R.menu.popup_search_food);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    /*case R.id.userNote:
                        break;*/
                    case R.id.createFood:
                        startActivity(new Intent(ActivityListAndSearch.this, ActivityCreateFood.class).putExtra(EventProperties.product_from, EventProperties.product_from_plus));
                        break;
                    /*case R.id.createEating:
                        break;*/
                    case R.id.createTemplate:
                        startActivity(new Intent(ActivityListAndSearch.this, CreateFoodTemplateActivity.class)
                                .putExtra(Config.EATING_SPINNER_POSITION, (int) spinner.getSelectedItemId())
                                .putExtra(EventProperties.template_from, EventProperties.template_from_plus));
                        break;
                    case R.id.createRecipe:
                        startActivity(new Intent(ActivityListAndSearch.this, AddingRecipeActivity.class).putExtra(EventProperties.recipe_from, EventProperties.recipe_from_button));
                        break;
                }
                return false;
            }
        });
    }

}

