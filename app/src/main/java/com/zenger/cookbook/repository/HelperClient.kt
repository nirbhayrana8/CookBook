package com.zenger.cookbook.repository

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


object Util {

    @BindingAdapter("loadImage")
    @JvmStatic
    fun ImageView.loadImage(url: String) {
        Glide.with(this)
                .load(url)
                .centerCrop()
                .into(this)
    }
}