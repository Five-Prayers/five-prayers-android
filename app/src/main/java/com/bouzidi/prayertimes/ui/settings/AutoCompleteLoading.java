package com.bouzidi.prayertimes.ui.settings;

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
        // the AutoCompleteTextview is about to start the filtering so show
        // the ProgressPager
        mLoadingIndicator.setVisibility(View.VISIBLE);
        super.performFiltering(text, keyCode);
    }

    @Override
    public void onFilterComplete(int count) {
        // the AutoCompleteTextView has done its job and it's about to show
        // the drop down so close/hide the ProgreeBar
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        super.onFilterComplete(count);
    }

}