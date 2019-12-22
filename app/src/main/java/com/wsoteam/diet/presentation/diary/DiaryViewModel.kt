package com.wsoteam.diet.presentation.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class DiaryViewModel : ViewModel() {

  companion object {
    private val liveSelectedDate = MutableLiveData<DiaryDay>()

    val selectedDate: LiveData<DiaryDay> = liveSelectedDate

    val scrollToPosition = MutableLiveData<Int>()


    val isToday: Boolean
      get() {
        val calendar = Calendar.getInstance()

        return currentDate == DiaryDay(
            calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.MONTH],
            calendar[Calendar.YEAR]
        )
      }

    var currentDate: DiaryDay
      get() {
        val current = liveSelectedDate.value
        return if (current != null) current else {
          val calendar = Calendar.getInstance()
          DiaryDay(
              calendar[Calendar.DAY_OF_MONTH],
              calendar[Calendar.MONTH],
              calendar[Calendar.YEAR]
          )
        }
      }
      set(value) {
        liveSelectedDate.value = value
      }
  }

  data class DiaryDay(
    val day: Int,
    val month: Int,
    val year: Int
  ) {
    val calendar: Calendar
      get() {
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_MONTH] = day
        calendar[Calendar.MONTH] = month
        calendar[Calendar.YEAR] = year
        return calendar
      }
  }
}
