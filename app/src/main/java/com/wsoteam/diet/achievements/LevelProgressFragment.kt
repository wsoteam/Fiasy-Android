package com.wsoteam.diet.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.wsoteam.diet.R
import com.wsoteam.diet.utils.argument
import com.wsoteam.diet.utils.dp2px
import com.wsoteam.diet.utils.getVectorIcon
import java.lang.IllegalArgumentException

class LevelProgressFragment : DialogFragment() {
  var levelId by argument<Int>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?): View? {
    return LayoutInflater.from(inflater.context)
        .inflate(R.layout.fragment_motiviation_level_progress, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
    toolbar.setNavigationOnClickListener {
      dismiss()
    }

    toolbar.setTitle(resources.getIdentifier("level_$levelId", "string", view.context.packageName))

    val levelIconView = view.findViewById<ImageView>(R.id.level_icon)
    levelIconView.setImageDrawable(
        requireContext().getVectorIcon(
            resources.getIdentifier(
                "ic_evolve_map_$levelId",
                "drawable",
                view.context.packageName
            )
        )
    )

    val achievementsContainer = view.findViewById<LinearLayout>(R.id.achievements_list)
    val goalsContainer = view.findViewById<LinearLayout>(R.id.goals_list)
    achievementsContainer.removeAllViews()

    val strategy = getLevelManager().levels.find { it.id == levelId }
        ?: throw IllegalArgumentException("level not found")

    strategy.achievements.forEachIndexed { id, achievement ->
      val achievementIcon = ImageView(view.context).apply {
        val lp = MarginLayoutParams(context.dp2px(85f), context.dp2px(75f))
        lp.leftMargin = context.dp2px(12f) * id

        layoutParams = lp

        setImageDrawable(
            context.getDrawable(
                if (getLevelManager().unlocked.contains(achievement))
                  achievement.imageId
                else
                  R.drawable.ic_achievement_locked_wait
            )
        )
      }

      achievementsContainer.addView(achievementIcon)

      addGoalProgress(goalsContainer, strategy.getAchievementState(requireContext(), achievement.id))
    }
  }

  private fun addGoalProgress(container: ViewGroup, state: AchievementProgressState){
    val achievementContainer = LayoutInflater.from(container.context)
        .inflate(R.layout.item_achievement_progress, container, false)

    val title = achievementContainer.findViewById<TextView>(R.id.title)
    title.text = state.description

    val progress = achievementContainer.findViewById<ProgressBar>(R.id.progress)
    progress.max = state.max
    progress.progress = state.progress

    container.addView(achievementContainer)
  }
}
