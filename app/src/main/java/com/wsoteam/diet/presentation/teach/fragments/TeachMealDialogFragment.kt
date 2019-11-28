package com.wsoteam.diet.presentation.teach.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.wsoteam.diet.R
import com.wsoteam.diet.presentation.teach.TeachHostFragment
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment
import kotlinx.android.synthetic.main.fragment_teach_meal.*

class TeachMealDialogFragment : SupportBlurDialogFragment() {


    private var _style = STYLE_NO_TITLE
    private var _theme = R.style.TeachDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(_style, _theme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_teach_meal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        teachCancel.setOnClickListener {
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, Intent())
            dismiss() }
        linearLayoutBreakfast.setOnClickListener { nextDialog(TeachHostFragment.BREAKFAST) }
        linearLayoutLunch.setOnClickListener { nextDialog(TeachHostFragment.LUNCH) }
        linearLayoutDinner.setOnClickListener { nextDialog(TeachHostFragment.DINNER) }
        linearLayoutSnack.setOnClickListener { nextDialog(TeachHostFragment.SNACK) }

    }

    private fun nextDialog(meal: Int) {
        val intent = Intent()
        intent.putExtra(TeachMealDialogFragment().javaClass.name, meal)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }

}