package za.co.whatsyourvibe.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import za.co.whatsyourvibe.user.R;
import za.co.whatsyourvibe.user.adapters.EventsByCategoryAdapter;
import za.co.whatsyourvibe.user.models.Event;

public class EventsByCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private TextView textView;

    private ProgressBar progressBar;

    private String category;

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_events_by_category);

        Toolbar toolbar = findViewById(R.id.events_category_toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        category = getIntent().getStringExtra("CATEGORY");

        if (category !=null) {

            category.trim().toUpperCase();
        }else{

            finish();
        }


        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(category);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.events_category_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setHasFixedSize(true);

        progressBar = findViewById(R.id.events_category_progressBar);

        textView = findViewById(R.id.events_category_textView);

        getCategoryEvents(category);

    }

    private void getCategoryEvents(String category) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("vibes")
                .whereEqualTo("category", category)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e !=null) {

                            textView.setText(e.getMessage());

                            textView.setVisibility(View.VISIBLE);

                            recyclerView.setVisibility(View.GONE);

                            progressBar.setVisibility(View.GONE);

                            return;
                        }

                        if ( queryDocumentSnapshots !=null && !queryDocumentSnapshots.isEmpty()) {

                            List<Event> eventList = new ArrayList<>();

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                Event event = doc.toObject(Event.class);

                                eventList.add(event);

                            }

                            EventsByCategoryAdapter eventsByCategoryAdapter = new EventsByCategoryAdapter(eventList,getApplicationContext());

                            recyclerView.setAdapter(eventsByCategoryAdapter);

                            recyclerView.setVisibility(View.VISIBLE);

                            progressBar.setVisibility(View.GONE);

                            textView.setVisibility(View.GONE);
                        }else{

                            recyclerView.setVisibility(View.GONE);

                            progressBar.setVisibility(View.GONE);

                            textView.setVisibility(View.VISIBLE);

                            textView.setText("No Events Available");

                        }

                    }
                });

    }
}
