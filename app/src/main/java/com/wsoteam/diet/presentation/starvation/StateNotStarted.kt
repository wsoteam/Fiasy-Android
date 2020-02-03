package com.wsoteam.diet.presentation.starvation


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View

import com.wsoteam.diet.R
import com.wsoteam.diet.Sync.WorkWithFirebaseDB
import com.wsoteam.diet.utils.Subscription
import kotlinx.android.synthetic.main.state_not_started.*


class StateNotStarted : Fragment(R.layout.state_not_started) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textBtn.setOnClickListener {

            if(Subscription.check(context))
                WorkWithFirebaseDB.setStarvationTimestamp(System.currentTimeMillis() - 1_000)
            else BlockedStarvationDialogFragment.show(fragmentManager)

        }

        edit.setOnClickListener {
            if(Subscription.check(context))
                startActivity(Intent(context, StarvationSettingsActivity::class.java))
            else BlockedStarvationDialogFragment.show(fragmentManager)

        }
    }
}
