package com.hbouzidi.fiveprayers.ui.timingtable.tableview.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.ui.timingtable.tableview.model.ColumnHeader;

import java.util.Objects;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class ColumnHeaderViewHolder extends AbstractSorterViewHolder {

    @NonNull
    private final LinearLayout column_header_container;
    @NonNull
    private final TextView column_header_textview;

    public ColumnHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        column_header_textview = itemView.findViewById(R.id.column_header_textView);
        column_header_container = itemView.findViewById(R.id.column_header_container);
    }

    public void setColumnHeader(@Nullable ColumnHeader columnHeader) {
        column_header_textview.setText(String.valueOf(Objects.requireNonNull(columnHeader).getData()));


        column_header_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        column_header_textview.requestLayout();
    }
}
