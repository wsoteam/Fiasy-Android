package com.wsoteam.diet.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import com.wsoteam.diet.BuildConfig
import com.wsoteam.diet.R
import com.wsoteam.diet.presentation.auth.AuthStrategyFragment
import com.wsoteam.diet.presentation.auth.ResetPasswordAuthStrategy
import com.wsoteam.diet.utils.InputValidation
import com.wsoteam.diet.utils.onTextChanged
import com.wsoteam.diet.views.InAppNotification
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions

class ResetPasswordFragment : AuthStrategyFragment() {

    private val emailValidator by lazy {
        InputValidation.EmailValidation(R.string.constraint_error_username_email)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_reset, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reset = view.findViewById<View>(R.id.auth_strategy_reset)
        val email = view.findViewById<TextInputLayout>(R.id.email)

        email.editText?.onTextChanged { text ->
            reset.isEnabled = text.isEmpty()
        }

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        view.findViewById<View>(R.id.close).setOnClickListener {
            AlertDialog.Builder(it.context)
                    .setTitle(getString(R.string.close_form_dialog_title))
                    .setMessage(getString(R.string.close_form_dialog_message))
                    .setPositiveButton(getString(R.string.close_dialog)) { _, _ ->
                        requireFragmentManager().popBackStack()
                    }
                    .setNegativeButton(getString(R.string.continue_form_fill_button), null)
                    .show()
        }

        reset.setOnClickListener {
            val error = emailValidator.validate(email.editText)

            if (!error.isNullOrEmpty()) {
                notification.setText(error)
                notification.show(getView(), InAppNotification.DURATION_QUICK)
                return@setOnClickListener
            }

            val strategy = getStrategyByType(ResetPasswordAuthStrategy::class.java).apply {
                authStrategy = this
            }

            disposables.add(strategy.sendVerificationCode(email.editText?.text.toString() ?: "")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Functions.EMPTY_ACTION, Consumer { exception ->
                        if (BuildConfig.DEBUG) {
                            exception.printStackTrace()
                        }
                    }))
        }
    }
}
