package com.hbouzidi.fiveprayers.ui.timingtable.tableview.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.hbouzidi.fiveprayers.R;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class RowHeaderViewHolder extends AbstractViewHolder {
    @NonNull
    public final TextView row_header_textview;

    public RowHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        row_header_textview = itemView.findViewById(R.id.row_header_textview);
    }
}
