package com.hbouzidi.fiveprayers.ui.quran.index;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.database.QuranBookmarkRegistry;
import com.hbouzidi.fiveprayers.quran.dto.BookmarkType;
import com.hbouzidi.fiveprayers.quran.dto.QuranBookmark;
import com.hbouzidi.fiveprayers.quran.dto.QuranPage;
import com.hbouzidi.fiveprayers.quran.dto.Surah;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class BookmarkListAdapter extends RecyclerView.Adapter<BookmarkListAdapter.Holder> {

    private BookmarkListner bookmarkListner;
    private List<Surah> surahs;
    private List<QuranBookmark> quranBookmarks;
    private RecyclerView mRecyclerView;
    private final Context context;

     @Inject QuranBookmarkRegistry quranBookmarkRegistry;

    public BookmarkListAdapter(Context context) {
        surahs = new ArrayList<>();
        quranBookmarks = new ArrayList<>();
        this.context = context;
    }

    public void setBookmarkListner(BookmarkListner bookmarkListner) {
        this.bookmarkListner = bookmarkListner;
    }

    public void setSurahList(List<Surah> newList) {
        surahs = new ArrayList<>(newList);
    }

    public void setQuranBookmarks(List<QuranBookmark> newList) {
        quranBookmarks = new ArrayList<>(newList);
    }

    public List<Surah> getSurahs() {
        return surahs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ((FivePrayerApplication) viewGroup.getContext().getApplicationContext())
                .adapterComponent
                .inject(this);

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bookmark_item, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        QuranBookmark quranBookmark = quranBookmarks.get(i);
        QuranPage quranPage = quranBookmark.getQuranPage();

        String surahName = surahs.get(quranPage.getSurahNumber() - 1).getName();

        StringBuilder juzInfoBuilder = new StringBuilder();
        int hizbNumber = quranPage.getRubHizb() / 4;
        juzInfoBuilder
                .append("الجزء ")
                .append(quranPage.getJuz())
                .append(" - الحزب ")
                .append(hizbNumber == 0 ? 1 : hizbNumber);

        holder.bookmarkSurahNameTextView.setText(surahName);
        holder.bookmarkSurahDetailsTextView.setText(juzInfoBuilder);
        holder.bookmarkPageNumTextView.setText(String.valueOf(quranPage.getPageNum()));

        if (BookmarkType.USER_MADE.equals(quranBookmark.getBookmarkType())) {
            holder.bookmarkImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_bookmark_filled, null));
        }
    }

    @Override
    public int getItemCount() {
        return quranBookmarks.size();
    }

    interface BookmarkListner {
        void onBookmark(int pos);
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView bookmarkImageView;
        CardView bookmarkItemCardBiew;
        TextView bookmarkSurahNameTextView;
        TextView bookmarkSurahDetailsTextView;
        TextView bookmarkPageNumTextView;

        public Holder(@NonNull View itemView) {
            super(itemView);

            bookmarkItemCardBiew = itemView.findViewById(R.id.bookmark_item_card_view);
            bookmarkSurahNameTextView = itemView.findViewById(R.id.bookmark_surah_name_text_view);
            bookmarkSurahDetailsTextView = itemView.findViewById(R.id.bookmark_surah_details_text_view);
            bookmarkPageNumTextView = itemView.findViewById(R.id.bookmark_page_num_text_view);
            bookmarkImageView = itemView.findViewById(R.id.bookmark_image_view);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                bookmarkListner.onBookmark(quranBookmarks.get(pos).getQuranPage().getPageNum());
            });

            itemView.setOnLongClickListener(v -> {
                int pos = getBindingAdapterPosition();
                BookmarkType bookmarkType = quranBookmarks.get(pos).getBookmarkType();

                if (BookmarkType.AUTOMATIC.equals(bookmarkType)) {
                    return false;
                } else {
                    onItemRemove(pos);
                    bookmarkItemCardBiew.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, null));
                    return true;
                }
            });
        }

        private void onItemRemove(int position) {
            Snackbar snackbar = Snackbar
                    .make(mRecyclerView, context.getText(R.string.msg_delete_bookmark_confirmation), Snackbar.LENGTH_LONG)
                    .setAction(context.getText(R.string.common_accept), view -> {
                        quranBookmarkRegistry.deleteBookmark(quranBookmarks.get(position).getQuranPage().getPageNum());
                        quranBookmarks.remove(position);
                        notifyItemRemoved(position);
                    }).addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            bookmarkItemCardBiew.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.white, null));
                        }

                        @Override
                        public void onShown(Snackbar sb) {
                            super.onShown(sb);

                        }
                    });
            snackbar.show();
        }
    }
}