package com.wsoteam.diet.achievements

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wsoteam.diet.Sync.WorkWithFirebaseDB
import timber.log.Timber

class FirebaseLevelManager(context: Context) : LevelManager(context) {
  protected val strategies = getStrategies()

  protected val mutableAchievements = MutableLiveData<Achievement>()
  protected val mutableLevel = MutableLiveData<Pair<Int, Int>>()

  private var unlockedAchievements = ArrayList<Achievement>()
  private var currentLevelStrategy = nextLevel()

  override val levels: List<LevelStrategy>
    get() = strategies

  override val currentLevel: Int
    get() = currentLevelStrategy.id

  override val achievements: List<Achievement>
    get() {
      return strategies.flatMapTo(ArrayList()) { it.achievements }
    }

  override val unlocked: List<Achievement>
    get() = unlockedAchievements

  override val liveAchievements: LiveData<Achievement>
    get() = mutableAchievements

  override val liveLevelChanges: LiveData<Pair<Int, Int>>
    get() = mutableLevel

  protected fun onAchievementUnlocked(achievement: Achievement) {
    Timber.d("achievement unlocked: ${achievement.id}")

    unlockedAchievements.add(achievement)

    // save achievement
    mutableAchievements.value = achievement

    WorkWithFirebaseDB.setAchievements(unlockedAchievements.map { it.id })
  }

  fun restoreAchievements(unlocked: List<String>?) {
    if (unlocked == null) {
      return
    }

    unlockedAchievements.addAll(achievements.filter { it.id in unlocked })
  }

  private fun nextLevel(): LevelStrategy {
    return strategies.firstOrNull { !unlocked.containsAll(it.achievements) } ?: strategies.last()
  }

  override fun onUserAction(action: UserInAppAction) {
    Timber.d("user action received: $action")

    currentLevelStrategy.didAchievementUnlockedByAction(action)?.let {
      onAchievementUnlocked(it)

      if (unlocked.containsAll(currentLevelStrategy.achievements)) {
        // all achievements were gained from level, so we can level - up
        val oldLevel = currentLevelStrategy.id
        currentLevelStrategy = nextLevel()

        if (currentLevel > oldLevel) {
          Timber.d("level updated: $oldLevel -> $currentLevel")
          mutableLevel.value = Pair(oldLevel, currentLevel)
        }
      }
    }
  }
}