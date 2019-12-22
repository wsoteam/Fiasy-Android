package com.wsoteam.diet.presentation.fab

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wsoteam.diet.R
import com.wsoteam.diet.views.fabmenu.FloatingActionMenu
import com.wsoteam.diet.views.fabmenu.SubActionButton

class MainFabMenu {

    companion object{
        fun initFabMenu(activity: Activity, fab: FloatingActionButton,
                               listener: FloatingActionMenu.MenuStateChangeListener,
                               activityListener: View.OnClickListener,
                               measurementListener: View.OnClickListener,
                               mealListener: View.OnClickListener,
                               waterListener: View.OnClickListener,
                                onClickListener: View.OnClickListener):  FloatingActionMenu{

            val builder = SubActionButton.Builder(activity).setTheme(SubActionButton.THEME_LIGHTER)

            val activityBtn = builder.setContentView(getView(
                    activity, R.drawable.ic_fab_menu_activity, activity.getString(R.string.widget_user_activity_title)
            ), null).build()
            activityBtn.setOnClickListener(activityListener)

            val measurementBtn = builder.setContentView(getView(
                    activity, R.drawable.ic_fab_menu_measurement, activity.getString(R.string.fab_menu_measurement)
            )).build()
            measurementBtn.setOnClickListener(measurementListener)


            val mealBtn = builder.setContentView(getView(
                    activity, R.drawable.ic_fab_menu_meal, activity.getString(R.string.fab_menu_food)
            )).build()
            mealBtn.setOnClickListener(mealListener)


            val waterBtn = builder.setContentView(getView(
                    activity, R.drawable.ic_fab_menu_water, activity.getString(R.string.water)
            )).build()
            waterBtn.setOnClickListener(waterListener)

            return FloatingActionMenu.Builder(activity)
                    .addSubActionView(activityBtn)
                    .addSubActionView(measurementBtn)
                    .addSubActionView(mealBtn)
                    .addSubActionView(waterBtn)
                    .attachTo(fab)
                    .setStateChangeListener(listener)
                    .setEndAngle(260)
                    .setOnclickListener(onClickListener)
                    .build()
        }

        private fun getView(context: Context, imgResId: Int, str: String ): View{
            val view = LayoutInflater.from(context).inflate(R.layout.fab_menu_item, null)
            val img = view.findViewById<ImageView>(R.id.img)
            img.setImageResource(imgResId)
            val txt = view.findViewById<TextView>(R.id.txt)
            txt.text = str
            return view
        }
    }

}