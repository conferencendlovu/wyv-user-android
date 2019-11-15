package za.co.whatsyourvibe.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import za.co.whatsyourvibe.user.R;
import za.co.whatsyourvibe.user.activities.EventDetailsActivity;
import za.co.whatsyourvibe.user.models.Event;

public class EventsByCategoryAdapter extends RecyclerView.Adapter<EventsByCategoryAdapter.MyViewHolder> {

    private List<Event> eventList;

    private Context context;

    public EventsByCategoryAdapter(List<Event> eventList, Context context) {

        this.context = context;

        this.eventList = eventList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_event,
                viewGroup,
                false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {

        Glide.with(context)
                .load(eventList.get(position).getCoverPhotoUrl())
                .placeholder(R.drawable.spinner)
                .centerCrop()
                .into(myViewHolder.cover);

        myViewHolder.title.setText(eventList.get(position).getTitle());

        myViewHolder.time.setText(eventList.get(position).getTime());

        myViewHolder.date.setText(eventList.get(position).getDate());

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, EventDetailsActivity.class);

                intent.putExtra("EVENT",eventList.get(position));

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, date, time;

        ImageView cover, icon;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            time = itemView.findViewById(R.id.item_event_time);

            date = itemView.findViewById(R.id.item_event_date);

            title = itemView.findViewById(R.id.item_event_title);

            cover = itemView.findViewById(R.id.item_event_coverImage);

        }
    }
}