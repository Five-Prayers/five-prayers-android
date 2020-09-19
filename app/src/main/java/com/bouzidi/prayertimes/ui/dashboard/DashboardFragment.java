package com.bouzidi.prayertimes.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.ui.calendar.CalendarActivity;
import com.bouzidi.prayertimes.ui.names.NamesActivity;
import com.bouzidi.prayertimes.ui.qibla.CompassActivity;
import com.bouzidi.prayertimes.ui.quran.surahs.QuranActivity;
import com.bouzidi.prayertimes.ui.timingtable.TimingTableActivity;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    ArrayList<DashModel> dashModelArrayList;
    private RecyclerView recyclerView;
    DashAdapter dashAdapter;
    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = root.findViewById(R.id.rv1);

        dashModelArrayList = new ArrayList<>();
        String heads[] = {"Qibla Direction", "Prayer Times Table", "Hijri Calendar", "The Holy Quran", "Asma Al Husna"};

        String subs[] = {"Locate the direction of the Qibla", "Monthly Prayer table", "Hijri dates and Holidays", "Recite & Listen the Holy Quran", "99 Names of God"};

        int images[] = {R.drawable.ic_compass_24dp, R.drawable.ic_table_24dp, R.drawable.ic_calendar_24dp, R.drawable.ic_quran_24dp,
                R.drawable.ic_alah_24dp};

        Intent intents[] = {new Intent(getActivity(), CompassActivity.class),
                new Intent(getActivity(), TimingTableActivity.class),
                new Intent(getActivity(), CalendarActivity.class),
                new Intent(getActivity(), QuranActivity.class),
                new Intent(getActivity(), NamesActivity.class)
        };

        for (int count = 0; count < heads.length; count++) {
            DashModel dashModel = new DashModel();
            dashModel.setHead(heads[count]);
            dashModel.setSub(subs[count]);
            dashModel.setImage(images[count]);
            dashModel.setIntent(intents[count]);
            dashModelArrayList.add(dashModel);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        dashAdapter = new DashAdapter(dashModelArrayList, getActivity());
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(dashAdapter);

        return root;
    }
}
