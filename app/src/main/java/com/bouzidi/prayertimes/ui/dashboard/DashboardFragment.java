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
import com.bouzidi.prayertimes.ui.timingtable.TimingTableActivity;
import com.bouzidi.prayertimes.ui.qibla.CompassActivity;

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
        String heads[] = {"Qibla Direction", "Prayer Calendar", "Asma Al Husna", "The Holy Quran", "Zakat Calculator", "Dua and Azkar"};

        String subs[] = {"Locate the direction of the qibla", "Times for calendar month", "99 Names of God", "Recite & Listen the Holy Quran", "How much Zakat to pay", "Recite Dua and Azkar"};

        int images[] = {R.drawable.ic_compass_24dp, R.drawable.ic_table_24dp, R.drawable.ic_alah_24dp, R.drawable.ic_quran_24dp,
                R.drawable.ic_donation_24dp, R.drawable.ic_dua_hands};

        Intent intents[] = {new Intent(getActivity(), CompassActivity.class), new Intent(getActivity(), TimingTableActivity.class), null, null,
                null, null};

        for (int count = 0; count < heads.length; count++) {
            DashModel dashModel = new DashModel();
            dashModel.setHead(heads[count]);
            dashModel.setSub(subs[count]);
            dashModel.setImage(images[count]);
            dashModel.setIntent(intents[count]);
            dashModelArrayList.add(dashModel);
            //this should be retrieved in our adapter
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        dashAdapter = new DashAdapter(dashModelArrayList, getActivity());
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(dashAdapter);

        return root;
    }
}
