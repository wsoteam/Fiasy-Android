package com.wsoteam.diet.presentation.teach.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wsoteam.diet.R
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment

class TeachFoodDetailDialogFragment: SupportBlurDialogFragment() {

    private var _style = STYLE_NO_TITLE
    private var _theme = R.style.TeachDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(_style, _theme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_teach_detail, container, false)
    }

}