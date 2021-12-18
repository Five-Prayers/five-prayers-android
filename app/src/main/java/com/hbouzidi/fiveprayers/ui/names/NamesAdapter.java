package com.hbouzidi.fiveprayers.ui.names;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

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

        holder.firstNameCardView.setVisibility(View.VISIBLE);
        holder.thirdNameCardView.setVisibility(View.VISIBLE);

        int firstNameId = context.getResources().getIdentifier(firstName.getDrawableName(), "drawable", context.getPackageName());
        int secondNameId = context.getResources().getIdentifier(secondName.getDrawableName(), "drawable", context.getPackageName());
        int thirdNameId = context.getResources().getIdentifier(thirdName.getDrawableName(), "drawable", context.getPackageName());

        holder.firstNameImageView.setImageDrawable(VectorDrawableCompat.create(context.getResources(), firstNameId, null));
        holder.firstNameImageView.setContentDescription(firstName.getTransliteration());

        holder.secondNameImageView.setImageDrawable(VectorDrawableCompat.create(context.getResources(), secondNameId, null));
        holder.secondNameImageView.setContentDescription(secondName.getTransliteration());

        holder.thirdNameImageView.setImageDrawable(VectorDrawableCompat.create(context.getResources(), thirdNameId, null));
        holder.thirdNameImageView.setContentDescription(thirdName.getTransliteration());

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

        if (firstName.getNumber() == 0 && thirdName.getNumber() == 0) {
            holder.firstNameCardView.setVisibility(View.INVISIBLE);
            holder.thirdNameCardView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return names.size() / 3;
    }

    static class Holder extends RecyclerView.ViewHolder {

        ImageView firstNameImageView;
        ImageView secondNameImageView;
        ImageView thirdNameImageView;

        TextView firstTransliterationTextView;
        TextView secondTransliterationTextView;
        TextView thirdTransliterationTextView;

        TextView firstTranslationTextView;
        TextView secondTranslationTextView;
        TextView thirdTranslationTextView;

        CardView firstNameCardView;
        CardView secondNameCardView;
        CardView thirdNameCardView;

        View itemView;

        public Holder(@NonNull View itemView) {
            super(itemView);

            firstNameImageView = itemView.findViewById(R.id.name_image_view_1);
            secondNameImageView = itemView.findViewById(R.id.name_image_view_2);
            thirdNameImageView = itemView.findViewById(R.id.name_image_view_3);

            firstTransliterationTextView = itemView.findViewById(R.id.transliteration_text_view_1);
            secondTransliterationTextView = itemView.findViewById(R.id.transliteration_text_view_2);
            thirdTransliterationTextView = itemView.findViewById(R.id.transliteration_text_view_3);

            firstTranslationTextView = itemView.findViewById(R.id.translation_text_view_1);
            secondTranslationTextView = itemView.findViewById(R.id.translation_text_view_2);
            thirdTranslationTextView = itemView.findViewById(R.id.translation_text_view_3);

            firstNameCardView = itemView.findViewById(R.id.card_view_1);
            secondNameCardView = itemView.findViewById(R.id.card_view_2);
            thirdNameCardView = itemView.findViewById(R.id.card_view_3);

            this.itemView = itemView;
        }
    }
}