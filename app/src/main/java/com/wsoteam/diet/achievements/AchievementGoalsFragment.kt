package com.wsoteam.diet.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.wsoteam.diet.R

class AchievementGoalsFragment : DialogFragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialog)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_achievement_goal, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val progress = view.findViewById<AchievementDetailedProgress>(R.id.achievement_progress)
    progress.fillWith(8)
  }
}
