package com.hbouzidi.fiveprayers.ui.names;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.names.AllahNames;
import com.hbouzidi.fiveprayers.names.model.AllahName;

import java.util.List;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class NamesAdapter extends RecyclerView.Adapter<NamesAdapter.Holder> {

    private final List<AllahName> names;
    private Context context;

    public NamesAdapter() {
        names = AllahNames.getAll();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.names_item, viewGroup, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        AllahName firstName = names.get(i * 3);
        AllahName secondName = names.get(i * 3 + 1);
        AllahName thirdName = names.get(i * 3 + 2);

        holder.firstNameTextView.setText(firstName.getFontReference());
        holder.secondNameTextView.setText(secondName.getFontReference());
        holder.thirdNameTextView.setText(thirdName.getFontReference());

        holder.firstTransliterationTextView.setText(firstName.getTransliteration());
        holder.secondTransliterationTextView.setText(secondName.getTransliteration());
        holder.thirdTransliterationTextView.setText(thirdName.getTransliteration());

        int firstNameTranslationIndex = context.getResources().getIdentifier("ALLAH_NAME_" + firstName.getNumber(),
                "string", context.getPackageName());

        int secondNameTranslationIndex = context.getResources().getIdentifier("ALLAH_NAME_" + secondName.getNumber(),
                "string", context.getPackageName());

        int thirdNameTranslationIndex = context.getResources().getIdentifier("ALLAH_NAME_" + thirdName.getNumber(),
                "string", context.getPackageName());

        holder.firstTranslationTextView.setText(context.getResources().getString(firstNameTranslationIndex));
        holder.secondTranslationTextView.setText(context.getResources().getString(secondNameTranslationIndex));
        holder.thirdTranslationTextView.setText(context.getResources().getString(thirdNameTranslationIndex));
    }

    @Override
    public int getItemCount() {
        return names.size() / 3;
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView firstNameTextView;
        TextView secondNameTextView;
        TextView thirdNameTextView;

        TextView firstTransliterationTextView;
        TextView secondTransliterationTextView;
        TextView thirdTransliterationTextView;

        TextView firstTranslationTextView;
        TextView secondTranslationTextView;
        TextView thirdTranslationTextView;

        View itemView;

        public Holder(@NonNull View itemView) {
            super(itemView);

            Typeface typeface = ResourcesCompat.getFont(context, R.font.font_allah_names);

            firstNameTextView = itemView.findViewById(R.id.name_text_view_1);
            secondNameTextView = itemView.findViewById(R.id.name_text_view_2);
            thirdNameTextView = itemView.findViewById(R.id.name_text_view_3);

            firstNameTextView.setTypeface(typeface);
            secondNameTextView.setTypeface(typeface);
            thirdNameTextView.setTypeface(typeface);

            firstTransliterationTextView = itemView.findViewById(R.id.transliteration_text_view_1);
            secondTransliterationTextView = itemView.findViewById(R.id.transliteration_text_view_2);
            thirdTransliterationTextView = itemView.findViewById(R.id.transliteration_text_view_3);

            firstTranslationTextView = itemView.findViewById(R.id.translation_text_view_1);
            secondTranslationTextView = itemView.findViewById(R.id.translation_text_view_2);
            thirdTranslationTextView = itemView.findViewById(R.id.translation_text_view_3);

            this.itemView = itemView;
        }
    }
}