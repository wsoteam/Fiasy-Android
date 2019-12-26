package com.wsoteam.diet.presentation.premium


import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.wsoteam.diet.R

class FiftyDiscountDialogFragment : DialogFragment() {

    companion object {
        private const val FRAGMENT_TAG = "new_training_dialog"

        fun newInstance() = FiftyDiscountDialogFragment()

        fun show(fragmentManager: FragmentManager?): FiftyDiscountDialogFragment? {
            if (fragmentManager == null) return null
            val dialog = newInstance()
            dialog.show(fragmentManager, FRAGMENT_TAG)
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_fifty_discount, container, false)
    }


}
