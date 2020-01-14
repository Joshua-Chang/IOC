package com.example.library;

import android.view.View;

public abstract class DebouncingOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        // 调用抽象方法
        doClick(v);
    }

    // 抽象方法，子类new必须实现该方法
    protected abstract void doClick(View v);
}