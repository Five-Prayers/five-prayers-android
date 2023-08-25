package com.hbouzidi.fiveprayers.ui.invocations;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.os.ConfigurationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.quran.dto.Invocation;
import com.hbouzidi.fiveprayers.utils.UiUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InvocationsAdapter extends RecyclerView.Adapter<InvocationsAdapter.Holder> {

    private final String systemLanguage;
    private Context context;
    private final Map<String, List<Invocation>> invocations;

    InvocationsAdapter(Map<String, List<Invocation>> invocations) {
        this.invocations = invocations;

        systemLanguage = Objects.requireNonNull(ConfigurationCompat
                .getLocales(Resources.getSystem().getConfiguration())
                .get(0)).getLanguage();
    }

    @NonNull
    @Override
    public InvocationsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.invocation_item, viewGroup, false);

        return new InvocationsAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvocationsAdapter.Holder holder, int i) {
        List<Invocation> invocation = invocations.get(String.valueOf(i));

        Invocation invocationInArabicLanguage = Objects.requireNonNull(invocation).stream()
                .filter((dhikr -> dhikr.getLanguage().equals("ar")))
                .findFirst()
                .orElse(invocation.get(0));

        Invocation invocationInEnglishLanguage = Objects.requireNonNull(invocation).stream()
                .filter((ayah -> ayah.getLanguage().equals("en")))
                .findFirst()
                .orElse(invocation.get(1));

        Invocation invocationInSystemLanguage = Objects.requireNonNull(invocation).stream()
                .filter((ayah -> ayah.getLanguage().equals(systemLanguage)))
                .findFirst()
                .orElse(null);

        String translatedInvocation = invocationInSystemLanguage != null ? invocationInSystemLanguage.getText() : invocationInEnglishLanguage.getText();

        holder.separatorTextView.setText(UiUtils.getIslamicPhrase("XXXXXXX", context));
        holder.adhkarTextView.setText(invocationInArabicLanguage.getText());
        holder.adhkarTranslationTextView.setText(translatedInvocation);

        String pageIndicator = i + 1 + "/" + invocations.size();
        holder.invocationNumberTextView.setText(pageIndicator);
    }

    @Override
    public int getItemCount() {
        return invocations.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        final TextView adhkarTextView;
        final TextView adhkarTranslationTextView;
        final TextView separatorTextView;
        final TextView invocationNumberTextView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            adhkarTextView = itemView.findViewById(R.id.adhkar_text_view);
            adhkarTranslationTextView = itemView.findViewById(R.id.adhkar_translation_text_view);
            separatorTextView = itemView.findViewById(R.id.invocation_ornaments_separator_text_view);
            invocationNumberTextView = itemView.findViewById(R.id.invocation_number_text_view);

        }
    }
}