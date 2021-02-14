package com.hbouzidi.fiveprayers.ui.quran.pages;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.BuildConfig;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.database.QuranBookmarkRegistry;
import com.hbouzidi.fiveprayers.quran.dto.BookmarkType;
import com.hbouzidi.fiveprayers.quran.dto.QuranBookmark;
import com.hbouzidi.fiveprayers.quran.dto.QuranPage;
import com.hbouzidi.fiveprayers.quran.dto.Surah;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.Holder> {

    private List<QuranPage> quranPage = new ArrayList<>();
    private PageShown pageShown;
    private List<Surah> surahs;
    private Context context;

    private final int textColor;
    private final int backgroundColor;

    public PageAdapter(int textColor, int backgroundColor) {
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public void setPageShown(PageShown pageShown) {
        this.pageShown = pageShown;
    }

    public void setQuranPages(List<QuranPage> newList) {
        quranPage = new ArrayList<>(newList);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.page_item, viewGroup, false);
        context = viewGroup.getContext();
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int index) {
        QuranPage previousQuranPage = null;
        QuranPage currentQuranPage = quranPage.get(index);
        boolean displayQuarterInfo = false;

        if (index > 0) {
            previousQuranPage = quranPage.get(index - 1);
        }

        String surahName = getSurahNameFromIndex(currentQuranPage.getSurahNumber());

        int hizbQuarter = currentQuranPage.getRubHizb();

        if (previousQuranPage != null && (currentQuranPage.getRubHizb() != previousQuranPage.getRubHizb())) {
            displayQuarterInfo = true;
        }

        File file = new File(context.getFilesDir().getAbsolutePath(), BuildConfig.QURAN_IMAGES_FOLDER_NAME + "/" + currentQuranPage.getPageNum() + ".png");

        final Bitmap selectedImage = BitmapFactory.decodeFile(file.getAbsolutePath());

        holder.ayahsImageView.setImageBitmap(selectedImage);
        holder.ayahsImageView.setColorFilter(brightIt(textColor));

        holder.ayahsConstraintLayout.setBackgroundColor(backgroundColor);

        holder.pageNumTextView.setText(String.valueOf(currentQuranPage.getPageNum()));
        holder.surahNameTextView.setText(surahName);
        holder.juzTextView.setText(getHizbInfoBuilder(currentQuranPage, displayQuarterInfo, hizbQuarter));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            holder.ayahsConstraintLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            holder.topHeaderLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            holder.bottomFooterLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        holder.closeImageView.setOnClickListener(v -> ((Activity) context).finish());

        initializeBookmarkIcon(holder.bookmarkImageView, currentQuranPage);
    }

    @Override
    public int getItemCount() {
        return quranPage.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull Holder holder) {
        super.onViewAttachedToWindow(holder);
        pageShown.onDiplayed(holder.getBindingAdapterPosition(), holder);
    }

    public void setSurahs(List<Surah> surahs) {
        this.surahs = surahs;
    }

    interface PageShown {
        void onDiplayed(int pos, Holder holder);
    }

    static class Holder extends RecyclerView.ViewHolder {

        ImageView ayahsImageView;
        TextView surahNameTextView;
        TextView juzTextView;
        ConstraintLayout topHeaderLayout;
        ConstraintLayout bottomFooterLayout;
        TextView pageNumTextView;
        ConstraintLayout ayahsConstraintLayout;
        ImageView closeImageView;
        ImageView bookmarkImageView;

        public Holder(@NonNull View itemView) {
            super(itemView);

            ayahsImageView = itemView.findViewById(R.id.ayahs_image_view);
            surahNameTextView = itemView.findViewById(R.id.surah_name_text_view);
            juzTextView = itemView.findViewById(R.id.juz_text_view);
            topHeaderLayout = itemView.findViewById(R.id.top_header_layout);
            bottomFooterLayout = itemView.findViewById(R.id.bottom_footer_layout);
            ayahsConstraintLayout = itemView.findViewById(R.id.ayah_layout);
            pageNumTextView = itemView.findViewById(R.id.page_num_text_view);
            closeImageView = itemView.findViewById(R.id.close_image_view);
            bookmarkImageView = itemView.findViewById(R.id.bookmark_image_view);
        }
    }

    @NonNull
    private StringBuilder getHizbInfoBuilder(QuranPage item, boolean displayQuarterInfo, int hizbQuarter) {
        StringBuilder hizbInfoBuilder = new StringBuilder();
        hizbInfoBuilder
                .append("الجزء ")
                .append(item.getJuz());

        if (displayQuarterInfo) {
            int hizbNum = hizbQuarter / 4;

            if (hizbQuarter % 4 == 2) {
                hizbInfoBuilder.append(" ,1/4 ");
                hizbNum++;
            } else if (hizbQuarter % 4 == 3) {
                hizbInfoBuilder.append(" ,1/2 ");
                hizbNum++;
            } else if (hizbQuarter % 4 == 0) {
                hizbInfoBuilder.append(" ,3/4 ");
            } else {
                hizbInfoBuilder.append(" ,");
                hizbNum++;
            }

            hizbInfoBuilder.append("الحزب ");
            hizbInfoBuilder.append(hizbNum);
        }
        return hizbInfoBuilder;
    }

    private String getSurahNameFromIndex(int surahIndex) {
        return surahs.get(surahIndex - 1).getName();
    }

    private ColorMatrixColorFilter brightIt(int fb) {
        ColorMatrix cmB = new ColorMatrix();
        cmB.set(new float[]{
                1, 0, 0, 0, fb,
                0, 1, 0, 0, fb,
                0, 0, 1, 0, fb,
                0, 0, 0, 1, 0});

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(cmB);

        return new ColorMatrixColorFilter(colorMatrix);
    }

    private void initializeBookmarkIcon(ImageView bookmarkImageView, QuranPage quranPage) {
        QuranBookmarkRegistry quranBookmarkRegistry = QuranBookmarkRegistry.getInstance(context);

        QuranBookmark bookmarkByPageNumber = quranBookmarkRegistry.getBookmarkByPageNumber(quranPage.getPageNum(), BookmarkType.USER_MADE);

        bookmarkImageView.setImageResource((bookmarkByPageNumber == null) ? R.drawable.ic_bookmark_empty : R.drawable.ic_bookmark_filled);

        setBookmarkImageOnClickListener(bookmarkImageView, quranPage, bookmarkByPageNumber);
    }

    private void setBookmarkImageOnClickListener(ImageView bookmarkImageView, QuranPage quranPage, QuranBookmark bookmarkByPageNumber) {
        bookmarkImageView.setOnClickListener(view -> {

            QuranBookmarkRegistry quranBookmarkRegistry = QuranBookmarkRegistry.getInstance(context);

            if (bookmarkByPageNumber != null) {
                quranBookmarkRegistry.deleteBookmark(quranPage.getPageNum());
                bookmarkImageView.setImageResource(R.drawable.ic_bookmark_empty);
            } else {
                quranBookmarkRegistry.saveBookmark(quranPage, BookmarkType.USER_MADE);
                bookmarkImageView.setImageResource(R.drawable.ic_bookmark_filled);
            }
        });
    }
}
