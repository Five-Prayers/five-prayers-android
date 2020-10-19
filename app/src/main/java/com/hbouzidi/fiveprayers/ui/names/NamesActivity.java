package com.hbouzidi.fiveprayers.ui.names;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.R;

public class NamesActivity extends AppCompatActivity {

    private RecyclerView namesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_names);

        namesRecyclerView = findViewById(R.id.names_recycler_view);

        Toolbar toolbar = findViewById(R.id.names_toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(v -> finish());

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