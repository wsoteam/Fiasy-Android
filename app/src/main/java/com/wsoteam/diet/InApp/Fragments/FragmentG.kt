package com.wsoteam.diet.InApp.Fragments


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.wsoteam.diet.Authenticate.POJO.Box
import com.wsoteam.diet.InApp.BasePremiumFragment
import com.wsoteam.diet.R
import com.wsoteam.diet.utils.RichTextUtils
import com.wsoteam.diet.utils.formatSpannable
import kotlinx.android.synthetic.main.fragment_g.*


class FragmentG : BasePremiumFragment(R.layout.fragment_g) {

    companion object {
        @JvmStatic
        fun newInstance(box: Box) : Fragment{
            val bundle = Bundle()
            bundle.putSerializable(TAG_BOX, box)
            val fragment = FragmentG()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getCurrentSKU(): String = "trial_long_pic_3d_3m_2k"
    override fun setPrice(sku: com.android.billingclient.api.SkuDetails?) {
        sku?.also {
            priceText.text = getString(R.string.mount_premium, it.price)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardViewPremG.setBackgroundResource(R.drawable.prem_card_backgound)
        premiumGClose.setOnClickListener {
            activity?.onBackPressed()
        }

        buyPremiumG.setOnClickListener {
            buy(getCurrentSKU())
        }

        val actionSignIn = RichTextUtils.RichText(getString(R.string.premium_feature_title__span).toUpperCase())
                .bold()
        val spannable = getString(R.string.premium_text_g).formatSpannable(actionSignIn.text())
        prem_g_main_txt.text = spannable
    }
}