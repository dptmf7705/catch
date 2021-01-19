package com.catchapp.catchapp.ui.main.notice;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.catchapp.catchapp.databinding.ItemNoticeBinding;

public class NoticeItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemNoticeBinding binding;

    NoticeItemViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }

    void bindTo(@NonNull final Notice item) {
        binding.setItem(item);
    }
}