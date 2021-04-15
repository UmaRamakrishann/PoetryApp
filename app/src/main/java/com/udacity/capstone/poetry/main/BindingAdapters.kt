package com.udacity.capstone.poetry.main

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.udacity.capstone.poetry.R

@BindingAdapter("favoriteIcon")
fun bindFavoriteImage(imageView: ImageView, isFavorite: Boolean) = if (isFavorite) {
    imageView.setImageResource(R.drawable.ic_red_heart)
    imageView.setContentDescription("Favorite icon")
} else {
    imageView.setImageResource(R.drawable.ic_grey_heart)
    imageView.setContentDescription("Not favorite icon")
}

@BindingAdapter("goneIfFalse")
fun goneIfFalse(view: View, it: Any?) {
    view.visibility = if (it == true) View.VISIBLE else View.GONE
}



