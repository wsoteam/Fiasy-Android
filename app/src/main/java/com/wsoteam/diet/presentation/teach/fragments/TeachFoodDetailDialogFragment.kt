package com.wsoteam.diet.presentation.teach.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.wsoteam.diet.Config
import com.wsoteam.diet.R
import com.wsoteam.diet.common.Analytics.EventProperties
import com.wsoteam.diet.common.Analytics.Events
import com.wsoteam.diet.presentation.search.basket.db.BasketEntity
import com.wsoteam.diet.presentation.teach.TeachHostFragment
import com.wsoteam.diet.presentation.teach.TeachUtil
import com.wsoteam.diet.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_teach_detail.*
import java.util.ArrayList
import kotlin.math.round


class TeachFoodDetailDialogFragment: DialogFragment() {

    var weight = 0
    var spinnerId = 0
    private var portionsSizes = mutableListOf<Int>()
    private val MINIMAL_PORTION = 1
    private val EMPTY_FIELD = -1
    private var position = 0

    private var isCanceled = true

    private var basketEntity: BasketEntity = BasketEntity()

    private var _style = STYLE_NO_TITLE
    private var _theme = R.style.TeachDialog_NoStatusBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(_style, _theme)
        Events.logTeach(EventProperties.teach_weight)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_teach_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        include.setBackgroundResource(R.drawable.teach_cardview_back)

        btnDone.setOnClickListener {
            if (weight > 0) {
                val intent = Intent()
                intent.putExtra(TeachHostFragment.ACTION, TeachHostFragment.ACTION_START_BASKET_DIALOG)
                intent.putExtra(TeachHostFragment.INTENT_MEAL, spinnerId)
                basketEntity.eatingType = spinnerId
                intent.putExtra(TeachHostFragment.INTENT_FOOD, prepareToSave(
                        edtWeightCalculate.text.toString(), tvProtCalculate.text.toString(),
                        tvFatCalculate.text.toString(), tvCarboCalculate.text.toString(),
                        tvKcalCalculate.text.toString(), spnFood.selectedItemPosition
                ))
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                isCanceled = false
                dismiss()
            }
        }

