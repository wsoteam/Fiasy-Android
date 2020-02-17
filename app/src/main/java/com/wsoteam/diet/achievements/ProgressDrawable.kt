package com.wsoteam.diet.achievements

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.wsoteam.diet.R
import com.wsoteam.diet.utils.dp2pxF
import java.lang.IllegalArgumentException

class ProgressDrawable(context: Context) : Drawable() {
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val progressColor = ContextCompat.getColor(context, R.color.orange)
  private val backgroundColor = ContextCompat.getColor(context, R.color.grey_500)
  private val boundsRounded = RectF()
  private val doneIcon = ContextCompat.getDrawable(context, R.drawable.ic_done_white_24dp)
      ?: throw IllegalArgumentException("done icon not found")

  init {
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = context.dp2pxF(1.5f)
    paint.color = ContextCompat.getColor(context, R.color.orange)

    DrawableCompat.setTint(doneIcon, paint.color)
  }

  override fun onBoundsChange(bounds: Rect) {
    super.onBoundsChange(bounds)

    doneIcon.setBounds(bounds)
    boundsRounded.set(bounds)
  }

  override fun draw(canvas: Canvas) {
    if (level == 10000) {
      doneIcon.draw(canvas)
      return
    }

    paint.color = backgroundColor
    canvas.drawArc(boundsRounded, 360f * level / 10000f - 90f,
        360f - 360f * level / 10000f, false, paint)

    paint.color = progressColor
    canvas.drawArc(
        boundsRounded, -90f,
        360f * level / 10000f, false, paint
    )
  }

  override fun setAlpha(alpha: Int) {
    paint.alpha = alpha
  }

  override fun getOpacity(): Int {
    return PixelFormat.OPAQUE
  }

  override fun setColorFilter(colorFilter: ColorFilter?) {

  }

}