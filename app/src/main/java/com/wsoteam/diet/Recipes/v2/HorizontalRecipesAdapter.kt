package com.wsoteam.diet.Recipes.v2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wsoteam.diet.R
import com.wsoteam.diet.Recipes.POJO.RecipeItem
import com.wsoteam.diet.utils.Img
import kotlinx.android.synthetic.main.recipe_horizontal_scroll.view.*

class HorizontalRecipesAdapter(private var listRecipe: List<RecipeItem>?):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    fun updateData(listRecipe: List<RecipeItem>?){
        this.listRecipe = listRecipe
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RecipeView(parent)
    }

    override fun getItemCount(): Int = listRecipe?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecipeView) holder.bind(listRecipe?.get(position))
    }

    class RecipeView(parent: ViewGroup)
        : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_horizontal_scroll, parent, false)) {

        private fun getContext(): Context = itemView.context

        fun bind(recipeItem: RecipeItem?){

            itemView.premiumLabel.visibility =
                    if (recipeItem?.isPremium == true) View.VISIBLE else View.GONE
            itemView.ivLike.visibility = View.GONE
            itemView.textRecipe.text = recipeItem?.name
            itemView.tvRecipeKK.text = recipeItem?.calories.toString()
            setImg(itemView.imageRecipe, recipeItem?.url ?: "", itemView.llBackground)

        }
        private fun setImg(img: ImageView, url: String, background: View) {
            Picasso.get()
                    .load(url)
                    .resizeDimen(R.dimen.receipt_container_width, R.dimen.receipt_container_height)
                    .centerCrop()
                    .into(img, object : Callback {
                        override fun onSuccess() {
                            Img.setBackGround(img.drawable, background)
                        }

                        override fun onError(e: Exception) {

                        }
                    })
        }

    }
}

