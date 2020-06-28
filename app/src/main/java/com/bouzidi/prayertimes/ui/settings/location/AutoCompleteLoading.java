package com.bouzidi.prayertimes.ui.settings.location;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

public class AutoCompleteLoading extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    private ProgressBar mLoadingIndicator;

    public AutoCompleteLoading(Context context) {
        super(context);
    }

    public void setLoadingIndicator(ProgressBar view) {
        mLoadingIndicator = view;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        super.performFiltering(text, keyCode);
    }

    @Override
    public void onFilterComplete(int count) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        super.onFilterComplete(count);
    }
}