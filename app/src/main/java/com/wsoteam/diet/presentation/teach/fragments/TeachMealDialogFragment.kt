package com.wsoteam.diet.presentation.teach.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.wsoteam.diet.R
import com.wsoteam.diet.common.Analytics.EventProperties
import com.wsoteam.diet.common.Analytics.Events
import com.wsoteam.diet.presentation.teach.TeachHostFragment
import com.wsoteam.diet.presentation.teach.TeachHostFragment.Companion.INTENT_MEAL
import com.wsoteam.diet.presentation.teach.TeachUtil
import kotlinx.android.synthetic.main.fragment_teach_meal.*

class TeachMealDialogFragment : DialogFragment() {


    private var _style = STYLE_NO_TITLE
    private var _theme = R.style.FullScreenDialog_NoStatusBar

    private var isCanceled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(_style, _theme)
        Events.logTeach(EventProperties.teach_main)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_teach_meal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        teachCancel.setOnClickListener {
            Events.logTeach(EventProperties.teach_skip)
            TeachUtil.setOpen(context, false)
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, Intent())
            dismiss() }
        linearLayoutBreakfast.setOnClickListener { nextDialog(TeachHostFragment.BREAKFAST) }
        linearLayoutLunch.setOnClickListener { nextDialog(TeachHostFragment.LUNCH) }
        linearLayoutDinner.setOnClickListener { nextDialog(TeachHostFragment.DINNER) }
        linearLayoutSnack.setOnClickListener { nextDialog(TeachHostFragment.SNACK) }

    }

    private fun nextDialog(meal: Int) {
        val intent = Intent()
        intent.putExtra(INTENT_MEAL, meal)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        isCanceled = false
        dismiss()
    }

    override fun onDetach() {
        super.onDetach()
        if (isCanceled)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, Intent())
    }
}