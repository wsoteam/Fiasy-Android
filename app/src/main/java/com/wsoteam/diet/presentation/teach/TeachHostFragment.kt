package com.wsoteam.diet.presentation.teach

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.wsoteam.diet.R
import com.wsoteam.diet.common.diary.FoodWork
import com.wsoteam.diet.presentation.search.basket.db.BasketEntity
import com.wsoteam.diet.presentation.teach.fragments.*
import java.util.*


class TeachHostFragment : Fragment() {

    companion object {
        const val BREAKFAST = 0
        const val LUNCH = 1
        const val DINNER = 2
        const val SNACK = 3

        const val ACTION = "ACTION"
        const val ACTION_START_MEAL_DIALOG = "ACTION_START_MEAL_DIALOG"
        const val ACTION_START_FOOD_DIALOG = "ACTION_START_FOOD_DIALOG"
        const val ACTION_START_SEARCH_DIALOG = "ACTION_START_SEARCH_DIALOG"
        const val ACTION_START_BASKET_DIALOG = "ACTION_START_BASKET_DIALOG"
        const val ACTION_SAVE_FOOD = "ACTION_SAVE_FOOD"

        const val INTENT_FOOD = "INTENT_FOOD"
        const val INTENT_MEAL = "INTENT_MEAL"

        const val REQUEST_BLUR = 110
        const val REQUEST_MEAL = 111
        const val REQUEST_SEARCH = 112
        const val REQUEST_DETAIL = 113
        const val REQUEST_DONE = 114
        const val REQUEST_BASKET = 115
        const val REQUEST_ACHIEVEMENT = 116

    }


    private val blur = BlurDialogFragment()
    private var basketEntity: BasketEntity? = null
    private var finalSave: Animation? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        startDialog(blur, REQUEST_BLUR)
        startDialog(TeachMealDialogFragment(), REQUEST_MEAL)

        finalSave = AnimationUtils.loadAnimation(activity, R.anim.anim_meas_update)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                REQUEST_MEAL -> {
                    val args = Bundle()
                    args.putInt(INTENT_MEAL,
                            data!!.getIntExtra(INTENT_MEAL, 0))
                    startDialog(TeachSearchDialogFragment(), REQUEST_SEARCH, args)

                }
                REQUEST_SEARCH -> {

                    when (data?.getStringExtra(ACTION)) {
                        ACTION_START_MEAL_DIALOG -> {
                            startDialog(TeachMealDialogFragment(), REQUEST_MEAL)
                        }
                        ACTION_START_FOOD_DIALOG -> {
                            val food = data!!.getSerializableExtra(INTENT_FOOD) as BasketEntity
                            val args = Bundle()
                            args.putInt(INTENT_MEAL,
                                    data!!.getIntExtra(INTENT_MEAL, 0))
                            args.putSerializable(INTENT_FOOD, food)
                            startDialog(TeachFoodDetailDialogFragment(), REQUEST_DETAIL, args)

                        }
                    }
                }
                REQUEST_DETAIL -> {
                    when (data?.getStringExtra(ACTION)) {
                        ACTION_START_SEARCH_DIALOG -> {
                            val args = Bundle()
                            args.putInt(INTENT_MEAL,
                                    data!!.getIntExtra(INTENT_MEAL, 0))
                            startDialog(TeachSearchDialogFragment(), REQUEST_SEARCH, args)
                        }
                        ACTION_START_BASKET_DIALOG -> {
                            startDialog(TeachBasketDialogFragment(), REQUEST_BASKET)
                            basketEntity = data!!.getSerializableExtra(INTENT_FOOD) as BasketEntity
                        }

                    }
                }

                REQUEST_DONE -> {
                    when (data?.getStringExtra(ACTION)) {
                        ACTION_START_BASKET_DIALOG -> {
                            startDialog(TeachBasketDialogFragment(), REQUEST_BASKET)
                        }

                    }

                }

                REQUEST_BASKET -> {
                    when (data?.getStringExtra(ACTION)) {
                        ACTION_SAVE_FOOD -> {
                            val calendar = Calendar.getInstance()
                            basketEntity?.apply {
                                FoodWork.saveItem(this,
                                        calendar.get(Calendar.DAY_OF_MONTH),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.YEAR))
                                runCountdown()
                            }


                        }

                    }

                }
            }

        }

        if (resultCode == Activity.RESULT_CANCELED) {

            //TODO
//            TeachUtil.setOpen(context, false)
            blur.dismiss()
        }
    }

    private fun startDialog(dialogFragment: DialogFragment, request: Int, args: Bundle) {
        dialogFragment.arguments = args
        startDialog(dialogFragment, request)
    }

    private fun startDialog(dialogFragment: DialogFragment, requestCode: Int) {
        val fm = activity?.supportFragmentManager
        dialogFragment.setTargetFragment(this, requestCode)
        fm?.let { dialogFragment.show(fm, dialogFragment.javaClass.name) }
    }

    private fun runCountdown() {
        val toast = Toast(context)
        toast.view = LayoutInflater.from(context).inflate(R.layout.toast_meas_update, null)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        val title = toast.view.findViewById<TextView>(R.id.title)
        val ellipse = toast.view.findViewById<ImageView>(R.id.ivEllipse)
        title.text = resources.getString(R.string.srch_save_list)
        ellipse.animation = finalSave
        toast.show()
        object : CountDownTimer(2000, 100) {
            override fun onTick(l: Long) {

            }

            override fun onFinish() {
                blur.dismiss()
                startDialog(AchievementDialogFragment(), REQUEST_ACHIEVEMENT)
            }
        }.start()
    }

}
