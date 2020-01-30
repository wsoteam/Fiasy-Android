package com.wsoteam.diet.presentation.teach.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.wsoteam.diet.R



class BlurDialogFragment : DialogFragment() {

    private var _style = STYLE_NO_TITLE
    private var _theme = R.style.FullScreenDialog_NoStatusBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(_style, _theme)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_blur_dialog, container, false)
    }


}
