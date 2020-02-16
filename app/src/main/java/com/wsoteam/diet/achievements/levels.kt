@file:JvmName("Levels")

package com.wsoteam.diet.achievements

import android.content.Context
import android.text.format.DateUtils
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.wsoteam.diet.App
import com.wsoteam.diet.R
import com.wsoteam.diet.Sync.WorkWithFirebaseDB
import com.wsoteam.diet.achievements.Achievements.FIRST_PRODUCT_ADDED
import com.wsoteam.diet.achievements.Achievements.SIGNED_UP
import com.wsoteam.diet.achievements.Achievements.STREAK_14_PRODUCTS
import com.wsoteam.diet.achievements.Achievements.STREAK_14_WATER
import com.wsoteam.diet.achievements.Achievements.STREAK_14_WEIGHT
import com.wsoteam.diet.achievements.Achievements.STREAK_21_2_PRODUCTS
import com.wsoteam.diet.achievements.Achievements.STREAK_21_2_WATER
import com.wsoteam.diet.achievements.Achievements.STREAK_21_2_WEIGHT
import com.wsoteam.diet.achievements.Achievements.STREAK_21_PRODUCTS
import com.wsoteam.diet.achievements.Achievements.STREAK_21_WATER
import com.wsoteam.diet.achievements.Achievements.STREAK_21_WEIGHT
import com.wsoteam.diet.achievements.Achievements.STREAK_7_PRODUCTS
import com.wsoteam.diet.achievements.Achievements.STREAK_7_WATER
import com.wsoteam.diet.achievements.Achievements.STREAK_7_WEIGHT
import com.wsoteam.diet.achievements.StreakProperty.PRODUCTS_ADDED
import com.wsoteam.diet.achievements.StreakProperty.WATER_DRUNK
import com.wsoteam.diet.achievements.StreakProperty.WEIGHT_CHANGES
import com.wsoteam.diet.achievements.UserInAppAction.ADDED_PRODUCT
import com.wsoteam.diet.achievements.UserInAppAction.ADDED_WATER
import com.wsoteam.diet.achievements.UserInAppAction.ADDED_WEIGHT
import com.wsoteam.diet.achievements.UserInAppAction.SIGN_UP
import io.reactivex.Single
import java.lang.IllegalArgumentException

fun getStrategies(): List<LevelStrategy> {
  return arrayListOf(
      Level1(),
      StreaksLevel(
          2, 7, mapOf(
          PRODUCTS_ADDED to STREAK_7_PRODUCTS,
          WATER_DRUNK to STREAK_7_WATER,
          WEIGHT_CHANGES to STREAK_7_WEIGHT
      )
      ),
      StreaksLevel(
          3, 14, mapOf(
          PRODUCTS_ADDED to STREAK_14_PRODUCTS,
          WATER_DRUNK to STREAK_14_WATER,
          WEIGHT_CHANGES to STREAK_14_WEIGHT
      )
      ),
      StreaksLevel(
          4, 21, mapOf(
          PRODUCTS_ADDED to STREAK_21_PRODUCTS,
          WATER_DRUNK to STREAK_21_WATER,
          WEIGHT_CHANGES to STREAK_21_WEIGHT
      )
      ),
      StreaksLevel(
          5, 21, mapOf(
          PRODUCTS_ADDED to STREAK_21_2_PRODUCTS,
          WATER_DRUNK to STREAK_21_2_WATER,
          WEIGHT_CHANGES to STREAK_21_2_WEIGHT
      )
      )
  )
}

private val levelManager by lazy { FirebaseLevelManager(App.instance) }

fun getLevelManager(): LevelManager {
  return levelManager
}

fun dispatchInAppAction(action: UserInAppAction) {
  getLevelManager().onUserAction(action)
}

enum class StreakProperty {
  WEIGHT_CHANGES,
  WATER_DRUNK,
  PRODUCTS_ADDED,
}

object Achievements {

  private fun createAchievement(id: String,
    @StringRes title: Int,
    @StringRes description: Int,
    @DrawableRes imageId: Int): Achievement {

    return Achievement(App.instance, id, title, description, imageId)
  }

  val FIRST_PRODUCT_ADDED = createAchievement(
      "first_product",
      R.string.first_product_title,
      R.string.first_product_description,
      R.drawable.meal_1
  )

  val STREAK_7_PRODUCTS = createAchievement(
      "7_products_added",
      R.string.motivation_7_products_added_achievement_title,
      R.string.motivation_7_products_added_achievement_description,
      R.drawable.meal_2
  )

  val STREAK_14_PRODUCTS = createAchievement(
      "14_products_added",
      R.string.motivation_14_products_added_achievement_title,
      R.string.motivation_14_products_added_achievement_description,
      R.drawable.meal_3
  )

  val STREAK_21_PRODUCTS = createAchievement(
      "21_products_added",
      R.string.motivation_21_products_added_achievement_title,
      R.string.motivation_21_products_added_achievement_description,
      R.drawable.meal_4
  )

