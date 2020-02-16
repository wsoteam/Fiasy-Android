package com.wsoteam.diet.utils

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat

fun ViewGroup.inflate(@LayoutRes id: Int, attach: Boolean = true): View {
  return LayoutInflater.from(context)
      .inflate(id, this, attach)
}

fun View.hideKeyboard() {
  ContextCompat.getSystemService(context, InputMethodManager::class.java)
      ?.hideSoftInputFromWindow(windowToken, 0)
}

fun Canvas.withStateSaved(block: Canvas.() -> Unit) {
  val index = save()
  block()
  restoreToCount(index)
}