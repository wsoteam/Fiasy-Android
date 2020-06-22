package com.wsoteam.diet.presentation.diary

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wsoteam.diet.MainScreen.Controller.EatingAdapter
import com.wsoteam.diet.MainScreen.Controller.UpdateCallback
import com.wsoteam.diet.MainScreen.Fragments.FragmentEatingScroll
import com.wsoteam.diet.R
import com.wsoteam.diet.Sync.WorkWithFirebaseDB
import com.wsoteam.diet.ads.FiasyAds
import com.wsoteam.diet.ads.nativetemplates.NativeTemplateStyle
import com.wsoteam.diet.ads.nativetemplates.TemplateView
import com.wsoteam.diet.model.Eating
import com.wsoteam.diet.presentation.diary.DiaryViewModel.DiaryDay
import com.wsoteam.diet.presentation.diary.WidgetsAdapter.WidgetView
import com.wsoteam.diet.utils.Subscription
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class EatingsWidget(itemView: View) : WidgetView(itemView), UpdateCallback {
  private val container: RecyclerView = itemView.findViewById(R.id.container)
  private val emptyMeals = listOf<List<Eating>>(
      emptyList(),
      emptyList(),
      emptyList(),
      emptyList()
  )
  private val disposables = CompositeDisposable()
  private val dateChangeObserver = Observer<DiaryDay> {
    update()
  }

  private val eatingsObserver = Observer<Int> { id ->
    if ((id ?: -1) == WorkWithFirebaseDB.EATING_UPDATED) {
      update()
    }
  }
  private val nativeAd: TemplateView = itemView.findViewById(R.id.nativeAd)

  private val adObserver = Observer<Boolean> {
    Log.d("kkk", "adObserver+ $it")
    if (it)  nativeAd.visibility = View.VISIBLE
    else  nativeAd.visibility = View.GONE
  }


  init {
    container.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
  }

  override fun update() {
    val today = DiaryViewModel.currentDate

    val dateTitle = FragmentEatingScroll.setDateTitleV2(today.day, today.month, today.year)
    if (!Meals.hasMeals()) {
      container.adapter = EatingAdapter(emptyMeals, itemView.context, dateTitle, this)
    }

    disposables.clear()

    disposables.addAll(
        Meals.meals(DiaryViewModel.currentDate, false)
          .onErrorReturnItem(emptyList())
          .toList()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { meals ->
            if (meals.isEmpty()) {
              container.adapter = EatingAdapter(emptyMeals, itemView.context, dateTitle, this)
            } else {
              container.adapter = EatingAdapter(meals, itemView.context, dateTitle, this)
            }
          }
    )
  }

  private fun updateNativeAd(){

    val ad = FiasyAds.getLiveDataAdView().value

    if (ad != null && Subscription.check(context)) {
      nativeAd.visibility = View.VISIBLE
      nativeAd.setStyles(NativeTemplateStyle.Builder().build())
      nativeAd.setNativeAd(ad)
    } else{
      nativeAd.visibility = View.GONE
    }
  }

  override fun onBind(parent: RecyclerView, position: Int) {
    super.onBind(parent, position)
    updateNativeAd()
  }

  override fun onAttached(parent: RecyclerView) {
    super.onAttached(parent)

    DiaryViewModel.selectedDate.observeForever(dateChangeObserver)
    WorkWithFirebaseDB.liveUpdates().observeForever(eatingsObserver)
    FiasyAds.adStatus.observeForever(adObserver)

    update()
    updateNativeAd()
  }

  override fun onDetached(parent: RecyclerView) {
    super.onDetached(parent)
    DiaryViewModel.selectedDate.removeObserver(dateChangeObserver)
    WorkWithFirebaseDB.liveUpdates().removeObserver(eatingsObserver)
    FiasyAds.adStatus.removeObserver(adObserver)

    disposables.clear()
  }
}