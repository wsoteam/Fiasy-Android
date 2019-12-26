package com.wsoteam.diet.presentation.onboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.wsoteam.diet.Authenticate.POJO.Box
import com.wsoteam.diet.InApp.ActivitySubscription
import com.wsoteam.diet.InApp.Fragments.FragmentE
import com.wsoteam.diet.InApp.Fragments.FragmentF
import com.wsoteam.diet.R
import com.wsoteam.diet.Sync.UserDataHolder
import com.wsoteam.diet.common.Analytics.EventProperties
import com.wsoteam.diet.common.Analytics.Events
import com.wsoteam.diet.presentation.premium.PremiumFeaturesActivity
import com.wsoteam.diet.utils.dp
import com.wsoteam.diet.views.OnboardUserCardView

class OnboardPlanActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_onboard_plan)

    val weight = findViewById<OnboardUserCardView>(R.id.weight)
    val height = findViewById<OnboardUserCardView>(R.id.height)
    val age = findViewById<OnboardUserCardView>(R.id.age)

    UserDataHolder.getUserData()?.let {
      age.setValue(it.profile.age.toString())

      weight.setValue(it.profile.weight.toString())
      height.setValue(it.profile.height.toString())
    }

    findViewById<View>(R.id.next).setOnClickListener {
      Events.trackOnboardEvent("next")

      val box = Box()
      box.buyFrom = EventProperties.trial_from_second_offer

      val intents = mutableListOf<Intent>()

      if (intent.getBooleanExtra("feedback", false)) {
        intents += Intent(this, OnBoardFeedbackActivity::class.java)
      }

      intents += ActivitySubscription.open(this, FragmentF::class.java, box)

      startActivities(intents.toTypedArray())
      finish()
    }

    findViewById<View>(R.id.close).visibility = View.GONE
    findViewById<View>(R.id.close).setOnClickListener {
      Events.trackOnboardEvent("close")

      finish()

      if (intent.getBooleanExtra("feedback", false)) {
        startActivity(Intent(this, OnBoardFeedbackActivity::class.java))
      }
    }

    findViewById<ImageView>(R.id.recommended_plan_image).let {
      Picasso.get()
        .load(R.drawable.onboard_plan_background_recommended_plan)
        .resize(dp(this, 320f), 0)
        .centerCrop()
        .into(it)
    }
  }

  override fun onBackPressed() {
    super.onBackPressed()

    if (intent.getBooleanExtra("feedback", false)) {
      startActivity(Intent(this, OnBoardFeedbackActivity::class.java))
    }
  }

}
