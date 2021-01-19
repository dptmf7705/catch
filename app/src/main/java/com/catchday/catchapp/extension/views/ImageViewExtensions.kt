package com.catchday.catchapp.extension.views

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.catchday.catchapp.R
import java.io.File


const val placeholder: Int = R.drawable.shape_placeholder

val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)

const val thumbnailSizeMultiplier = 0.25f

fun ImageView.loadImageUrlCenterCrop(imageUrl: String?) {
    if (imageUrl.isNullOrBlank()) {
        return loadPlaceHolder(placeholder)
    }

    Glide.with(context).load(imageUrl)
        .applyRequestOptions()
        .centerCrop()
        .into(this)
}

fun ImageView.loadImageUrl(imageUrl: String?) {
    if (imageUrl.isNullOrBlank()) {
        return loadPlaceHolder(placeholder)
    }

    if (height == 0) {
        return runAfterPreDraw { loadImageUrl(imageUrl) }
    }

    Glide.with(context).load(imageUrl)
        .applyRequestOptionsWithHeight(height)
        .into(this)
}

fun ImageView.loadImageUri(imageUri: Uri?) {
    if (imageUri == null) {
        return loadPlaceHolder(placeholder)
    }

    Glide.with(context).load(imageUri)
        .applyRequestOptions()
        .centerCrop()
        .into(this)
}


fun ImageView.loadImageUrlCircleCrop(imageUrl: String?) {
    if (imageUrl.isNullOrBlank()) {
        return loadPlaceHolder(placeholder)
    }

    if (height == 0) {
        return runAfterPreDraw { loadImageUrlCircleCrop(imageUrl) }
    }

    Glide.with(context).load(placeholder)
        .applyRequestOptionsWithHeight(height)
        .circleCrop()
        .into(this)
}

fun ImageView.loadImageFile(file: File?) {
    if (file == null) {
        return loadPlaceHolder(placeholder)
    }

    Glide.with(context).load(file)
        .applyRequestOptions()
        .into(this)
}

fun ImageView.loadImageBitmap(bitmap: Bitmap?) {
    if (bitmap == null) {
        return loadPlaceHolder(placeholder)
    }

    Glide.with(context).load(bitmap)
        .applyRequestOptionsWithSize(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
        .into(TargetAdjustImageSize(this))
}

fun ImageView.loadImageBitmapWrapContent(bitmap: Bitmap?) {
    if (bitmap == null) {
        return loadPlaceHolder(placeholder)
    }

    if (width == 0) {
        return runAfterPreDraw { loadImageBitmapWrapContent(bitmap) }
    }

    Glide.with(context).load(bitmap)
        .applyRequestOptionsWithWidth(width)
        .into(TargetAdjustImageSize(this))
}

fun ImageView.loadImageFileAdjustViewBounds(file: File?, doOnImageLoaded: (() -> Unit)? = null) {
    if (file == null) {
        return loadPlaceHolder(placeholder)
    }

    if (width == 0) {
        return runAfterPreDraw { loadImageFileAdjustViewBounds(file, doOnImageLoaded) }
    }

    Glide.with(context).load(file)
        .applyRequestOptionsWithWidth(width)
        .into(object : CustomTarget<Drawable>() {

            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                if (resource.intrinsicHeight > height) {
                    val ratio = height / resource.intrinsicHeight.toDouble()
                    val aspectWidth = (resource.intrinsicWidth * ratio).toInt()
                    val aspectHeight = (resource.intrinsicHeight * ratio).toInt()

                    val imageView = this@loadImageFileAdjustViewBounds

                    imageView.updateLayoutParams {
                        width = aspectWidth
                        height = aspectHeight
                    }

                    Glide.with(context).load(resource)
                        .applyRequestOptionsWithSize(aspectWidth, aspectHeight)
                        .into(imageView)

                    doOnImageLoaded?.invoke()

                } else {
                    updateLayoutParams {
                        this.width = resource.intrinsicWidth
                        this.height = resource.intrinsicHeight
                    }

                    setImageDrawable(resource)
                    doOnImageLoaded?.invoke()
                }
            }
        })
}

fun ImageView.loadImageUrlAdjustWidth(imageUrl: String?) {
    if (imageUrl.isNullOrBlank()) {
        return loadPlaceHolder(placeholder)
    }

    if (width == 0) {
        return runAfterPreDraw { loadImageUrlAdjustWidth(imageUrl) }
    }

    Glide.with(context).load(imageUrl)
        .applyRequestOptionsWithWidth(width)
        .into(TargetAdjustImageSize(this@loadImageUrlAdjustWidth))
}

private fun ImageView.loadPlaceHolder(resId: Int) {
    Glide.with(context)
        .load(resId)
        .into(this)
}

private fun <T> RequestBuilder<T>.applyRequestOptions() =
    placeholder(placeholder)
        .thumbnail(thumbnailSizeMultiplier)
        .apply(requestOptions)

private fun <T> RequestBuilder<T>.applyRequestOptionsWithWidth(width: Int) =
    override(width, Target.SIZE_ORIGINAL).applyRequestOptions()

private fun <T> RequestBuilder<T>.applyRequestOptionsWithHeight(height: Int) =
    override(Target.SIZE_ORIGINAL, height).applyRequestOptions()

private fun <T> RequestBuilder<T>.applyRequestOptionsWithSize(width: Int, height: Int) =
    override(width, height).applyRequestOptions()


private class TargetAdjustImageSize(
    private val imageView: ImageView
) : CustomTarget<Drawable>() {

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        imageView.updateLayoutParams {
            this.width = resource.intrinsicWidth
            this.height = resource.intrinsicHeight
        }
        imageView.setImageDrawable(resource)
    }

    override fun onLoadCleared(placeholder: Drawable?) {}
}
