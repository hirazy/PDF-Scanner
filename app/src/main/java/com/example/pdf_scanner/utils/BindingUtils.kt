package com.example.pdf_scanner.utils

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.bumptech.glide.Glide
import com.example.pdf_scanner.R
import com.squareup.picasso.Picasso


@BindingAdapter("image")
fun loadImage(image: ImageView, url: String) {
    Glide.with(image)
        .load(url).placeholder(R.mipmap.ic_loadding)
        .centerCrop()
        .into(image)
}

@BindingAdapter("imageUrl", "error")
fun loadImg(view: ImageView, url: String, error: Drawable) {
    Picasso.get().load(url).error(error).into(view)
}


@InverseBindingAdapter(attribute = "textChange", event = "textAttrChanged")
fun getTextChange(view: EditText): String {
    val text = view.text.toString().replace("\\D+".toRegex(), "")
    if (TextUtils.isEmpty(text))  {
        return "0"
    }
    val number = (text).toDouble()
    return String.format("%,.0f", number)
}
