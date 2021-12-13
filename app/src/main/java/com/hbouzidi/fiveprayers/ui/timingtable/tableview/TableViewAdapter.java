package com.hbouzidi.fiveprayers.ui.timingtable.tableview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.ui.timingtable.tableview.holder.CellViewHolder;
import com.hbouzidi.fiveprayers.ui.timingtable.tableview.holder.ColumnHeaderViewHolder;
import com.hbouzidi.fiveprayers.ui.timingtable.tableview.holder.RowHeaderViewHolder;
import com.hbouzidi.fiveprayers.ui.timingtable.tableview.model.Cell;
import com.hbouzidi.fiveprayers.ui.timingtable.tableview.model.ColumnHeader;
import com.hbouzidi.fiveprayers.ui.timingtable.tableview.model.RowHeader;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class TableViewAdapter extends AbstractTableAdapter<ColumnHeader, RowHeader, Cell> {


    private final String month;

    public TableViewAdapter(String month) {
        super();
        this.month = month;
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View layout;


        layout = inflater.inflate(R.layout.table_view_cell_layout, parent, false);
        return new CellViewHolder(layout);
    }


    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable Cell cellItemModel, int
            columnPosition, int rowPosition) {

        CellViewHolder viewHolder = (CellViewHolder) holder;
        viewHolder.setCell(cellItemModel);
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_view_column_header_layout, parent, false);

        return new ColumnHeaderViewHolder(layout);
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable ColumnHeader
            columnHeaderItemModel, int columnPosition) {

        ColumnHeaderViewHolder columnHeaderViewHolder = (ColumnHeaderViewHolder) holder;
        columnHeaderViewHolder.setColumnHeader(columnHeaderItemModel);
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_view_row_header_layout, parent, false);

        return new RowHeaderViewHolder(layout);
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable RowHeader rowHeaderItemModel,
                                          int rowPosition) {
        RowHeaderViewHolder rowHeaderViewHolder = (RowHeaderViewHolder) holder;
        String data = String.valueOf(Objects.requireNonNull(rowHeaderItemModel).getData());
        rowHeaderViewHolder.row_header_textview.setText(NumberFormat.getInstance(Locale.getDefault()).format(Integer.valueOf(data)));
    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_view_corner_layout, parent, false);

        TextView textView = view.findViewById(R.id.corner_title_text_view);
        textView.setText(month);

        return view;
    }


    @Override
    public int getColumnHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getCellItemViewType(int column) {
        return 0;
    }
}
