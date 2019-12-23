package com.wsoteam.diet.presentation

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.collection.SparseArrayCompat
import androidx.transition.TransitionManager
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.wsoteam.diet.BuildConfig
import com.wsoteam.diet.R
import com.wsoteam.diet.R.string
import com.wsoteam.diet.presentation.auth.AuthStrategyFragment
import com.wsoteam.diet.presentation.auth.MainAuthNewActivity
import com.wsoteam.diet.presentation.auth.ResetPasswordAuthStrategy
import com.wsoteam.diet.utils.InputValidation
import com.wsoteam.diet.utils.InputValidation.EmailValidation
import com.wsoteam.diet.utils.InputValidation.MinLengthValidation
import com.wsoteam.diet.utils.OnTextChanged
import com.wsoteam.diet.utils.hideKeyboard
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.Arrays
import java.util.concurrent.TimeUnit.SECONDS

class ResetPasswordFragment : AuthStrategyFragment() {
  companion object {
    private val formValidators = SparseArrayCompat<List<InputValidation>>()

    init {
      formValidators.put(R.id.email, Arrays.asList(
          MinLengthValidation(string.constraint_error_username_min_length, 5),
          EmailValidation(string.write_email)
      ));

      formValidators.put(R.id.password, Arrays.asList(
          MinLengthValidation(R.string.constraint_error_password_min_length, 5)
      ));

      formValidators.put(R.id.password2, Arrays.asList(
          MinLengthValidation(R.string.constraint_error_password_min_length, 5)
      ))
    }
  }

  private lateinit var mask: View
  private lateinit var loader: ImageView

  private lateinit var action: TextView
  private lateinit var email: TextInputLayout
  private lateinit var password1: TextInputLayout
  private lateinit var password2: TextInputLayout
  private lateinit var hint: TextView
  private lateinit var validationLabel: TextView

  private val validateForm = Runnable {
    (action.parent as View).apply {
      isActivated = validateForm(true)
      isClickable = true
    }
  }

  private val watcher = OnTextChanged {
    clearErrors()

    action.removeCallbacks(validateForm)
    action.postDelayed(validateForm, 200)
  }

  private val formValidators = SparseArrayCompat<List<InputValidation>>()

  private val passwordMatchValidation = object : InputValidation {
    override fun validate(input: EditText): CharSequence? {
      if (!input.text.toString().equals(password1.editText?.text?.toString() ?: "")) {
        return input.context.getString(R.string.password_dont_match)
      } else {
        return null
      }
    }
  }

  private var formInputs = ArrayList<TextInputLayout>()

  fun clearErrors() {
    validationLabel.text = ""
    validationLabel.visibility = View.INVISIBLE

    arrayOf(email, password1, password2).forEach {
      it.error = null
    }
  }

  override fun onCreateView(inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_auth_reset, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    mask = view.findViewById(R.id.mask)
    loader = view.findViewById(R.id.loader)

    action = view.findViewById<ViewGroup>(R.id.auth_strategy_reset)
      .getChildAt(0) as TextView

    view.setOnClickListener {
      it.hideKeyboard()
    }

    email = view.findViewById(R.id.email)
    hint = view.findViewById(R.id.hint)
    password1 = view.findViewById(R.id.password1)
    password2 = view.findViewById(R.id.password2)
    validationLabel = view.findViewById(R.id.validation_label)

    email.editText?.addTextChangedListener(watcher)
    password1.editText?.addTextChangedListener(watcher)
    password2.editText?.addTextChangedListener(watcher)

    if (BuildConfig.DEBUG) {
      (action.parent as View).setOnLongClickListener {
        if (isEnteringPassword()) {
          arguments = Bundle.EMPTY
          enterEmail()
        } else {
          arguments = Bundle().apply {
            putString("", "")
          }

          enterNewPassword()
        }

        return@setOnLongClickListener true
      }
    }

    //    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

    view.findViewById<View>(R.id.close).setOnClickListener {
      if(true) {
        (requireActivity() as MainAuthNewActivity).signIn()
      } else { // if someone asks to add it back :(
        AlertDialog.Builder(it.context)
          .setTitle(getString(R.string.close_form_dialog_title))
          .setMessage(getString(R.string.close_form_dialog_message))
          .setPositiveButton(getString(R.string.close_dialog)) { _, _ ->
            (requireActivity() as MainAuthNewActivity).signIn()
          }
          .setNegativeButton(getString(R.string.continue_form_fill_button), null)
          .show()
      }
    }

    arguments?.apply {
      val userEmail = getString("email", null)

      if (userEmail != null) {
        email.editText?.setText(userEmail)
        enterNewPassword()
      } else {
        enterEmail()
      }
    } ?: enterEmail()

    (action.parent as View).setOnClickListener {
      clearErrors()

      if (!validateForm(true)) {
        return@setOnClickListener;
      }

      val strategy = if (authStrategy == null) {
        getStrategyByType(ResetPasswordAuthStrategy::class.java).apply {
          authStrategy = this
        }
      } else {
        authStrategy as ResetPasswordAuthStrategy
      }

      if (!isEnteringPassword()) {
        showSuccessState(true)

        disposables.add(strategy.sendVerificationCode(email.editText?.text?.toString()?.trim() ?: "")
          .timeout(10, SECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              {
//                showSuccessState(false)

                AlertDialog.Builder(requireContext())
                  .setTitle("Отправлено")
                  .setMessage("Мы отправили вам письмо на почту")
                  .setPositiveButton(android.R.string.ok) { _, _ ->
                    (requireActivity() as MainAuthNewActivity).signIn()
                  }
                  .show()

              },
              { exception ->
                if (BuildConfig.DEBUG) {
                  exception.printStackTrace()
                }

                showSuccessState(false)

                if (exception is FirebaseAuthInvalidUserException) {
                  email.error = "Пользователь не зарегистрирован"
                } else {
                  validationLabel.visibility = View.VISIBLE
                  validationLabel.text = "Не удалось отправить письмо, " +
                          "проверьте соединение с интернетом"
                }
              }
          ))
      } else {
        strategy.setCode(arguments?.getString("code") ?: "")
        strategy.setEmail(arguments?.getString("email") ?: "")
        strategy.setPassword(password1.editText?.text?.toString()?.trim() ?: "")

        authorize(ResetPasswordAuthStrategy::class.java)
      }
    }
  }

