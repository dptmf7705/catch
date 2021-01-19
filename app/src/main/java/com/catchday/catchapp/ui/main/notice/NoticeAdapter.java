package com.catchday.catchapp.ui.main.notice;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.catchday.catchapp.R;

public class NoticeAdapter extends ListAdapter<Notice, NoticeItemViewHolder> {

    public NoticeAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public NoticeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoticeItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notice, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeItemViewHolder holder, int position) {
        final Notice item = getItem(holder.getBindingAdapterPosition());
        holder.bindTo(item);
    }

    private static final DiffUtil.ItemCallback<Notice> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Notice>() {
                @Override
                public boolean areItemsTheSame(@NonNull Notice oldItem,
                                               @NonNull Notice newItem) {
                    return oldItem.getImageResId() == newItem.getImageResId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Notice oldItem,
                                                  @NonNull Notice newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
