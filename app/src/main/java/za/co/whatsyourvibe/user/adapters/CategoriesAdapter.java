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
import za.co.whatsyourvibe.user.activities.EventsByCategoryActivity;
import za.co.whatsyourvibe.user.models.Category;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {
    private List<Category> categories;
    private Context context;
    //public static MyEvent myEvent;

    public CategoriesAdapter(List<Category> categories, Context context) {
        this.context = context;
        this.categories = categories;

        //  myEvent = new MyEvent();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category, viewGroup,
                false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {

        myViewHolder.title.setText(categories.get(position).getTitle().toUpperCase());

        Glide
                .with(context)
                .load(categories.get(position).getUrl())
                .centerCrop()
                .placeholder(R.drawable.spinner)
                .into(myViewHolder.photo);

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, EventsByCategoryActivity.class);

                String category = categories.get(position).getTitle().toUpperCase().trim();

                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                i.putExtra("CATEGORY", category);

                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView photo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_category_title);

            photo = itemView.findViewById(R.id.item_category_photo);

        }
    }
}