package com.wsoteam.diet.presentation.teach.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.wsoteam.diet.R
import com.wsoteam.diet.common.Analytics.EventProperties
import com.wsoteam.diet.common.Analytics.Events
import com.wsoteam.diet.presentation.teach.TeachHostFragment
import com.wsoteam.diet.presentation.teach.TeachUtil
import kotlinx.android.synthetic.main.fragment_teach_basket.*


class TeachBasketDialogFragment : DialogFragment() {

    private var _style = STYLE_NO_TITLE
    private var _theme = R.style.FullScreenDialog_NoStatusBar

    private var isCanceled = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(_style, _theme)
        Events.logTeach(EventProperties.teach_done)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_teach_basket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCounter.text = getPaintedString(1)
        tvAddToBasket.setOnClickListener {
            val intent = Intent()
            intent.putExtra(TeachHostFragment.ACTION, TeachHostFragment.ACTION_SAVE_FOOD)
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
            TeachUtil.setOpen(context, false)
            isCanceled = false
            dismiss()
        }
    }

    private fun getPaintedString(size: Int): Spannable {
        val string = getString(R.string.srch_basket_card, size)
        val positionPaint = string.indexOf(" ") + 1
        val spannable = SpannableString(string)
        spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.srch_painted_string)),
                positionPaint,
                string.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(UnderlineSpan(), 0, string.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

    override fun onDetach() {
        super.onDetach()
        if(isCanceled)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, Intent())
    }

}
