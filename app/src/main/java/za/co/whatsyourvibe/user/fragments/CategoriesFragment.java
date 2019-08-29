package za.co.whatsyourvibe.user.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import za.co.whatsyourvibe.user.R;
import za.co.whatsyourvibe.user.activities.EventsByCategoryActivity;
import za.co.whatsyourvibe.user.adapters.CategoriesAdapter;
import za.co.whatsyourvibe.user.models.Category;


public class CategoriesFragment extends Fragment {

    private TextView textView;
    
    private RecyclerView recyclerView;
    
    private ProgressBar progressBar;

    private CategoriesAdapter adapter;

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
        
        getCategories();
    }

    private void getCategories() {

        FirebaseFirestore categoriesRef = FirebaseFirestore.getInstance();

        categoriesRef.collection("categories")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots !=null && queryDocumentSnapshots.size() > 0) {

                            List<Category> categoryList = new ArrayList<>();

                            for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {

                                Category category = doc.toObject(Category.class);

                                categoryList.add(category);
                            }

                            adapter = new CategoriesAdapter(categoryList,getContext());

                            recyclerView.setAdapter(adapter);

                            adapter.notifyDataSetChanged();

                            progressBar.setVisibility(View.GONE);

                            textView.setVisibility(View.GONE);

                            recyclerView.setVisibility(View.VISIBLE);

                        }else {

                            progressBar.setVisibility(View.GONE);

                            textView.setVisibility(View.VISIBLE);

                            textView.setText("No Categories Available");

                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);

                        textView.setVisibility(View.VISIBLE);

                        textView.setText(e.getMessage());

                        recyclerView.setVisibility(View.GONE);
                    }
                });
    }

    private void initViews() {
        
        if (getActivity() !=null) {
            progressBar = getActivity().findViewById(R.id.fragment_categories_progressBar);

            textView = getActivity().findViewById(R.id.fragment_categories_textView);

            recyclerView = getActivity().findViewById(R.id.fragment_categories_recyclerView);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            recyclerView.setHasFixedSize(true);
        }
        
    }
}
