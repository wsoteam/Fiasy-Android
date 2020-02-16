package com.wsoteam.diet.achievements

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.wsoteam.diet.BuildConfig
import com.wsoteam.diet.R
import com.wsoteam.diet.utils.dp2pxF
import timber.log.Timber
import kotlin.math.ceil
import kotlin.math.roundToInt

class AchievementDetailedProgress
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

  private val shape = StarIcon(context.dp2pxF(26f), context.dp2pxF(26f), R.drawable.group_479)
  private val stars = arrayListOf<StarIcon>()
  private val starsPerRow = 7
  private val marginVertical = context.dp2pxF(8f)
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

  init {
    paint.color = Color.parseColor("#f49231")
    paint.style = Style.STROKE
    paint.strokeWidth = context.dp2pxF(1f)

    if (isInEditMode) {
      fillWith(8)
    }
  }

  fun fillWith(numberOfStars: Int) {
    for (x in 0 until numberOfStars) {
      stars.add(StarIcon(shape.w, shape.h, shape.resId))
    }

    requestLayout()
    invalidate()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val rows = ceil(1f * stars.size / starsPerRow)
    val height = (rows * shape.h + (rows - 1) * marginVertical).roundToInt() +
        paddingBottom + paddingTop

    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawLine(0f, 0f, 1f * width, 0f, paint)

    val spaceHorizontal = (width - starsPerRow * shape.w) / (starsPerRow - 1)

    for (row in 0 until ceil(1f * stars.size / starsPerRow).toInt()) {
      val cells = Math.min(starsPerRow, stars.size - row * starsPerRow)

      for (cell in 0 until cells) {
        val star = stars[row * starsPerRow + cell]
        star.drawable.bounds.offsetTo(
            ((shape.w + spaceHorizontal) * cell).toInt(),
            ((shape.h + marginVertical) * row).toInt()
        )

        if (row == 0) {
          star.drawable.bounds.offset(0, paddingTop)
        }

        star.drawable.draw(canvas)
      }
    }

    Timber.d("bottom=$bottom, top = $top, $height")

    canvas.drawLine(
        0f, height - paint.strokeWidth, 1f * width,
        height - paint.strokeWidth, paint
    )
  }

  private inner class StarIcon(val w: Float, val h: Float, val resId: Int) {
    val drawable by lazy {
      val d = ContextCompat.getDrawable(context, resId)
          ?: throw IllegalArgumentException("drawable with id=${resId.toString(16)} not found")
      d.setBounds(0, 0, w.toInt(), h.toInt())
      d
    }
  }
}
