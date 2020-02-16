package com.wsoteam.diet.achievements

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import io.reactivex.Single

class Achievement(context: Context, val id: String,
  val title: Int,
  val description: Int,
  val imageId: Int) {
  val imageName: String = context.resources.getResourceEntryName(imageId)
}


data class AchievementProgressState(val progress: Int, val max: Int, val description: CharSequence)

enum class UserInAppAction {
  SIGN_UP,
  ADDED_PRODUCT,
  ADDED_WEIGHT,
  ADDED_WATER;
}

abstract class LevelManager(context: Context) {
  protected val context = context.applicationContext

  abstract val currentLevel: Int

  abstract val levels: List<LevelStrategy>

  /**
   * List of all available achievements by this manager
   */
  abstract val achievements: List<Achievement>

  /**
   * List of currently unlocked achievements
   */
  abstract val unlocked: List<Achievement>

  /**
   * Callback to grant user with achievement
   */
  abstract val liveAchievements: LiveData<Achievement>

  abstract val liveLevelChanges: LiveData<Pair<Int, Int>>

  /**
   * Handling user action in order to level up or give an achievement
   */
  abstract fun onUserAction(action: UserInAppAction)
}

abstract class LevelStrategy(val id: Int) {

  /**
   * Called whenever user made action that is described in one of {@link UserInAppAction}
   * here those actions sequence could be stored for e.g sequence of 7 actions required to
   * unlock achievement
   *
   * @return True if achievement was unlocked by action
   */
  @MainThread
  abstract fun didAchievementUnlockedByAction(action: UserInAppAction): Achievement?

  abstract fun getAchievementState(context: Context, id: String): AchievementProgressState

  /**
   * List of achievement that could be earned by this level
   */
  abstract val achievements: List<Achievement>
}