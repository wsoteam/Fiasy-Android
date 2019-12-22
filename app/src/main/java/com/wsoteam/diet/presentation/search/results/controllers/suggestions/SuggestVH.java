package com.wsoteam.diet.presentation.search.results.controllers.suggestions;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.wsoteam.diet.R;
import com.wsoteam.diet.presentation.search.IClick;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuggestVH extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    private IClick click;

    public SuggestVH(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup) {
        super(layoutInflater.inflate(R.layout.view_suggest_item, viewGroup, false));
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    public void bind(String title, String query, IClick click) {
        this.click = click;
        try {
            tvTitle.setText(getPaintedString(title, query));
        }catch (Exception ex){
            Log.e("LOL", "unknown");
        }
    }

    private Spannable getPaintedString(String title, String query) {
        String[] queryWords = query.split("\\s");
        Spannable spannable = new SpannableString(title);
        for (String word : queryWords) {
            if (title.toLowerCase().contains(word.toLowerCase())) {
                Matcher matcher = Pattern.compile("(?=(" + word.toLowerCase() + "))").matcher(title.toLowerCase());
                while (matcher.find()) {
                    spannable.setSpan(new ForegroundColorSpan(itemView.getResources().getColor(R.color.srch_suggest_painted_string)), matcher.start(), word.length() + matcher.start(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return spannable;
    }

    @Override
    public void onClick(View view) {
        click.click(getAdapterPosition());
    }
}
