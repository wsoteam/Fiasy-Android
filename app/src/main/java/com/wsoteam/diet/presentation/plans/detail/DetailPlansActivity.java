package com.wsoteam.diet.presentation.plans.detail;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.wsoteam.diet.R;
import com.wsoteam.diet.di.CiceroneModule;
import com.wsoteam.diet.presentation.global.BaseActivity;
import ru.terrakok.cicerone.Router;

public class DetailPlansActivity extends BaseActivity implements DetailPlansView {

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.recycler) RecyclerView recycler;
  @BindView(R.id.btnJoin) Button btnJoin;


  Router router = CiceroneModule.router();

  private MenuItem leaveMenu;
  private AlertDialog alertDialogChangePlan;

  @InjectPresenter
  DetailPlansPresenter presenter;
  View.OnClickListener navigationListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      presenter.clickedClose();
    }
  };
  MenuItem.OnMenuItemClickListener menuListener = new MenuItem.OnMenuItemClickListener() {
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

      switch (menuItem.getItemId()) {
        case R.id.mLeave: {
          presenter.clickedLeave();
          return true;
        }
        case R.id.mShare:{
          presenter.clickedShare();
          return true;
        }
        default:
          return false;
      }
    }
  };

  @ProvidePresenter
  DetailPlansPresenter providePresenter() {
    return new DetailPlansPresenter(router, getIntent(), this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail_plans);
    ButterKnife.bind(this);

    getWindow().getDecorView().setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    getWindow().setStatusBarColor(Color.parseColor("#32000000"));

    toolbar.inflateMenu(R.menu.diet_plans_menu);
    toolbar.setNavigationOnClickListener(navigationListener);
    toolbar.setOverflowIcon(getDrawable(R.drawable.ic_more));

    Menu menu = toolbar.getMenu();
    MenuItem shareMenu = menu.findItem(R.id.mShare);
    leaveMenu = menu.findItem(R.id.mLeave);
    invalidateOptionsMenu();

    shareMenu.setOnMenuItemClickListener(menuListener);
    leaveMenu.setOnMenuItemClickListener(menuListener);

    recycler.setLayoutManager(new LinearLayoutManager(this));
  }

  @OnClick({ R.id.btnJoin })
  void onClicked() {
    presenter.clickedJoin();
  }

  @Override
  public void showProgress(boolean show) {

  }

  @Override
  public void showMessage(String message) {

  }

  @Override
  public void setAdapter(RecyclerView.Adapter adapter) {
    recycler.setAdapter(adapter);
  }

  @Override public void visibilityButtonJoin(boolean value) {
    btnJoin.setVisibility(value ? View.VISIBLE : View.GONE);
    leaveMenu.setVisible(!value);
  }

  @Override public void visibilityButtonLeave(boolean value) {
    leaveMenu.setVisible(value);
  }

  @Override public void startAlert(String plan) {

    alertDialogChangePlan = AlertDialogs.changePlan(this, (View v) -> {
          presenter.joinPlans();
          alertDialogChangePlan.dismiss();
        }
        , plan);

    alertDialogChangePlan.show();
  }

  @Override public void showAlertJoinToPlan() {
    AlertDialogs.planJoined(this, 2000).show();
  }

  @Override public void sharePlan(String str) {
    Intent i = new Intent(
        android.content.Intent.ACTION_SEND);
    i.setType("text/plain");
    i.putExtra(android.content.Intent.EXTRA_TEXT, str);
    startActivity(
        Intent.createChooser(i, getResources().getString(R.string.titleShareDialogPlan)));
  }

  @Override protected void onResume() {
    presenter.onResume();
    super.onResume();
  }
}
