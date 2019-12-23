package com.wsoteam.diet.presentation.onboard

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wsoteam.diet.R
import com.wsoteam.diet.utils.ApiRequest.Feedback
import com.wsoteam.diet.utils.NetworkService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class OnBoardFeedbackActivity : AppCompatActivity(), OnCheckedChangeListener {
  private lateinit var next: View
  private lateinit var feedback: EditText

  private var selected: CompoundButton? = null
  private var disposables = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_onboard_questions)

    findViewById<View>(R.id.close).setOnClickListener { finish() }

    feedback = findViewById(R.id.custom_feedback)
    next = findViewById(R.id.next)
    next.setOnClickListener {
      if (selected != null) {
        next.isEnabled = false

        val text = if(selected?.id == R.id.other){
          feedback.text.toString()
        } else {
          selected?.text?.toString()
        }

        disposables.add(NetworkService.getInstance().api.sendFeedback(Feedback(text))
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .doOnEvent { t1, t2 ->
            next.isEnabled = true
          }
          .subscribe(
              {
                finish()
              },
              { error ->
                error.printStackTrace()

                Toast.makeText(this, "Не удалось отправить, попробуйте позже", Toast.LENGTH_SHORT).show()
              }
          ))

        return@setOnClickListener
      }
    }

    findViewById<ViewGroup>(R.id.container).let { container ->
      for (i in 0..container.childCount) {
        val target = container.getChildAt(i)

        if (target is CheckBox) {
          target.setOnCheckedChangeListener(this)
        }
      }
    }

    findViewById<CheckBox>(R.id.other)
      .setOnCheckedChangeListener { v, isChecked ->
        onCheckedChanged(v, isChecked)

        feedback.visibility = if (isChecked) {
          View.VISIBLE
        } else {
          View.GONE
        }

        feedback.requestFocus()
      }

    next.isEnabled = false
  }

  override fun onCheckedChanged(
    target: CompoundButton,
    isChecked: Boolean) {

    if (isChecked) {
      selected?.isChecked = false
      selected = target
    }

    next.isEnabled = selected != null
  }

}
