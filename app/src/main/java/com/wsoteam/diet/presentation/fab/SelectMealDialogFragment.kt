package com.wsoteam.diet.presentation.fab


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.wsoteam.diet.Config
import com.wsoteam.diet.R
import com.wsoteam.diet.presentation.diary.DiaryViewModel
import com.wsoteam.diet.presentation.search.ParentActivity
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment
import kotlinx.android.synthetic.main.select_meal_dialog_fragment.*
import java.util.*


class SelectMealDialogFragment : SupportBlurDialogFragment() {

    val BREAKFAST = 0
    val LUNCH = 1
    val DINNER = 2
    val SNACK = 3

    private var _style = STYLE_NO_TITLE
    private var _theme = R.style.FullScreenDialog_NoStatusBar

    private var date : String = ParentActivity.prepareDate(Calendar.getInstance())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(_style, _theme)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.select_meal_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutBreakfast.setOnClickListener {addFood(BREAKFAST)}
        linearLayoutLunch.setOnClickListener { addFood(LUNCH)}
        linearLayoutDinner.setOnClickListener { addFood(DINNER)}
        linearLayoutSnack.setOnClickListener { addFood(SNACK)}

        DiaryViewModel.selectedDate.observeForever(dateObserver)
    }

    private val dateObserver = Observer<DiaryViewModel.DiaryDay> { date ->
        if (date != null) {
            this.date = ParentActivity.prepareDate(date.calendar)
        }
    }

    private fun addFood(meal: Int){

        val intent = Intent(context, ParentActivity::class.java)
        intent.putExtra(Config.INTENT_DATE_FOR_SAVE, date)
        intent.putExtra(Config.TAG_CHOISE_EATING, meal)
        context?.startActivity(intent)
        dismiss()
    }
}
