package com.hbouzidi.fiveprayers.ui.names;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.names.AllahNames;
import com.hbouzidi.fiveprayers.names.model.AllahName;

import java.util.ArrayList;
import java.util.List;

public class NamesAdapter extends RecyclerView.Adapter<NamesAdapter.Holder> {

    private NameListner nameListner;
    private List<AllahName> names;
    private Context context;

    public NamesAdapter() {
        names = AllahNames.getAll();
    }

    public void setNameListner(NameListner nameListner) {
        this.nameListner = nameListner;
    }

    public void setNameList(List<AllahName> newList) {
        names = new ArrayList<>(newList);
    }

    public List<AllahName> getNameDtos() {
        return names;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.names_item, viewGroup, false);
        Holder holder = new Holder(view);

        return holder;
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

    interface NameListner {
        void onName(int pos);
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

            firstNameTextView = itemView.findViewById(R.id.name_text_view_1);
            secondNameTextView = itemView.findViewById(R.id.name_text_view_2);
            thirdNameTextView = itemView.findViewById(R.id.name_text_view_3);

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