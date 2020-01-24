package com.wsoteam.diet.presentation.starvation


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.wsoteam.diet.R
import kotlinx.android.synthetic.main.fragment_starvation_settings.*
import android.app.TimePickerDialog
import android.util.Log
import java.util.*


class StarvationSettingsFragment : Fragment(R.layout.fragment_starvation_settings) {

    private lateinit var daysWeek: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StarvationViewModel.getStarvation().observe(this, androidx.lifecycle.Observer {
            updateUi(it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        days.setOnClickListener {
            DaysPickerDialogFragment.show(fragmentManager)
        }
        time.setOnClickListener { openDialog() }

        daysWeek = resources.getStringArray(R.array.days_week).toList()
    }

    fun openDialog() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            Log.d("kkk", "time - ${cal.time}")
//            textView.text = SimpleDateFormat("HH:mm").format(cal.time)
        }
        TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()

    }

    private fun updateUi(starvation: Starvation) {

        val dayResult = StringBuffer()
        val daysList = starvation.days?.toMutableList() ?: mutableListOf()

        starvation.timeMillis?.apply {

        }

        when {
            daysList.size == 0 -> days.text = getString(R.string.starvation_select)
            daysList.size == Calendar.DAY_OF_WEEK -> days.text = getString(R.string.starvation_all_days)
            else -> {
                daysList.sortWith(compareBy { it })
                if (daysList[0] == Calendar.SUNDAY) {
                    daysList.remove(Calendar.SUNDAY)
                    daysList.add(Calendar.SUNDAY)
                }

                daysList.forEach {
                    Log.d("kkk", "day - $it")
                    dayResult.append(", ").append(daysWeek[it - 1])
                }

                dayResult.delete(0, 2)
                days.text = dayResult
            }
        }
    }
}
