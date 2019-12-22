package com.wsoteam.diet.InApp.Fragments.slides;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wsoteam.diet.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SlideFragment extends Fragment {
    Unbinder unbinder;
    private static final String TAG = "SlideFragment";
    private int position;
    @BindView(R.id.mainImage)
    ImageView mainImage;
    ArrayList<Integer> draws = new ArrayList<Integer>(){
        {
            add(R.drawable.review_1);
            add(R.drawable.review_2);
            add(R.drawable.review_3);
            add(R.drawable.review_4);
        }
    };


    public static SlideFragment newInstance(int position){
        Bundle bundle = new Bundle();
        bundle.putInt(TAG, position);
        SlideFragment slideFragment = new SlideFragment();
        slideFragment.setArguments(bundle);
        return slideFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slide, container, false);
        unbinder = ButterKnife.bind(this, view);
        Glide.with(getActivity()).load(draws.get(getArguments().getInt(TAG))).into(mainImage);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
