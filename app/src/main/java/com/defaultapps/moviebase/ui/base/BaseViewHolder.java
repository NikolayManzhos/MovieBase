package com.defaultapps.moviebase.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;


public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