  val STREAK_21_2_PRODUCTS = createAchievement(
      "21_2_products_added",
      R.string.motivation_21_products_added_achievement_title,
      R.string.motivation_21_products_added_achievement_description,
      R.drawable.meal_4
  )

  val STREAK_7_WATER = createAchievement(
      "7_water_added",
      R.string.motivation_7_water_added_achievement_title,
      R.string.motivation_7_water_added_achievement_description,
      R.drawable.meal_2
  )

  val STREAK_14_WATER = createAchievement(
      "14_water_added",
      R.string.motivation_14_water_added_achievement_title,
      R.string.motivation_14_water_added_achievement_description,
      R.drawable.meal_3
  )

  val STREAK_21_WATER = createAchievement(
      "21_water_added",
      R.string.motivation_21_water_added_achievement_title,
      R.string.motivation_21_water_added_achievement_description,
      R.drawable.meal_4
  )

  val STREAK_21_2_WATER = createAchievement(
      "21_2_water_added",
      R.string.motivation_21_water_added_achievement_title,
      R.string.motivation_21_water_added_achievement_description,
      R.drawable.meal_4
  )

  val STREAK_7_WEIGHT = createAchievement(
      "7_weight_added",
      R.string.motivation_7_weight_added_achievement_title,
      R.string.motivation_7_weight_added_achievement_description,
      R.drawable.meal_2
  )

  val STREAK_14_WEIGHT = createAchievement(
      "14_weight_added",
      R.string.motivation_14_weight_added_achievement_title,
      R.string.motivation_14_weight_added_achievement_description,
      R.drawable.meal_3
  )

  val STREAK_21_WEIGHT = createAchievement(
      "21_weight_added",
      R.string.motivation_21_weight_added_achievement_title,
      R.string.motivation_21_weight_added_achievement_description,
      R.drawable.meal_4
  )

  val STREAK_21_2_WEIGHT = createAchievement(
      "21_2_weight_added",
      R.string.motivation_21_weight_added_achievement_title,
      R.string.motivation_21_weight_added_achievement_description,
      R.drawable.meal_4
  )

  val SIGNED_UP = createAchievement(
      "sign_up",
      R.string.sign_up_title,
      R.string.sign_up_description,
      R.drawable.time_1
  )
}

class StreakCounter(
  private val property: StreakProperty,
  private var lastUpdate: Long,
  private var value: Int) {

  fun incrementAndGet(): Int {
    if (System.currentTimeMillis() > lastUpdate && !DateUtils.isToday(lastUpdate)) {
      value++;
      WorkWithFirebaseDB.setUserStreakProgress(property, value, System.currentTimeMillis())
    }

    return value;
  }

  fun reset() {
    WorkWithFirebaseDB.setUserStreakProgress(property, 0, System.currentTimeMillis())
  }

  fun value(): Int {
    return value;
  }
}

class StreaksLevel(levelId: Int,
  val streaksRequired: Int,
  val achievementsMap: Map<StreakProperty, Achievement>) : LevelStrategy(levelId) {

  private val eatingStreak = WorkWithFirebaseDB.getStreakCounter(PRODUCTS_ADDED)
  private val drinkStreak = WorkWithFirebaseDB.getStreakCounter(WATER_DRUNK)
  private val weightStreak = WorkWithFirebaseDB.getStreakCounter(WEIGHT_CHANGES)

  override fun didAchievementUnlockedByAction(action: UserInAppAction): Achievement? {
    return when (action) {
      ADDED_PRODUCT -> incrementStreak(PRODUCTS_ADDED, eatingStreak)
      ADDED_WATER -> incrementStreak(WATER_DRUNK, drinkStreak)
      ADDED_WEIGHT -> incrementStreak(WEIGHT_CHANGES, weightStreak)
      else -> return null
    }
  }

  private fun incrementStreak(property: StreakProperty, counter: StreakCounter): Achievement? {
    if (counter.incrementAndGet() == streaksRequired) {
      counter.reset()
      return achievementsMap[property]
    } else {
      return null
    }
  }

  override fun getAchievementState(context: Context, id: String): AchievementProgressState {
    return AchievementProgressState(0, 0, "")
  }

  override val achievements: List<Achievement>
    get() = ArrayList(achievementsMap.values)
}

class Level1 : LevelStrategy(1) {
  override fun didAchievementUnlockedByAction(action: UserInAppAction): Achievement? {
    return when (action) {
      SIGN_UP -> SIGNED_UP
      ADDED_PRODUCT -> FIRST_PRODUCT_ADDED
      else -> null
    }
  }

  override fun getAchievementState(context: Context, id: String): AchievementProgressState {
    return when (id) {
      SIGNED_UP.id -> AchievementProgressState(1, 1, "signed up")
      FIRST_PRODUCT_ADDED.id -> AchievementProgressState(0, 1, "No products added")
      else -> throw IllegalArgumentException("Achievement($id) doesn't belong to this level")
    }
  }

  override val achievements: List<Achievement>
    get() = listOf(FIRST_PRODUCT_ADDED, SIGNED_UP)
}

