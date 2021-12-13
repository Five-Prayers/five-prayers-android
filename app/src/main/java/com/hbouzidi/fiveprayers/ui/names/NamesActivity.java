package com.hbouzidi.fiveprayers.ui.names;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hbouzidi.fiveprayers.R;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class NamesActivity extends AppCompatActivity {

    private RecyclerView namesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_names);

        namesRecyclerView = findViewById(R.id.names_recycler_view);
        ConstraintLayout namesContentLayout = findViewById(R.id.names_content_layout);

        ViewCompat.setLayoutDirection(namesContentLayout, ViewCompat.LAYOUT_DIRECTION_RTL);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());

        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        NamesAdapter surahAdapter = new NamesAdapter();

        namesRecyclerView.setLayoutManager(linearLayoutManager);
        namesRecyclerView.setAdapter(surahAdapter);
        namesRecyclerView.setHasFixedSize(true);
    }
}