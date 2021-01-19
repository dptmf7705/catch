package com.catchapp.catchapp.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.catchapp.catchapp.R;

import xyz.sangcomz.indicatordecorator.IndicatorItemDecoration;
import xyz.sangcomz.indicatordecorator.shape.CircleIndicator;

public class PagerRecyclerView extends RecyclerView {

    public PagerRecyclerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public PagerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PagerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        addItemDecoration(getItemIndicator());
        new PagerSnapHelper().attachToRecyclerView(this);
    }

    private IndicatorItemDecoration getItemIndicator() {
        CircleIndicator indicator = new CircleIndicator();

        indicator.setColorActive(getResources().getColor(R.color.colorSkyBlue, null));
        indicator.setColorInactive(getResources().getColor(R.color.colorGrayLight, null));

        IndicatorItemDecoration decoration = new IndicatorItemDecoration();
        decoration.setIndicatorShape(indicator);

        return decoration;
    }

}
