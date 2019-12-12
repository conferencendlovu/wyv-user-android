package za.co.whatsyourvibe.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import za.co.whatsyourvibe.user.R;
import za.co.whatsyourvibe.user.activities.EventDetailsActivity;
import za.co.whatsyourvibe.user.models.Event;

public class EventsByTrending extends RecyclerView.Adapter<EventsByTrending.MyViewHolder> {

    private List<Event> eventList;

    private Context context;

    public EventsByTrending(List<Event> eventList, Context context) {

        this.context = context;

        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventsByTrending.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_event_trending,
                viewGroup,
                false);

        return new EventsByTrending.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsByTrending.MyViewHolder myViewHolder, final int position) {

        Glide.with(context)
                .load(eventList.get(position).getCoverPhotoUrl())
                // .placeholder(placeholder)
                // .fitCenter()
                .into(myViewHolder.photo);

        Glide.with(context)
                .load(eventList.get(position).getCoverPhotoUrl())
                // .placeholder(placeholder)
                // .fitCenter()
                .into(myViewHolder.poster);

        myViewHolder.title.setText(eventList.get(position).getTitle());

        myViewHolder.going.setText(eventList.get(position).getGoing()+"");

//        myViewHolder.ratingBar.setRating((float)eventList.get(position).getRate());

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

        TextView title, going;

        ImageView photo, poster;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            title = itemView.findViewById(R.id.item_event_trending_title);

            going = itemView.findViewById(R.id.item_event_trending_going);

            photo = itemView.findViewById(R.id.item_event_trending_photo);

            poster = itemView.findViewById(R.id.item_event_trending_poster);

        }
    }
}
