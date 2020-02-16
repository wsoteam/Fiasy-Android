package com.wsoteam.diet.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.Style
import android.graphics.Path
import android.graphics.Path.Direction.CW
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.withSave
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.wsoteam.diet.R
import com.wsoteam.diet.utils.dp2px
import com.wsoteam.diet.utils.dp2pxF
import com.wsoteam.diet.utils.getVectorIcon
import com.wsoteam.diet.utils.withStateSaved
import kotlin.math.roundToInt

class EvolveMapView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
  companion object {
    private val lockedMap = SparseIntArray().apply {
      put(R.drawable.ic_evolve_map_1, R.drawable.ic_evolve_map_1_locked)
      put(R.drawable.ic_evolve_map_2, R.drawable.ic_evolve_map_2_locked)
      put(R.drawable.ic_evolve_map_3, R.drawable.ic_evolve_map_3_locked)
      put(R.drawable.ic_evolve_map_4, R.drawable.ic_evolve_map_4_locked)
      put(R.drawable.ic_evolve_map_5, R.drawable.ic_evolve_map_5_locked)
      put(R.drawable.ic_evolve_map_6, R.drawable.ic_evolve_map_6_locked)
      put(R.drawable.ic_evolve_map_7, R.drawable.ic_evolve_map_7_locked)
    }
  }

  private val debugPath = Path()
  private val debugPaint: Paint
  private val shadowColor = ColorUtils.setAlphaComponent(Color.BLACK, 80)

  private val path = Path()
  private val linePaint = Paint(ANTI_ALIAS_FLAG)

  private val levels = SparseArray<PointF>()
  private val levelDrawables = SparseArray<Array<Drawable>>()

  private val levelIconWidth = context.dp2px(66f)
  private val levelIconHeight = context.dp2px(66f)

  init {
    linePaint.color = Color.WHITE
    linePaint.style = Style.STROKE
    linePaint.strokeWidth = context.dp2pxF(2f)

    debugPaint = Paint(linePaint)
    debugPaint.color = Color.parseColor("#ffc6c6c6")

    addLevel(R.drawable.ic_evolve_map_1, 0.12f, 0.08f)
    addLevel(R.drawable.ic_evolve_map_2, 0.42f, 0.11f)
    addLevel(R.drawable.ic_evolve_map_3, 0.7f, 0.25f)
    addLevel(R.drawable.ic_evolve_map_4, 0.38f, 0.38f)
    addLevel(R.drawable.ic_evolve_map_5, 0.06f, 0.49f)
    addLevel(R.drawable.ic_evolve_map_6, 0.37f, 0.65f)
    addLevel(R.drawable.ic_evolve_map_7, 0.72f, 0.75f)

    setWillNotDraw(false)
  }

  fun addLevel(@DrawableRes icon: Int, x: Float, y: Float) {
    levels.put(icon, PointF(x, y))
    levelDrawables.put(
        icon,
        arrayOf(
            withBounds(
                context.getVectorIcon(lockedMap.get(icon)),
                levelIconWidth, levelIconHeight
            ),

            withBounds(
                context.getVectorIcon(icon),
                levelIconWidth, levelIconHeight
            )
        )
    )
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)

    layoutLevels2()
  }

  private fun withBounds(d: VectorDrawableCompat, w: Int, h: Int): VectorDrawableCompat {
    d.setBounds(0, 0, w, h)
    return d
  }

  private fun getControlPoint(x1: Float, y1: Float, x2: Float, y2: Float): Pair<Float, Float> {
    return (x2 + x1) * 0.5f to ((y2 + y1) * 0.5f - context.dp2pxF(4f))
  }

  private fun getLevelCoordinates(i: Int): PointF {
    val (x, y) = levels.valueAt(i)
    return PointF(x * width, y * height)
  }

  private fun layoutLevels2() {
    path.reset();

    var (x1, y1) = getLevelCoordinates(0)
    var (x2, y2) = getLevelCoordinates(1)
    var (x3, y3) = getLevelCoordinates(2)
    var (x4, y4) = getLevelCoordinates(3)
    var (x5, y5) = getLevelCoordinates(4)
    var (x6, y6) = getLevelCoordinates(5)
    var (x7, y7) = getLevelCoordinates(6)

    y1 += levelIconHeight * 0.5f

    //y2 += levelIconHeight * 0.5f
    x2 += levelIconWidth

    //y3 += levelIconHeight * 0.5f
    x3 += levelIconWidth * 0.5f

    val iconEmptySpace = context.dp2pxF(4f)

    path.moveTo(x1 +  iconEmptySpace, y1 - iconEmptySpace)
    path.quadTo(
        x2, y2,
        x3, y3 + iconEmptySpace
    )

    y3 += levelIconHeight
    x5 += levelIconWidth * 0.5f

    path.moveTo(x3, y3 -  iconEmptySpace)
    path.cubicTo(
        x4 + levelIconWidth, y4 + levelIconHeight * 0.75f,
        x4, y4 + levelIconHeight * 0.25f,
        x5, y5 +  iconEmptySpace
    )

    path.moveTo(x5, y5 + levelIconHeight - iconEmptySpace)
    path.cubicTo(
        x6, y6 + levelIconHeight * 0.25f,
        x6 + levelIconWidth, y6 + levelIconHeight * 0.55f,
        x7+ iconEmptySpace, y7 + levelIconHeight * 0.5f
    )
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    linePaint.setShadowLayer(context.dp2pxF(2f), 0f, context.dp2pxF(1f), shadowColor)
    canvas.drawPath(path, linePaint)

    linePaint.clearShadowLayer()
    linePaint.setShadowLayer(context.dp2pxF(2f), 1f, 2f, shadowColor)
    for (i in 0 until levelDrawables.size()) {
      val d = levelDrawables.valueAt(i)[0] // take locked icon to draw

      canvas.withStateSaved {
        val (x1, y1) = getLevelCoordinates(i)
        translate(x1, y1)

        d.draw(canvas)
      }
    }
//    canvas.drawPath(debugPath, debugPaint)
  }
}
