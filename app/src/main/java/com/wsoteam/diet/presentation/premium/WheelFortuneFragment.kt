package com.wsoteam.diet.presentation.premium



import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.wsoteam.diet.R
import android.view.animation.AccelerateDecelerateInterpolator
import android.animation.ObjectAnimator
import com.wsoteam.diet.Authenticate.POJO.Box
import kotlinx.android.synthetic.main.fragment_wheel_fortune.*


class WheelFortuneFragment : Fragment(R.layout.fragment_wheel_fortune) {

    companion object{
        private const val TAG_BOX = "TAG_BOX"

        fun newInstance(box: Box): WheelFortuneFragment{
            val bundle = Bundle()
            bundle.putSerializable(TAG_BOX, box)
            val fragment = WheelFortuneFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wheel_fortune.setOnClickListener {
            val animation = ObjectAnimator.ofFloat(arrow_fortune, "rotation", 0.0f, 360f)
            animation.duration = 2400
            animation.repeatCount = ObjectAnimator.INFINITE
            animation.interpolator = AccelerateDecelerateInterpolator()
            animation.start()
        }


    }
}
