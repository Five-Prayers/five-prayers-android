package com.hbouzidi.fiveprayers.ui.timingtable.tableview.holder;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.ui.timingtable.tableview.model.Cell;

import java.util.Objects;

public class CellViewHolder extends AbstractViewHolder {
    @NonNull
    private final TextView cell_textview;
    @NonNull
    private final LinearLayout cell_container;

    public CellViewHolder(@NonNull View itemView) {
        super(itemView);
        cell_textview = itemView.findViewById(R.id.cell_data);
        cell_container = itemView.findViewById(R.id.cell_container);
    }

    public void setCell(@Nullable Cell cell, Context context) {
        cell_textview.setText(String.valueOf(Objects.requireNonNull(cell).getData()));

        cell_container.getLayoutParams().width = (int) (150 * context.getResources().getDisplayMetrics().density);;
        cell_textview.requestLayout();
    }
}
