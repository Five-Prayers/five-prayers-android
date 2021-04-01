package com.hbouzidi.fiveprayers.ui.timingtable;

import android.os.Bundle;

import java.time.LocalDate;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class NextTimingTableFragment extends TimingTableBaseFragment {

    public NextTimingTableFragment() {
    }

    public static NextTimingTableFragment newInstance() {
        NextTimingTableFragment fragment = new NextTimingTableFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setTableDate() {
        this.tableLocalDate = LocalDate.now().plusMonths(1);
    }

    @Override
    protected void setScrollAndSelect() {
    }
}