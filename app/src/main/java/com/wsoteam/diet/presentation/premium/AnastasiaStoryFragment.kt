package com.wsoteam.diet.presentation.premium

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.wsoteam.diet.Authenticate.POJO.Box
import com.wsoteam.diet.R
import kotlinx.android.synthetic.main.fragment_anastasia_story.*


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar2.setNavigationIcon(R.drawable.arrow_back_gray)
        toolbar2.setNavigationOnClickListener { activity?.onBackPressed() }

        scroll.setOnScrollChangeListener {
            v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            appbar.setLiftable(scrollY == 0) }
    }
}
