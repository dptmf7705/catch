package com.catchapp.catchapp.ui.common.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.catchapp.catchapp.extension.views.*
import java.io.File

@BindingAdapter("android:src")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("imageUrl")
fun loadImageFromUrl(imageView: ImageView, imageUrl: String?) {
    imageView.loadImageUrl(imageUrl)
}

@BindingAdapter("imageUrlWrapContent")
fun loadImageFromUrlAdjustWidth(imageView: ImageView, imageUrl: String?) {
    imageView.loadImageUrlAdjustWidth(imageUrl)
}

@BindingAdapter("imageFileWrapContent")
fun loadImageFromFileAdjustWidth(imageView: ImageView, filePath: String?) {
    filePath?.let { imageView.loadImageFileAdjustViewBounds(File(it)) }
}

@BindingAdapter("imageUrlCenterCrop")
fun loadImageFromUrlCenterCrop(imageView: ImageView, imageUrl: String?) {
    imageView.loadImageUrlCenterCrop(imageUrl)
}

@BindingAdapter("imageFilePath")
fun loadImageFromFile(imageView: ImageView, filePath: String?) {
    filePath?.let { imageView.loadImageFile(File(it)) }
}