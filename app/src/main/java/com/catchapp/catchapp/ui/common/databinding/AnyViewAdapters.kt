package com.catchapp.catchapp.ui.common.databinding

import android.view.View
import androidx.databinding.BindingAdapter
import com.catchapp.catchapp.extension.views.changeVisibility


@BindingAdapter("activate")
fun setActivated(view: View, activate: Boolean?) {
    activate?.let { view.isActivated = it }
}

@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Boolean?) {
    view.changeVisibility(visible)
}

@BindingAdapter("selected")
fun setSelected(view: View, selected: Boolean?) {
    selected?.let { view.isSelected = it }
}
