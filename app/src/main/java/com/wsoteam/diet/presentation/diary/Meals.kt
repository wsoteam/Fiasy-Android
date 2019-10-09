package com.wsoteam.diet.presentation.diary

import com.wsoteam.diet.Sync.UserDataHolder
import com.wsoteam.diet.model.Eating
import com.wsoteam.diet.model.Water
import io.reactivex.Flowable

object Meals {

  /**
   * Подсчет калорий, углеводов и жиров за указанный день
   */
  fun all(day: Int, month: Int, year: Int): Flowable<MealsDetailedResult> {
    UserDataHolder.getUserData()?.let {
      val meals =
        arrayOf(it.breakfasts, it.lunches, it.dinners, it.snacks, it.waters).filterNotNull()

      return Flowable.fromArray(meals)
        .flatMap { all -> Flowable.fromIterable(all) }
        .map { type ->
          type.filter { (key, meal) -> meal.day == day && meal.month == month && meal.year == year }
            .map { (key, value) ->
              val meal = value as Eating
              meal.urlOfImages = key.toString()
              meal
            }
        }
        .map { meals ->
          var calories = 0
          var fats = 0
          var proteins = 0
          var carbons = 0

          meals.forEach { meal ->
            calories = meal.calories
            fats = meal.fat
            proteins = meal.protein
            carbons = meal.carbohydrates
          }

          MealsDetailedResult(calories, fats, proteins, carbons, meals)
        }
    }

    return Flowable.empty()
  }

  fun water(day: Int, month: Int, year: Int): Flowable<Water> {
    UserDataHolder.getUserData()
        ?.waters?.let { mapWater ->

      mapWater.keys.forEach { key ->
        if (mapWater[key]?.day == day
            && mapWater[key]?.month == month
            && mapWater[key]?.year == year
        ) {
          mapWater[key]?.key = key
          return Flowable.fromArray(mapWater[key])
        }
      }
    }
    return Flowable.fromArray(Water(day, month, year, 0f))
  }

  data class MealsDetailedResult(
    val calories: Int,
    val fats: Int,
    val proteins: Int,
    val carbons: Int,
    val meals: List<Eating>
  )
}
