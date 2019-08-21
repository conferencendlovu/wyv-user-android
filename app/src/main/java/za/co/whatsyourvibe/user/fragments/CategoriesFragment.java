package za.co.whatsyourvibe.user.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import za.co.whatsyourvibe.user.R;
import za.co.whatsyourvibe.user.activities.EventsByCategoryActivity;


public class CategoriesFragment extends Fragment {


    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        initViews();
    }

    private void initViews() {

        CardView concerts = getActivity().findViewById(R.id.categories_fragment_concerts);

        concerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), EventsByCategoryActivity.class);

                intent.putExtra("CATEGORY", "Concerts");

                startActivity(intent);

            }
        });

        CardView sports = getActivity().findViewById(R.id.categories_fragment_sports);

        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), EventsByCategoryActivity.class);

                intent.putExtra("CATEGORY", "Sports");

                startActivity(intent);

            }
        });

        CardView tourism = getActivity().findViewById(R.id.categories_fragment_tourism);

        tourism.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), EventsByCategoryActivity.class);

                intent.putExtra("CATEGORY", "Tourism");

                startActivity(intent);

            }
        });

        CardView theater = getActivity().findViewById(R.id.categories_fragment_theater);

        theater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), EventsByCategoryActivity.class);

                intent.putExtra("CATEGORY", "Theater");

                startActivity(intent);

            }
        });

    }
}
