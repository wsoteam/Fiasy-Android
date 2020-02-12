package com.wsoteam.diet.presentation.profile.questions.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.internal.DebouncingOnClickListener
import com.google.android.material.textfield.TextInputLayout
import com.wsoteam.diet.Config
import com.wsoteam.diet.R
import com.wsoteam.diet.R.string
import com.wsoteam.diet.presentation.profile.questions.QuestionsActivity
import com.wsoteam.diet.utils.InputValidation
import com.wsoteam.diet.utils.InputValidation.MinLengthValidation
import com.wsoteam.diet.utils.hideKeyboard

class QuestionNameFragments : Fragment() {

    private lateinit var usernameView: TextInputLayout
    private lateinit var doneButton: View

    private val validators = listOf<InputValidation>(
            MinLengthValidation(string.constraint_error_user_profile_name_min_length, 3)
    )

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            doneButton.isEnabled = TextUtils.isEmpty(nextFormValidationError())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    private val actionListener = object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (TextUtils.isEmpty(nextFormValidationError()) && actionId == EditorInfo.IME_ACTION_DONE) {
                nextQuestion()
                return true
            } else {
                return false
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_question_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences(Config.ONBOARD_PROFILE, MODE_PRIVATE)

        view.setOnTouchListener { v, e ->
            v.hideKeyboard()

            /* return */ true
        }

        doneButton = view.findViewById(R.id.btnNext)
        doneButton.setOnClickListener(object : DebouncingOnClickListener() {
            override fun doClick(v: View) {
                nextQuestion()
            }
        })

        usernameView = view.findViewById(R.id.user_name)
        usernameView.editText?.addTextChangedListener(textWatcher)
        usernameView.editText?.setText(prefs.getString(Config.ONBOARD_PROFILE_NAME, ""))
        usernameView.editText?.setOnEditorActionListener(actionListener)
    }

    fun nextQuestion() {
        requireActivity().getSharedPreferences(Config.ONBOARD_PROFILE, MODE_PRIVATE)
                .edit()
                .putString(Config.ONBOARD_PROFILE_NAME, usernameView.editText?.text?.toString()
                        ?: "")
                .apply()

        view?.hideKeyboard()
        (requireActivity() as QuestionsActivity).nextQuestion()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        usernameView.editText?.removeTextChangedListener(textWatcher)
    }

    fun nextFormValidationError(): CharSequence? {
        validators.forEach {
            val error = it.validate(usernameView.editText)
            return if (TextUtils.isEmpty(error)) null else error
        }

        return null
    }

    companion object {
        @JvmStatic
        fun newInstance(): QuestionNameFragments {
            return QuestionNameFragments()
        }
    }
}