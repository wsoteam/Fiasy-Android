package com.losing.weight.presentation.training.dialog


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.losing.weight.R
import kotlinx.android.synthetic.main.dialog_fragment_abort_exercise.*


class AbortExerciseDialogFragment : DialogFragment() {

    companion object {
        private const val FRAGMENT_TAG = "abort_exercise_dialog"
        private const val requestCode = 328

        const val REQUEST_CODE_LEAVE = 4569
        const val REQUEST_CODE_CANCEL = 789

        fun newInstance() = AbortExerciseDialogFragment()

        fun show(fragment: Fragment?): AbortExerciseDialogFragment? {
            if (fragment == null) return null
            val dialog = newInstance()

            val fm = fragment.fragmentManager
            dialog.setTargetFragment(fragment, requestCode)
            fm?.let { dialog.show(fm, FRAGMENT_TAG) }
            return dialog
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_abort_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        leave.setOnClickListener {
            targetFragment?.onActivityResult(REQUEST_CODE_LEAVE, Activity.RESULT_OK, null)
            setTargetFragment(null, 0)
            dismiss()
        }

        cancel.setOnClickListener {
            targetFragment?.onActivityResult(REQUEST_CODE_CANCEL, Activity.RESULT_OK, null)
            setTargetFragment(null, 0)
            dismiss()
        }
    }
}