package com.wsoteam.diet.achievements

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.wsoteam.diet.BuildConfig
import com.wsoteam.diet.R

class AchievementUnlockedDialog(private val achievement: Achievement, context: Context)
  : AppCompatDialog(context) {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window?.decorView?.background = null

    setContentView(R.layout.dialog_achievement_unlocked)

    findViewById<ImageView>(R.id.close)?.setOnClickListener {
      dismiss()
    }

    findViewById<TextView>(R.id.action_done)?.setOnClickListener {
      dismiss()
    }

    findViewById<TextView>(R.id.title)?.apply {
      text = context?.getString(achievement.title)
    }

    findViewById<TextView>(R.id.description)?.apply {
      text = context?.getString(achievement.description)
    }

    findViewById<ImageView>(R.id.icon)?.apply {
      setImageDrawable(VectorDrawableCompat.create(resources, achievement.imageId, context.theme))
    }
  }
}
