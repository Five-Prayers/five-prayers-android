package com.hbouzidi.fiveprayers.ui.quran.index;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.quran.dto.BookmarkType;
import com.hbouzidi.fiveprayers.quran.dto.QuranBookmark;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class BookmarkIndexFragment extends QuranBaseIndexFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private RecyclerView userMadeBookmarkRecyclerView;
    private RecyclerView automaticBookmarkRecyclerView;
    private TextView automaticBookmarkSectionTitle;
    private TextView userMadeBookmarkSectionTitle;
    private BookmarkListAdapter automaticBookmarksListAdapter;
    private BookmarkListAdapter userMAdeBookmarksListAdapter;
    private LinearLayout emptyPlaceHolderLayout;
    private QuranIndexViewModel quranIndexViewModel;

    public BookmarkIndexFragment() {
    }

    public static BookmarkIndexFragment newInstance() {
        return new BookmarkIndexFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        ((FivePrayerApplication) context.getApplicationContext())
                .appComponent
                .quranComponent()
                .create()
                .inject(this);

        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_bookmark_list, container, false);

        emptyPlaceHolderLayout = rootView.findViewById(R.id.empty_place_holder_layout);
        userMadeBookmarkRecyclerView = rootView.findViewById(R.id.user_made_bookmark_recycler_view);
        automaticBookmarkRecyclerView = rootView.findViewById(R.id.automatic_bookmark_recycler_view);
        automaticBookmarkSectionTitle = rootView.findViewById(R.id.automatic_bookmark_section_title);
        userMadeBookmarkSectionTitle = rootView.findViewById(R.id.user_made_bookmark_section_title);

        quranIndexViewModel = viewModelFactory.create(QuranIndexViewModel.class);

        quranIndexViewModel.getSurahs().observe(getViewLifecycleOwner(), surahs -> this.surahs = surahs);
        quranIndexViewModel.getQuranPages().observe(getViewLifecycleOwner(), quranPages -> this.quranPages = quranPages);

        quranIndexViewModel.getQuranBookmarks().observe(getViewLifecycleOwner(), this::updateRecyclerViews);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(quranIndexViewModel != null) {
            quranIndexViewModel.updateLiveData(requireContext());
        }
    }

    private void updateRecyclerViews(List<QuranBookmark> bookmarks) {
        List<QuranBookmark> automaticBookmarks = PreferencesHelper.getSortedAutomaticBookmarkList(requireContext());

        ArrayList<QuranBookmark> userMadeBookmarks = bookmarks
                .stream()
                .filter(quranBookmark -> quranBookmark.getBookmarkType().equals(BookmarkType.USER_MADE))
                .sorted(Comparator.comparing(QuranBookmark::getTimestamps).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        automaticBookmarkSectionTitle.setVisibility(automaticBookmarks.isEmpty() ? View.INVISIBLE : View.VISIBLE);
        userMadeBookmarkSectionTitle.setVisibility(userMadeBookmarks.isEmpty() ? View.INVISIBLE : View.VISIBLE);
        emptyPlaceHolderLayout.setVisibility((userMadeBookmarks.isEmpty() && automaticBookmarks.isEmpty()) ? View.VISIBLE : View.INVISIBLE);

        if (automaticBookmarksListAdapter != null && userMAdeBookmarksListAdapter != null) {
            automaticBookmarksListAdapter.setQuranBookmarks(automaticBookmarks);
            automaticBookmarksListAdapter.notifyDataSetChanged();

            userMAdeBookmarksListAdapter.setQuranBookmarks(userMadeBookmarks);
            userMAdeBookmarksListAdapter.notifyDataSetChanged();
        } else {
            LinearLayoutManager automaticLinearLayoutManager = new LinearLayoutManager(requireContext());
            automaticBookmarksListAdapter = new BookmarkListAdapter(requireContext());
            automaticBookmarksListAdapter.setSurahList(surahs);
            automaticBookmarksListAdapter.setQuranBookmarks(automaticBookmarks);

            automaticBookmarkRecyclerView.setLayoutManager(automaticLinearLayoutManager);
            automaticBookmarkRecyclerView.setHasFixedSize(true);
            automaticBookmarkRecyclerView.setAdapter(automaticBookmarksListAdapter);

            automaticBookmarksListAdapter.setBookmarkListner(pos -> gotoSuraa(pos, surahs));

            LinearLayoutManager userMadeLinearLayoutManager = new LinearLayoutManager(requireContext());
            userMAdeBookmarksListAdapter = new BookmarkListAdapter(requireContext());
            userMAdeBookmarksListAdapter.setSurahList(surahs);
            userMAdeBookmarksListAdapter.setQuranBookmarks(userMadeBookmarks);

            userMadeBookmarkRecyclerView.setLayoutManager(userMadeLinearLayoutManager);
            userMadeBookmarkRecyclerView.setHasFixedSize(true);
            userMadeBookmarkRecyclerView.setAdapter(userMAdeBookmarksListAdapter);

            userMAdeBookmarksListAdapter.setBookmarkListner(pos -> gotoSuraa(pos, surahs));
        }
    }
}