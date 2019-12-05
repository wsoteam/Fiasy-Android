package com.wsoteam.diet.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat

fun ViewGroup.inflate(@LayoutRes id: Int, attach: Boolean = true): View {
    return LayoutInflater.from(context).inflate(id, this, attach)
}

fun View.hideKeyboard() {
    ContextCompat.getSystemService(context, InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.onTextChanged(block: EditText.(text: CharSequence) -> Unit): TextWatcher {
    val watcher = object: TextWatcher{
        override fun afterTextChanged(s: Editable) {

        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            block(s)
        }
    }

    return watcher
}