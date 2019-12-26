package com.wsoteam.diet.presentation.premium

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wsoteam.diet.Authenticate.POJO.Box
import com.wsoteam.diet.R
import com.wsoteam.diet.Recipes.POJO.GroupsHolder
import com.wsoteam.diet.Recipes.POJO.RecipeItem
import com.wsoteam.diet.Recipes.POJO.RecipesHolder
import com.wsoteam.diet.Recipes.v2.HorizontalRecipesAdapter
import kotlinx.android.synthetic.main.fragment_anastasia_story.*
import java.util.*


class AnastasiaStoryFragment : Fragment(R.layout.fragment_anastasia_story) {
    companion object{
        private const val TAG_BOX = "TAG_BOX"

        fun newInstance(box: Box): AnastasiaStoryFragment{
            val bundle = Bundle()
            bundle.putSerializable(TAG_BOX, box)
            val fragment = AnastasiaStoryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val adapter = HorizontalRecipesAdapter(getRecipes())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar2.setNavigationIcon(R.drawable.arrow_back_gray)
        toolbar2.setNavigationOnClickListener { activity?.onBackPressed() }

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        anastasia_recycler.layoutManager = linearLayoutManager
        anastasia_recycler.adapter = adapter


        appbar.setLiftable(true)
        scroll.setOnScrollChangeListener {
            v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            appbar.setLiftable(scrollY == 0) }

        next.setOnClickListener {
            activity?.onBackPressed()

        }
    }

    private fun getRecipes(): List<RecipeItem>{
        val list = LinkedList<RecipeItem>()
        list.add(RecipeItem("https://firebasestorage.googleapis.com/v0/b/diet-for-test.appspot.com/o/PlansRecipes%2F230%2F239.jpg?alt=media&token=2d015c38-fb4d-49ff-814e-fcc2f688db98",
                "Куриные филе с картофельным салатом", 650, false))
        list.add(RecipeItem("https://firebasestorage.googleapis.com/v0/b/diet-for-test.appspot.com/o/PlansRecipes%2F190%2F205.jpg?alt=media&token=1abc5b37-6310-4ee5-ad6b-56b538d45c60",
                "Свежие весенние рулеты", 521, false))
        return list
    }
}
