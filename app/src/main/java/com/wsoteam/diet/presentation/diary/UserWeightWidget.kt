package com.wsoteam.diet.presentation.diary

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.wsoteam.diet.App
import com.wsoteam.diet.R
import com.wsoteam.diet.Sync.UserDataHolder
import com.wsoteam.diet.Sync.WorkWithFirebaseDB
import com.wsoteam.diet.common.helpers.DateAndTime
import com.wsoteam.diet.presentation.measurment.MeasurmentActivity
import java.util.*


class UserWeightWidget(itemView: View) : WidgetsAdapter.WidgetView(itemView), OnClickListener {
    private val weightLabel = itemView.findViewById<TextView>(R.id.weight)
    private val updatedLabel = itemView.findViewById<TextView>(R.id.last_update)
    private val actionRefresh = itemView.findViewById<TextView>(R.id.action_refresh)

    override fun onClick(v: View) {
        when (v.id) {
            R.id.action_refresh -> openMeasScreen()
        }
    }

    private val weightObserver = Observer<Int> { id ->
        if ((id ?: -1) == WorkWithFirebaseDB.WEIGHT_UPDATED) {
            getWeightHistory()
        }
    }

    private fun openMeasScreen() {
        context.startActivity(Intent(context, MeasurmentActivity::class.java))
    }

    override fun onBind(parent: RecyclerView, position: Int) {
        super.onBind(parent, position)
        getWeightHistory()
    }

    override fun onAttached(parent: RecyclerView) {
        super.onAttached(parent)
        actionRefresh.setOnClickListener(this)

        WorkWithFirebaseDB.liveUpdates().observeForever(weightObserver)

    }

    override fun onDetached(parent: RecyclerView) {
        super.onDetached(parent)
        WorkWithFirebaseDB.liveUpdates().removeObserver(weightObserver)
        actionRefresh.setOnClickListener(null)
    }

    private fun getWeightHistory() {
        val keys: ArrayList<String>
        val values: ArrayList<String>
        try {
            if (UserDataHolder.getUserData() != null && UserDataHolder.getUserData().getProfile() != null && UserDataHolder.getUserData().weights != null && UserDataHolder.getUserData().weights.size > 0) {
                val weightHashMap = UserDataHolder.getUserData().weights
                keys = ArrayList()
                val iterator = weightHashMap.entries.iterator()
                while (iterator.hasNext()) {
                    val pair = iterator.next()
                    keys.add(pair.key as String)
                }
                Collections.sort(keys)

                values = ArrayList()
                for (i in keys.indices) {
                    values.add(weightHashMap[keys[i]]!!.weight.toString())
                }
                updatedLabel.text = App.getContext().resources.getString(R.string.meas_prefix) + " " + getTimeDiff(keys.last())
                weightLabel.text = values[values.size - 1] + " " + App.getContext().resources.getString(R.string.weight_unit)
            } else {
                weightLabel.text = UserDataHolder.getUserData().profile.weight.toString() + " " + App.getContext().resources.getString(R.string.weight_unit)
            }
        } catch (e: Exception) {
            Log.e("LOL", "Profile is null")
        }

    }

    private fun getTimeDiff(last: String): String {
        var millisInDay = 86400000L
        var lastTime = last.toLong()
        var calendar = DateAndTime.dropTime(Calendar.getInstance())
        var currentTime = calendar.timeInMillis
        var diffDays = (currentTime - lastTime) / millisInDay
        if (diffDays.toInt() == 0) {
            return App.getContext().resources.getString(R.string.meas_last_refresh_today)
        } else {
            return App.getInstance().resources.getQuantityString(R.plurals.meas_days_ago, diffDays.toInt(), diffDays.toInt())
        }
    }
}