  fun showSuccessState(visible: Boolean) {
    if (visible) {
      loader.visibility = View.VISIBLE
      mask.visibility = View.VISIBLE

      val drawable = loader.getDrawable()
      if (drawable is Animatable) {
        (drawable as Animatable).start()
      }
    } else {
      loader.visibility = View.GONE
      mask.visibility = View.GONE
    }
  }

  fun isEnteringPassword(): Boolean {
    return email.visibility == View.GONE
  }

  fun enterNewPassword() {
    TransitionManager.beginDelayedTransition(email.parent as ViewGroup)

    showSuccessState(false)

    password1.visibility = View.VISIBLE
    password2.visibility = View.VISIBLE
    email.visibility = View.GONE
    hint.visibility = View.GONE

    action.isEnabled = false
    action.setText(R.string.recoverPassword_enter)

    formInputs.clear()
    formInputs.addAll(listOf(password1, password2))

    formValidators.clear()
    formValidators.put(R.id.password, Arrays.asList(
        MinLengthValidation(R.string.constraint_error_password_min_length, 5)
    ))

    formValidators.put(R.id.password2, Arrays.asList(
        MinLengthValidation(R.string.constraint_error_password_min_length, 5),
        passwordMatchValidation
    ))

    (action.parent as View).apply {
      isActivated = validateForm(false)
      isClickable = true
    }
  }

  fun enterEmail() {
    TransitionManager.beginDelayedTransition(email.parent as ViewGroup)

    showSuccessState(false)

    password1.visibility = View.GONE
    password2.visibility = View.GONE
    email.visibility = View.VISIBLE
    hint.visibility = View.VISIBLE

    action.isEnabled = false
    action.setText(R.string.reset_send)

    formInputs.clear()
    formInputs.addAll(listOf(email))

    formValidators.clear()
    formValidators.put(R.id.email, Arrays.asList(
        MinLengthValidation(string.constraint_error_username_min_length, 5),
        EmailValidation(string.write_email)
    ));

    (action.parent as View).apply {
      isActivated = validateForm(false)
      isClickable = true
    }
  }

  protected fun validateForm(displayError: Boolean): Boolean {
    if (view == null) {
      return false
    }

    var hasErrors = false

    for (target in formInputs) {
      val validators = formValidators.get(target.getId()) ?: continue

      for (validator in validators) {
        val error = validator.validate(target.getEditText())

        if (!TextUtils.isEmpty(error)) {
          if (BuildConfig.DEBUG) {
            Log.d("ManualAuth", String.format("%s throws an error: %s",
                validator.javaClass.getSimpleName(), error))
          }

          if (displayError) {
            if(target.id == R.id.email){
              target.error = error
            } else {
              validationLabel.text = error
              validationLabel.visibility = View.VISIBLE
            }
            hasErrors = true
            break
          } else {
            return false
          }
        } else {
          if (BuildConfig.DEBUG) {
            Log.d("ManualAuth", String.format("%s passed",
                validator.javaClass.getSimpleName()))
          }
        }
      }
    }

    return !hasErrors
  }
}
