package com.wsoteam.diet.InApp.Fragments.slides;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wsoteam.diet.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TopSlideFragment extends Fragment {
    Unbinder unbinder;
    private static final String TAG = "TopSlideFragment";
    @BindView(R.id.ivMainTop)
    ImageView ivMainTop;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvText)
    TextView tvText;
    private int position;

    ArrayList<Integer> draws = new ArrayList<Integer>() {
        {
            add(R.drawable.first_photo);
            add(R.drawable.second_photo);
        }
    };

    ArrayList<String> titles = new ArrayList<String>() {
        {
            add("Как Fiasy помог вам?");
            add("Как Fiasy помог вам?");
        }
    };


    ArrayList<String> texts = new ArrayList<String>() {
        {
            add("Я пыталась похудеть разными способами, но обычно они не работали. Подсчет калорий оказался самым важным для меня. Мне нужно контролировать то, что я ем, и это действительно работает.");
            add("Мало того, что подсчет калорий помог мне следить за своей едой, это также помогло мне выявить продукты, которые кажутся «безвредными» и ограничить их потребление.");
        }
    };


    public static TopSlideFragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(TAG, position);
        TopSlideFragment slideFragment = new TopSlideFragment();
        slideFragment.setArguments(bundle);
        return slideFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slide_top, container, false);
        unbinder = ButterKnife.bind(this, view);
        position = getArguments().getInt(TAG);
        Glide.with(getActivity()).load(draws.get(position)).into(ivMainTop);
        tvTitle.setText(titles.get(position));
        tvText.setText(texts.get(position));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