        teachCancel.setOnClickListener {
            Events.logTeach(EventProperties.teach_skip)
            TeachUtil.setOpen(context, false)
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, Intent())
            dismiss() }

        teachNext.setOnClickListener {
            if (weight > 0){
                include.setBackgroundResource(R.drawable.teach_cardview_back_2)
                btnDone.visibility = View.VISIBLE
                teachCancel.visibility = View.GONE
                teachNext.visibility = View.GONE
                firstTxt.visibility = View.GONE
                secondTxt.visibility = View.VISIBLE
                teachConstr.setPadding(0,0,0, 30)
                edtWeightCalculate.hideKeyboard()
                Events.logTeach(EventProperties.teach_ready)
            }
        }

        ivBack.setOnClickListener {
            val intent = Intent()
            intent.putExtra(TeachHostFragment.ACTION, TeachHostFragment.ACTION_START_SEARCH_DIALOG)
            intent.putExtra(TeachHostFragment.INTENT_MEAL, spinnerId)
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
            isCanceled = false
            dismiss()
        }

        basketEntity =
        arguments?.getSerializable(TeachHostFragment.INTENT_FOOD) as BasketEntity

        setValue(basketEntity)
        bindSpinnerChoiceEating()
        handlePortions(basketEntity)

        edtWeightCalculate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
            ) {
            }

            override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
            ) {
               if (s != null && s.isNotEmpty()){
                   calculate(s)


               }
                else {
                   calculate("0")
//                   include.setBackgroundResource(R.drawable.teach_cardview_back)
//                   btnDone.visibility = View.GONE
//                   teachConstr.setPadding(0,0,0, 0)
               }
            }
        })
    }

    private fun bindSpinnerChoiceEating() {
        val adapter = ArrayAdapter(context!!,
                R.layout.item_spinner_food_search, resources.getStringArray(R.array.srch_eat_list))
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown_food_search)
        spnFood.adapter = adapter

        try {
            spnFood.setSelection(arguments!!.getInt(TeachHostFragment.INTENT_MEAL, 0))
        } catch (e: NullPointerException) {
            Log.e("error", "arguments == null", e)
        }

        spinnerId = spnFood.selectedItemPosition

        spnFood.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                spinnerId = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun setValue(basketEntity: BasketEntity?){
        tvTitle.text = basketEntity?.name

    }

    internal fun calculate(weight: CharSequence) {
        val count = weight.toString().toInt()
        val portionSize = portionsSizes[position]

        val prot = round(count * portionSize.toDouble() * basketEntity.proteins).toInt().toString() + " " +
                getString(R.string.srch_gramm)
        val carbo = (round(count * portionSize.toDouble() * basketEntity.carbohydrates).toInt().toString()
                + " "
                + getString(R.string.srch_gramm))
        val fats = (round(count * portionSize.toDouble() * basketEntity.fats).toInt().toString()
                + " "
                + getString(R.string.srch_gramm))
        val kcal = round(count * portionSize.toDouble() * basketEntity.calories).toInt().toString()

        this.weight = count
        showResult(kcal, prot, carbo, fats)
    }

    fun showResult(
            kcal: String,
            proteins: String,
            carbo: String,
            fats: String
    ) {
        tvKcalCalculate.text = kcal
        tvProtCalculate.text = proteins
        tvCarboCalculate.text = carbo
        tvFatCalculate.text = fats
    }

    internal fun handlePortions(basketEntity: BasketEntity) {
        val names = ArrayList<String>()

        if (basketEntity.sizePortion > Config.DEFAULT_WEIGHT) {
            var unit = context?.getString(R.string.srch_gramm)
            if (basketEntity.isLiquid) {
                unit = context?.getString(R.string.srch_ml)
            }
            portionsSizes.add(basketEntity.sizePortion)
            names.add(basketEntity.namePortion + " (" + basketEntity.sizePortion.toString() + unit + ")")
        }

        if (basketEntity.namePortion == Config.DEFAULT_PORTION_NAME && basketEntity.countPortions == Config.DEFAULT_PORTION) {
            basketEntity.makeAtomicDefault()
        }

        if (basketEntity.namePortion != null && basketEntity.namePortion != Config.DEFAULT_PORTION_NAME && basketEntity.sizePortion != 0 && basketEntity.countPortions != 0) {
            basketEntity.makeAtomic(basketEntity.countPortions, basketEntity.sizePortion)
        }

        portionsSizes.add(MINIMAL_PORTION)
        if (basketEntity.isLiquid) {
            names.add(getString(R.string.srch_ml))
        } else {
            names.add(getString(R.string.srch_gramm))
        }

        fillPortionSpinner(names)
    }

    fun fillPortionSpinner(names: ArrayList<String>) {
        val adapter = ArrayAdapter(context!!, R.layout.item_spinner_portions, names)
        adapter.setDropDownViewResource(R.layout.item_spinner_portion_drop)
        spnPortions.adapter = adapter
        spnPortions.setSelection(0)
        if (names.size != 1) {
            spnPortions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        p2: Int,
                        p3: Long
                ) {
                    changePortion(p2)
                }
            }
        } else {
            spnPortions.isEnabled = false
            spnPortions.background = null
        }
    }

    fun changePortion(position: Int) {
        this.position = position
//        getViewState().refreshCalculating()
    }

    fun prepareToSave(weight: String, prot: String, fats: String, carbo: String, kcal: String,
                      selectedItemPosition: Int) : BasketEntity {
        val calories = Integer.parseInt(kcal)
        val carbohydrates = Integer.parseInt(carbo.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
        val proteins = Integer.parseInt(prot.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
        val fat = Integer.parseInt(fats.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
        val countPortions = Integer.parseInt(weight)

        basketEntity.calories = calories.toDouble()
        basketEntity.fats = fat.toDouble()
        basketEntity.carbohydrates = carbohydrates.toDouble()
        basketEntity.proteins = proteins.toDouble()
        basketEntity.countPortions = countPortions
        basketEntity.eatingType = selectedItemPosition

        if (portionsSizes[position] == MINIMAL_PORTION) {
            basketEntity.deepId = -1
            basketEntity.namePortion = Config.DEFAULT_CUSTOM_NAME
            basketEntity.sizePortion = 1
        }

        if (basketEntity.sugar != EMPTY_FIELD.toDouble()) {
            basketEntity.sugar = countPortions.toDouble() * portionsSizes[position].toDouble() * basketEntity.sugar
        }

        if (basketEntity.saturatedFats != EMPTY_FIELD.toDouble()) {
            basketEntity.saturatedFats = countPortions.toDouble() * portionsSizes[position].toDouble() * basketEntity.saturatedFats
        }

        if (basketEntity.monoUnSaturatedFats != EMPTY_FIELD.toDouble()) {
            basketEntity.monoUnSaturatedFats = countPortions.toDouble() * portionsSizes[position].toDouble() * basketEntity.monoUnSaturatedFats
        }

        if (basketEntity.polyUnSaturatedFats != EMPTY_FIELD.toDouble()) {
            basketEntity.polyUnSaturatedFats = countPortions.toDouble() * portionsSizes[position].toDouble() * basketEntity.polyUnSaturatedFats
        }

        if (basketEntity.cholesterol != EMPTY_FIELD.toDouble()) {
            basketEntity.cholesterol = countPortions.toDouble() * portionsSizes[position].toDouble() * basketEntity.cholesterol
        }

        if (basketEntity.cellulose != EMPTY_FIELD.toDouble()) {
            basketEntity.cellulose = countPortions.toDouble() * portionsSizes[position].toDouble() * basketEntity.cellulose
        }

        if (basketEntity.sodium != EMPTY_FIELD.toDouble()) {
            basketEntity.sodium = countPortions.toDouble() * portionsSizes[position].toDouble() * basketEntity.sodium
        }

        if (basketEntity.pottassium != EMPTY_FIELD.toDouble()) {
            basketEntity.pottassium = countPortions.toDouble() * portionsSizes[position].toDouble() * basketEntity.pottassium
        }
//        saveEntity(basketEntity, selectedItemPosition)
        return basketEntity
    }

    override fun onDetach() {
        super.onDetach()

        if(isCanceled)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, Intent())
    }

}