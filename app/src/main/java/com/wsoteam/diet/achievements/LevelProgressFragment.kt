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
import androidx.lifecycle.Observer
import com.transferwise.sequencelayout.children
import com.wsoteam.diet.BuildConfig
import com.wsoteam.diet.R
import com.wsoteam.diet.utils.argument
import com.wsoteam.diet.utils.dp2px
import com.wsoteam.diet.utils.getVectorIcon
import java.lang.IllegalArgumentException
import java.util.logging.Level

class LevelProgressFragment : DialogFragment() {
  var levelId by argument<Int>()

  private val currentStrategy: LevelStrategy by lazy {
    getLevelManager().levels.find { it.id == levelId }
        ?: throw IllegalArgumentException("level not found")
  }

  private lateinit var achievementsContainer: ViewGroup
  private lateinit var goalsContainer: ViewGroup

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

    goalsContainer = view.findViewById<LinearLayout>(R.id.goals_list)
    achievementsContainer = view.findViewById<LinearLayout>(R.id.achievements_list)
    achievementsContainer.removeAllViews()

    getLevelManager().liveAchievements.observe(this, Observer { achievement ->
      val achievementImageview = achievementsContainer.children()
          .first { it.tag == achievement.id } as ImageView

      updateGoalProgress(goalsContainer, achievement )
      achievementImageview.setAchievementIcon(achievement)
    })

    currentStrategy.achievements.forEachIndexed { id, achievement ->
      val achievementIcon = ImageView(view.context).apply {
        val lp = MarginLayoutParams(context.dp2px(85f), context.dp2px(75f))
        lp.leftMargin = context.dp2px(12f) * id

        tag = achievement.id
        layoutParams = lp

        setAchievementIcon(achievement)

        if (!BuildConfig.DEBUG) {
          return
        }

        setOnLongClickListener debugUnlockAchievement@{
          (getLevelManager() as FirebaseLevelManager).unlockDirectly(achievement)
          return@debugUnlockAchievement true
        }
      }

      achievementsContainer.addView(achievementIcon)

      addGoalProgress(goalsContainer, achievement)
    }
  }

  private fun ImageView.setAchievementIcon(achievement: Achievement){
    setImageDrawable(
        context.getDrawable(
            if (getLevelManager().unlocked.contains(achievement))
              achievement.imageId
            else
              R.drawable.ic_achievement_locked_wait
        )
    )
  }

  private fun updateGoalProgress(container: ViewGroup, achievement: Achievement){
    val achievementContainer = container.children().first { it.tag == achievement.id }

    val state = currentStrategy.getAchievementState(requireContext(), achievement.id)
    val title = achievementContainer.findViewById<TextView>(R.id.title)
    title.text = state.description

    val progress = achievementContainer.findViewById<ProgressBar>(R.id.progress)
    progress.isIndeterminate = false
    progress.indeterminateDrawable = null
    progress.progressDrawable = ProgressDrawable(container.context)

    if (achievement !in getLevelManager().unlocked){
      progress.max = state.max + 100
      progress.progress = state.progress + 10
    } else {
      progress.max = 1
      progress.progress = 1
    }
  }

  private fun addGoalProgress(container: ViewGroup, achievement: Achievement) {
    val achievementContainer = LayoutInflater.from(container.context)
        .inflate(R.layout.item_achievement_progress, container, false)

    achievementContainer.tag = achievement.id

    val state = currentStrategy.getAchievementState(requireContext(), achievement.id)
    val title = achievementContainer.findViewById<TextView>(R.id.title)
    title.text = state.description

    val progress = achievementContainer.findViewById<ProgressBar>(R.id.progress)
    progress.isIndeterminate = false
    progress.indeterminateDrawable = null
    progress.progressDrawable = ProgressDrawable(container.context)

    if (achievement !in getLevelManager().unlocked){
      progress.max = state.max
      progress.progress = state.progress
    } else {
      progress.max = 1
      progress.progress = 1
    }

    container.addView(achievementContainer)
  }
}
