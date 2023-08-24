package com.hbouzidi.fiveprayers.ui.invocations;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.quran.dto.Invocation;
import com.hbouzidi.fiveprayers.ui.BaseActivity;

import java.util.List;
import java.util.Map;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public abstract class InvocationActivity extends BaseActivity {

    private RecyclerView invocationRecyclerView;

    public abstract Map<String, List<Invocation>> getInvocation();

    public abstract int getToolbarTitleResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invocations);

        invocationRecyclerView = findViewById(R.id.invocations_recycler_view);
        TextView invocationsToolbarTitle = findViewById(R.id.invocations_toolbar_title);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());

        invocationsToolbarTitle.setText(getString(getToolbarTitleResourceId()));
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        InvocationsAdapter invocationsAdapter = new InvocationsAdapter(getInvocation());

        invocationRecyclerView.setLayoutManager(linearLayoutManager);
        invocationRecyclerView.setAdapter(invocationsAdapter);
        invocationRecyclerView.setHasFixedSize(true);
        invocationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        new PagerSnapHelper().attachToRecyclerView(invocationRecyclerView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            invocationRecyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }
}
