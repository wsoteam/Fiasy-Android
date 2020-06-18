package com.losing.weight.presentation.premium

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.losing.weight.AmplitudaEvents
import com.losing.weight.Authenticate.POJO.Box
import com.losing.weight.Config
import com.losing.weight.InApp.ActivitySubscription
import com.losing.weight.R
import com.losing.weight.common.Analytics.EventProperties
import kotlinx.android.synthetic.main.activity_pre_prem_graph.*

class GraphPrePremium : AppCompatActivity(R.layout.activity_pre_prem_graph) {
    override fun onBackPressed() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var box = Box()
        box.buyFrom = EventProperties.trial_from_onboard
        box.comeFrom = AmplitudaEvents.view_prem_free_onboard
        box.isOpenFromIntrodaction = true
        box.isOpenFromPremPart = false

        var intent = Intent(this, ActivitySubscription::class.java)
        intent.putExtra(Config.TAG_BOX, box)
        btnNext.setOnClickListener {
           startActivity(intent)
        }
    }
}