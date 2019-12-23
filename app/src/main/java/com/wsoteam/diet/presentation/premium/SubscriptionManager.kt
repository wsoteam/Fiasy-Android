package com.wsoteam.diet.presentation.premium

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustEvent
import com.amplitude.api.Amplitude
import com.amplitude.api.Revenue
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.wsoteam.diet.App
import com.wsoteam.diet.BuildConfig
import com.wsoteam.diet.Config
import com.wsoteam.diet.EventsAdjust
import com.wsoteam.diet.InApp.Fragments.FragmentA
import com.wsoteam.diet.InApp.properties.CheckAndSetPurchase
import com.wsoteam.diet.InApp.properties.SingletonMakePurchase
import com.wsoteam.diet.common.Analytics.EventProperties
import com.wsoteam.diet.common.Analytics.Events
import com.wsoteam.diet.common.Analytics.UserProperty
import com.wsoteam.diet.utils.AbTests
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import org.json.JSONObject
import java.util.concurrent.CopyOnWriteArrayList

object SubscriptionManager {

  private var source: String = "ordinary"
  private val purchases = CopyOnWriteArrayList<SkuDetails>()
  private val purchaseListener = PurchasesUpdatedListener { responseCode, purchases ->
    Log.d("PurchaseUpdated", "responseCode=$responseCode, purchases=$purchases")

    if (responseCode != BillingClient.BillingResponse.OK) {
      Events.logBillingError(responseCode)
    }

    if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
      //send data about purchase into firebase (and save into profile subInfo)
      SingletonMakePurchase.getInstance().isMakePurchaseNow = true
      val p = purchases[0]

      if (BuildConfig.DEBUG) {
        Log.d("Fiasy", "Purchased, $p")
      }

      CheckAndSetPurchase(App.instance).execute(p.getSku(),
          p.getPurchaseToken(),
          p.getPackageName(),
          FragmentA.BUY_NOW)

      Adjust.trackEvent(AdjustEvent(EventsAdjust.buy_trial))

      try {
        if (p.isAutoRenewing()) {
          Events.logBuy("new_premium", EventProperties.auto_renewal_true)
        } else {
          Events.logBuy("new_premium", EventProperties.auto_renewal_false)
        }
      } catch (ex: Exception) {
        Events.logSetBuyError(ex.message)
      }

      val purchase = SubscriptionManager.purchases.find { it.sku == p.sku }
      val appEventsLogger = AppEventsLogger.newLogger(App.instance)
      val params = Bundle()
      params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY,
          purchase?.priceCurrencyCode ?: "RUB")
      appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_START_TRIAL,
          purchase?.price?.toDoubleOrNull() ?: 0.0, params)

      purchase?.let {
        UserProperty.setPremStatus(UserProperty.buy)
        UserProperty.setPremState(purchase.price, purchase.sku)

        val r = Revenue()
        r.setPrice(purchase.price.toDoubleOrNull() ?: 0.0)
        r.setProductId(purchase.sku)
        r.setEventProperties(JSONObject().apply {
          put("buy_from", source)
          put("abtest", if (AbTests.enableTrials()) "black_trial" else "black_direct")
        })
        r.setQuantity(1)
        r.setRevenueType("subscription")
        r.setReceipt(p.orderId, p.signature)

        Amplitude.getInstance().logRevenueV2(r)
      }

      App.instance.getSharedPreferences(Config.STATE_BILLING, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(Config.STATE_BILLING, true)
        .apply()

      App.instance.getSharedPreferences(Config.ALERT_BUY_SUBSCRIPTION, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(Config.ALERT_BUY_SUBSCRIPTION, true)
        .apply()

      purchase?.let {
        SubscriptionManager.purchases.remove(it)
        stream.offer(it)
      }
    }
  }

  private val stream = PublishProcessor.create<SkuDetails>()

  private val billingClient = BillingClient.newBuilder(App.instance)
    .setListener(purchaseListener)
    .build()

  fun buy(activity: Activity, subscriptionKey: String): Single<Int> {
    if(activity.intent.getBooleanExtra("fromDiary", false)){
      source = "header"
    } else if(activity.intent.hasExtra("source")){
      source = activity.intent.getStringExtra("source") ?: "ordinary"
    }

    return Single.create(ConnectBilling())
      .flatMap { Single.create(QuerySubscriptionSingle(subscriptionKey)) }
      .observeOn(AndroidSchedulers.mainThread())
      .map { subscription ->
        billingClient.launchBillingFlow(activity, BillingFlowParams.newBuilder()
          .setSkuDetails(subscription)
          .build())
      }
  }

  fun purchases(): Flowable<SkuDetails> {
    return stream.share()
  }

  class ConnectBilling : SingleOnSubscribe<Int> {
    override fun subscribe(emitter: SingleEmitter<Int>) {
      if (billingClient.isReady) {
        emitter.onSuccess(BillingClient.BillingResponse.OK)
        return;
      }

      billingClient.startConnection(object : BillingClientStateListener {
        override fun onBillingServiceDisconnected() {
          emitter.tryOnError(DisconnectedException())
        }

        override fun onBillingSetupFinished(responseCode: Int) {
          if (responseCode == BillingClient.BillingResponse.OK) {
            if (BuildConfig.DEBUG) {
              Log.d("Billing", "setup finished, ok")
            }

            if (!emitter.isDisposed) {
              emitter.onSuccess(responseCode)
            }

          } else {
            if (BuildConfig.DEBUG) {
              Log.d("Billing", "setup finished, error=$responseCode")
            }

            emitter.tryOnError(SetupFailedException())
          }
        }
      })
    }
  }

  class QuerySubscriptionSingle(val subscriptionId: String)
    : SingleOnSubscribe<SkuDetails> {
    override fun subscribe(emitter: SingleEmitter<SkuDetails>) {
      val params = SkuDetailsParams.newBuilder()
        .setSkusList(arrayListOf(subscriptionId))
        .setType(SkuType.SUBS)
        .build()

      billingClient.querySkuDetailsAsync(params) { code, details ->
        if (BuildConfig.DEBUG) {
          Log.d("Billing", "query finished, response=$code")
        }

        val subscription = details.find { it.sku == subscriptionId }

        if (subscription != null) {
          if (!emitter.isDisposed) {
            purchases.add(subscription)
            emitter.onSuccess(subscription)
          }
        } else {
          emitter.tryOnError(SubscriptionNotFoundException())
        }
      }
    }
  }

  open class BillingException() : Exception()

  class SubscriptionNotFoundException() : BillingException()
  class SetupFailedException() : BillingException()
  class DisconnectedException() : BillingException()
}
