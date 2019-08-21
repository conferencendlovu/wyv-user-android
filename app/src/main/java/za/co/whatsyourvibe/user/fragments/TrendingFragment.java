package za.co.whatsyourvibe.user.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import za.co.whatsyourvibe.user.R;
import za.co.whatsyourvibe.user.adapters.EventsByTrending;
import za.co.whatsyourvibe.user.models.Event;


public class TrendingFragment extends Fragment {

    private RecyclerView recyclerView;

    private TextView textView;

    private ProgressBar progressBar;


    public TrendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trending, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (getActivity() !=null) {

            textView = getActivity().findViewById(R.id.trending_textView);

            recyclerView  = getActivity().findViewById(R.id.trending_recyclerView);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            recyclerView.setHasFixedSize(true);

            progressBar  = getActivity().findViewById(R.id.trending_progressBar);

            getTrendingEvents();

        }

    }

    private void getTrendingEvents() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        if (e!=null) {

                            textView.setText(e.getMessage());

                            recyclerView.setVisibility(View.GONE);

                            progressBar.setVisibility(View.GONE);

                            textView.setVisibility(View.VISIBLE);

                            return;

                        }

                        if (queryDocumentSnapshots !=null && queryDocumentSnapshots.size() != 0) {

                            List<Event> eventList = new ArrayList<>();

                            for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {

                                Event event = doc.toObject(Event.class);

                                eventList.add(event);

                            }

                            EventsByTrending adapter = new EventsByTrending(eventList,getContext());

                            recyclerView.setAdapter(adapter);

                            adapter.notifyDataSetChanged();

                            recyclerView.setVisibility(View.VISIBLE);

                            progressBar.setVisibility(View.GONE);

                            textView.setVisibility(View.GONE);

                        }else{

                            textView.setText("No Trending Events At The Moment");

                            recyclerView.setVisibility(View.GONE);

                            progressBar.setVisibility(View.GONE);

                            textView.setVisibility(View.VISIBLE);

                        }

                    }
                });

    }
}
