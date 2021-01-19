package com.catchday.catchapp.ui.common.databinding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


@Suppress("UNCHECKED_CAST")
@BindingAdapter("items")
fun <T, VH : RecyclerView.ViewHolder> setItems(recyclerView: RecyclerView, items: List<T>?) {
    val adapter = recyclerView.adapter as? ListAdapter<T, VH>
    adapter?.submitList(items)
}