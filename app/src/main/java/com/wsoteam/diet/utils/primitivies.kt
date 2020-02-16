package com.wsoteam.diet.utils

import android.graphics.PointF

val Boolean.invert
  get() = !this


fun PointF.component1(): Float = x
fun PointF.component2(): Float = y