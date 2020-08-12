package com.bouzidi.prayertimes.ui.quran.pages;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.quran.dto.Ayah;
import com.bouzidi.prayertimes.quran.dto.Page;
import com.bouzidi.prayertimes.quran.dto.Surah;
import com.bouzidi.prayertimes.utils.CustomTypefaceSpan;
import com.bouzidi.prayertimes.utils.FontBySurahNumber;
import com.bouzidi.prayertimes.utils.SurahFontReference;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.Holder> {

    private List<Page> quranPage = new ArrayList<>();
    private PageShown pageShown;
    private List<Surah> surahs;
    private Context context;

    private int textColor;
    private int backgroundColor;

    public PageAdapter(int textColor, int backgroundColor) {
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public void setPageShown(PageShown pageShown) {
        this.pageShown = pageShown;
    }

    public void setQuranPages(List<Page> newList) {
        quranPage = new ArrayList<>(newList);
    }

    @NotNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.page_item, viewGroup, false);
        context = viewGroup.getContext();
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int index) {
        Page currentPage = quranPage.get(index);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String ayahText;
        String surahName = getSuraNameFromIndex(currentPage.getAyahs().get(0).getSurahNumber());

        boolean isFirst = true;
        boolean displayQuarterInfo = false;
        Ayah previousAyah = (index > 0) ? quranPage.get(index - 1).getAyahs().get(quranPage.get(index - 1).getAyahs().size() - 1) : null;
        int hizbQuarter = 1;

        for (Ayah currentAyah : currentPage.getAyahs()) {
            ayahText = currentAyah.getText();

            if (currentAyah.getNumberInSurah() == 1) {
                if (!isFirst) {
                    spannableStringBuilder.append("\n\n");
                }
                appendSurahName(spannableStringBuilder, currentAyah);
                spannableStringBuilder.append("\n");

                if (currentAyah.getSurahNumber() != 1 && currentAyah.getSurahNumber() != 9) {
                    int pos = ayahText.indexOf("ٱلرَّحِيم");
                    pos += ("ٱلرَّحِيم".length());

                    appendBasmala(spannableStringBuilder);
                    spannableStringBuilder.append("\n");

                    ayahText = ayahText.substring(pos + 1);
                }
            }

            if (previousAyah != null && (previousAyah.getHizbQuarter() != currentAyah.getHizbQuarter())) {
                hizbQuarter = currentAyah.getHizbQuarter();
                displayQuarterInfo = true;
            }

            previousAyah = currentAyah;

            isFirst = false;
            spannableStringBuilder.append(MessageFormat.format("{0}   \uFD3F{1}\uFD3E  ", ayahText, getArabicNumber(currentAyah.getNumberInSurah())));
        }

        holder.topLinearLayout.setVisibility(View.VISIBLE);
        holder.ayahsTextView.setTextColor(textColor);
        holder.ayahsConstraintLayout.setBackgroundColor(backgroundColor);

        holder.ayahsTextView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
        holder.pageNumTextView.setText(String.valueOf(currentPage.getPageNum()));
        holder.surahNameTextView.setText(surahName);
        holder.juzTextView.setText(getHizbInfoBuilder(currentPage, displayQuarterInfo, hizbQuarter));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            holder.ayahsTextView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            holder.ayahsConstraintLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        holder.ayahsTextView.setMovementMethod(new ScrollingMovementMethod());
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

        TextView ayahsTextView;
        TextView surahNameTextView;
        TextView juzTextView;
        LinearLayout topLinearLayout;
        TextView pageNumTextView;
        ConstraintLayout ayahsConstraintLayout;

        public Holder(@NonNull View itemView) {
            super(itemView);

            ayahsTextView = itemView.findViewById(R.id.ayahs_text_view);
            surahNameTextView = itemView.findViewById(R.id.surah_name_text_view);
            juzTextView = itemView.findViewById(R.id.juz_text_view);
            topLinearLayout = itemView.findViewById(R.id.top_linear_layout);
            ayahsConstraintLayout = itemView.findViewById(R.id.ayah_layout);
            pageNumTextView = itemView.findViewById(R.id.page_num_text_view);
        }
    }

    @NotNull
    private StringBuilder getHizbInfoBuilder(Page item, boolean displayQuarterInfo, int hizbQuarter) {
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

    private void appendBasmala(SpannableStringBuilder builder) {
        int bstart = builder.length();
        Typeface basmalaTypeface = ResourcesCompat.getFont(context, R.font.aga_islamic_phrases);
        builder.append("\u0035");
        builder.setSpan(new CustomTypefaceSpan(basmalaTypeface), bstart, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new RelativeSizeSpan(2.5f), bstart, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void appendSurahName(SpannableStringBuilder builder, Ayah ayah) {
        SurahFontReference surahFontReference = FontBySurahNumber.getSurahFontReference(ayah.getSurahNumber());
        Typeface surahTypeface = ResourcesCompat.getFont(context, surahFontReference.getFontResourceId());
        Typeface kitabTypeface = ResourcesCompat.getFont(context, R.font.kitab);
        Typeface ornamentTypeface = ResourcesCompat.getFont(context, R.font.arabesque_ornaments);

        int ornamentStart = builder.length();
        builder.append("$");
        builder.setSpan(new CustomTypefaceSpan(ornamentTypeface), ornamentStart, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new RelativeSizeSpan(2.5f), ornamentStart, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (ayah.getSurahNumber() != 101) {//Exclude Al Quariaa because of Typeface spacing bug
            int start = builder.length();
            builder.append(surahFontReference.getRegularText());
            builder.setSpan(new CustomTypefaceSpan(surahTypeface), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new RelativeSizeSpan(2f), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            String suraNameFromIndex = getSuraNameFromIndex(ayah.getSurahNumber());
            int pos = suraNameFromIndex.indexOf("سورة");
            pos += ("سورة".length());
            int start = builder.length();
            builder.append(suraNameFromIndex.substring(pos + 1));
            builder.setSpan(new CustomTypefaceSpan(kitabTypeface), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new RelativeSizeSpan(2f), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        int ornamentEnd = builder.length();
        builder.append("$");
        builder.setSpan(new CustomTypefaceSpan(ornamentTypeface), ornamentEnd, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new RelativeSizeSpan(2.5f), ornamentEnd, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private String getSuraNameFromIndex(int surahIndex) {
        return surahs.get(surahIndex - 1).getName();
    }

    private String getArabicNumber(int num) {
        Locale locale = new Locale("ar");
        return String.format(locale, "%d", num);
    }
}
