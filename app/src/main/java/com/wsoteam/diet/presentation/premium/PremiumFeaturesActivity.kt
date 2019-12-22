package com.wsoteam.diet.presentation.premium

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.MarginLayoutParams
import android.view.Window
import android.widget.ImageView
import android.widget.ImageView.ScaleType.CENTER_INSIDE
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.amplitude.api.Amplitude
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wsoteam.diet.MainScreen.DeepLink
import com.wsoteam.diet.R
import com.wsoteam.diet.R.string
import com.wsoteam.diet.common.Analytics.Events
import com.wsoteam.diet.presentation.premium.SubscriptionManager.SetupFailedException
import com.wsoteam.diet.presentation.premium.SubscriptionManager.SubscriptionNotFoundException
import com.wsoteam.diet.utils.AbTests
import com.wsoteam.diet.utils.IntentUtils
import com.wsoteam.diet.utils.RichTextUtils.RichText
import com.wsoteam.diet.utils.RichTextUtils.strikethrough
import com.wsoteam.diet.utils.asCurrency
import com.wsoteam.diet.utils.dp
import com.wsoteam.diet.views.DotIndicatorView
import com.wsoteam.diet.views.PremiumPlanCardView
import com.wsoteam.diet.views.PremiumPlanCardView2
import com.wsoteam.diet.views.TimerView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions.emptyConsumer
import kotlinx.android.synthetic.main.activity_premium_dark.*
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class PremiumFeaturesActivity : AppCompatActivity() {
  companion object {

    val subscriptionsMap = hashMapOf(
        "premium_year" to "trial_premium_year",
        "premium_6month" to "trial_premium_6month"
    )

    val localizedPlans = mapOf(
        "ru" to arrayOf(
            PremiumPlan(key = "premium_year",
                price = 949.0,
                trial = 7,
                duration = R.string.premium_subscription_1_year,
                diamondStyleId = R.drawable.ic_blue_diamond,
                monthlyPrice = 79.0,
                benefitPercentage = 75,
                benefitTintColor = Color.WHITE,
                benefitTextColor = Color.BLACK,
                prevMonthlyPrice = 316.0
            ),

            PremiumPlan(key = "premium_6month",
                price = 1450.0,
                trial = 3,
                duration = R.string.premium_subscription_6_month,
                diamondStyleId = R.drawable.ic_gold_diamond,
                monthlyPrice = 241.0,
                benefitPercentage = 24,
                benefitTintColor = 0xFFEFB476.toInt(),
                benefitTextColor = Color.WHITE,
                prevMonthlyPrice = 316.0
            ),

            PremiumPlan(key = "premium_3month",
                price = 949.0,
                trial = 0,
                duration = R.string.premium_subscription_3_month,
                diamondStyleId = R.drawable.ic_silver_diamond,
                monthlyPrice = 316.0,
                benefitPercentage = 0,
                benefitTintColor = 0xFFEFB476.toInt(),
                benefitTextColor = Color.WHITE,
                prevMonthlyPrice = -1.0
            )
        )
    )

    val plans: Array<PremiumPlan>
      get() {
        return localizedPlans.get(Locale.getDefault().language)
            ?: localizedPlans.getValue("en")
      }
  }

  private val disposables = CompositeDisposable()
  private var purchasedId: String? = null

  internal val source: String
    get() = intent.getStringExtra("source") ?: "profile"

   val isDarkTheme = false

  internal val withTrial: Boolean
    get() = AbTests.enableTrials();

  public override fun onCreate(savedInstanceState: Bundle?) {
    supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

    if (isDarkTheme) {
      setTheme(R.style.AppTheme_Premium)
      window.setBackgroundDrawableResource(R.color.premium_background_color_dark)
    } else {
      setTheme(R.style.AppTheme_Premium_Light)
      window.setBackgroundDrawableResource(R.color.premium_background_color_light)
    }

    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_premium_dark)

    Amplitude.getInstance().logEvent("new_premium_open", JSONObject().apply {
      optString("abtest",
          if (withTrial)
            "black_trial"
          else
            "black_direct")
    })

//    if (DeepLink.isNeedPrem.get()) {
//      toolbar.navigationIcon = null
//      DeepLink.isNeedPrem.set(false)
//    }

    findViewById<TextView>(R.id.privacy_policy).apply {
      movementMethod = LinkMovementMethod.getInstance()
      text = RichText(text)
        .onClick(View.OnClickListener { v ->
          Events.logPushButton("privacy", source)

          IntentUtils.openWebLink(v.context,
              getString(R.string.url_privacy_police))
        })
        .text()
    }

    val timerLabel = findViewById<View>(R.id.timer_label)
    findViewById<TimerView>(R.id.timer).apply {
      val endDate = FirebaseRemoteConfig.getInstance().getLong("premium_timer_date")

      val diff = System.currentTimeMillis() - endDate

      if (System.currentTimeMillis() > endDate || TimeUnit.DAYS.toMillis(3) >= diff) {
        visibility = View.GONE
        timerLabel.visibility = View.GONE
      }

      setupTimeout(endDate)
    }

    findViewById<TextView>(R.id.title).apply {
      val fullLabel = getString(R.string.premium_feature_title)
      val spannedLabel = RichText(getString(R.string.premium_feature_title__span))
        .bold()
        .colorRes(context, R.color.orange_premium)
        .textScale(1.4f)
        .text()

      text = TextUtils.concat(fullLabel, spannedLabel)
    }

    disposables.add(SubscriptionManager.purchases()
      .filter { it.sku == purchasedId }
      .doOnNext {
        finish()

        IntentUtils.openMainActivity(this)
      }
      .subscribe(emptyConsumer(), emptyConsumer())
    )

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    toolbar.setNavigationOnClickListener {
      Events.logPushButton("close", source)

      finish()
    }

    val buyButton = findViewById<TextView>(R.id.action_buy)
    val tariffsContainer = findViewById<RecyclerView>(R.id.tarifs)

    buyButton.setOnClickListener {
      val lm = tariffsContainer.layoutManager as LinearLayoutManager
      val id = lm.findFirstCompletelyVisibleItemPosition()

      purchase(plans[id].key)
    }

    tariffsContainer.adapter = PlansAdapter(isDarkMode = isDarkTheme, withTrial = withTrial)
    tariffsContainer.addOnScrollListener(object : OnScrollListener() {
      override fun onScrollStateChanged(p: RecyclerView, newState: Int) {
        super.onScrollStateChanged(p, newState)

        val lm = p.layoutManager as LinearLayoutManager

        val color: ColorStateList?
        val textColor: Int

        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
            lm.findFirstCompletelyVisibleItemPosition() == 0) {
          color = ContextCompat.getColorStateList(p.context, R.color.premium_button_buy_gold_color)
          textColor = Color.WHITE
        } else {
          color =
            ContextCompat.getColorStateList(p.context, R.color.premium_button_buy_ordinary_color)
          textColor = Color.BLACK
        }

        buyButton.setTextColor(textColor)
        ViewCompat.setBackgroundTintList(buyButton, color)
      }
    })

    val tariffsSnap = PagerSnapHelper()
    tariffsSnap.attachToRecyclerView(tariffsContainer)

    val adapter = ReviewsAdapter()

    val dotsIndicator = findViewById<DotIndicatorView>(R.id.reviews_page_indicator)
    dotsIndicator.setCirclesCount(adapter.itemCount)

    val reviewsContainer = findViewById<RecyclerView>(R.id.reviews)
    reviewsContainer.adapter = adapter
    reviewsContainer.addOnScrollListener(object : OnScrollListener() {
      override fun onScrollStateChanged(p: RecyclerView, newState: Int) {
        super.onScrollStateChanged(p, newState)

        val lm = p.layoutManager as LinearLayoutManager

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          dotsIndicator.setActiveIndex(lm.findFirstCompletelyVisibleItemPosition())
        }
      }
    })

    val reviewsSnap = PagerSnapHelper()
    reviewsSnap.attachToRecyclerView(reviewsContainer)

    findViewById<View>(R.id.action_open_subscriptions).setOnClickListener {
      Events.logPushButton("open_subscriptions_list", source)

      supportFragmentManager
        .beginTransaction()
        .add(android.R.id.content, SubscriptionsListFragment())
        .addToBackStack(null)
        .commit()
    }
  }

  override fun onBackPressed() {
    super.onBackPressed()
    Events.logPushButton("back", source)
  }

  fun purchase(subscriptionId: String) {
    purchasedId =
      if (withTrial) subscriptionsMap[subscriptionId] ?: subscriptionId else subscriptionId

    Events.logPushButton("purchase", source, purchasedId)

    disposables.add(SubscriptionManager
      .buy(this, purchasedId ?: "premium_year")
      .subscribe(emptyConsumer(), Consumer { error ->
        val toast = when (error) {
          is SubscriptionNotFoundException ->
            Toast.makeText(this, string.subscription_not_found, Toast.LENGTH_SHORT)
          is SetupFailedException ->
            Toast.makeText(this, string.subscription_not_found, Toast.LENGTH_SHORT)

          else -> null
        }

        toast?.show()
      }))
  }

  override fun onDestroy() {
    super.onDestroy()

    disposables.clear()
  }

  data class PremiumPlan(
    val key: String,
    val price: Double,
    val duration: Int,
    val diamondStyleId: Int,
    val monthlyPrice: Double,
    val benefitPercentage: Int,
    val prevMonthlyPrice: Double,
    val benefitTintColor: Int,
    val benefitTextColor: Int,
    val trial: Int
  )

  class PlanHolder(val view: PremiumPlanCardView) : ViewHolder(view)

  open class PlansAdapter(
    val isDarkMode: Boolean = false,
    val withTrial: Boolean = true) : Adapter<PlanHolder>() {

    open fun getLayoutParams(parent: ViewGroup, viewType: Int): LayoutParams {
      val width = parent.context.resources.displayMetrics.widthPixels

      return MarginLayoutParams(width - dp(parent.context, 64f), WRAP_CONTENT)
        .apply {
          leftMargin = dp(parent.context, 16f)
          rightMargin = dp(parent.context, 16f)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanHolder {
      val view = if (withTrial) {
        PremiumPlanCardView2(parent.context)
      } else {
        PremiumPlanCardView(parent.context)
      }

      view.layoutParams = getLayoutParams(parent, viewType)
      return PlanHolder(view)
    }

    override fun getItemCount(): Int {
      return plans.size
    }

    override fun onBindViewHolder(holder: PlanHolder, position: Int) {
      val c = holder.view.context
      holder.view.setDiamondStyle(plans[position].diamondStyleId)
      holder.view.duration.visibility = View.VISIBLE

      if (withTrial) {
        if (plans[position].trial > 0) {
          holder.view.duration.text = c.resources
            .getQuantityString(R.plurals.trial_days,
                plans[position].trial,
                plans[position].trial
            )
        } else {
          holder.view.duration.text = ""
          holder.view.duration.visibility = View.GONE
        }

        holder.view.price.text = c.resources
          .getString(R.string.subscription_detailed,
              c.getString(plans[position].duration),
              plans[position].price.asCurrency
          )
      } else {
        holder.view.duration.setText(plans[position].duration)
        holder.view.price.text = plans[position].price.asCurrency
      }

      holder.view.helper.text = holder.view.context
        .getString(R.string.premium_subscription_per_month, plans[position].monthlyPrice.asCurrency)

      if (plans[position].prevMonthlyPrice > 0) {
        val message = holder.view.context.getString(R.string.premium_subscription_per_month,
            plans[position].prevMonthlyPrice.asCurrency)

        holder.view.helper2.text = message.strikethrough()
      } else {
        holder.view.helper2.text = ""
      }

      if (position == 0) {
        holder.view.setDarkAppearance(isDarkMode)

        if (isDarkMode) {
          holder.view.setDiamondStyle(R.drawable.ic_brilliant_black)
          holder.view.setBackgroundResource(R.drawable.premium_annual_plan_gold_background)
        } else {
          holder.view.setBackgroundResource(R.drawable.premium_annual_plan_background)
        }
      } else {
        holder.view.setBackgroundResource(R.color.white)
      }

      if (isDarkMode) {
        holder.view.setBenefitsPercentage(
            plans[position].benefitPercentage,
            Color.BLACK,
            Color.WHITE
        )
      } else {
        holder.view.setBenefitsPercentage(
            plans[position].benefitPercentage,
            plans[position].benefitTintColor,
            plans[position].benefitTextColor
        )
      }
    }
  }

  private class ReviewHolder(private val image: ImageView) : RecyclerView.ViewHolder(image) {
    fun bind(imageId: Int) {
      image.setImageResource(imageId)
    }
  }

  private class ReviewsAdapter : RecyclerView.Adapter<ReviewHolder>() {
    private val reviews = intArrayOf(
        R.drawable.review_1,
        R.drawable.review_2,
        R.drawable.review_3,
        R.drawable.review_4
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {
      val params = MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)
      params.leftMargin = dp(parent.context, 16f)
      params.rightMargin = dp(parent.context, 16f)

      val view = ImageView(parent.context)
      view.layoutParams = params
      view.scaleType = CENTER_INSIDE

      return ReviewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {
      holder.bind(reviews[position])
    }

    override fun getItemCount(): Int {
      return reviews.size
    }
  }
}
