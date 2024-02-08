package com.android.offeringhands.broadcast;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.offeringhands.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BroadcastAdaptar extends RecyclerView.Adapter<BroadcastAdaptar.ViewHolder> {
    Context context;
    private final List<DisplayList> lists;

    public BroadcastAdaptar(List<DisplayList> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return the input unchanged if it's null or empty
        }

        // Convert the first character to uppercase and concatenate the rest of the string
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.broadcaste_entry, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisplayList displayList = lists.get(position);
        holder.name.setText((capitalizeFirstLetter(displayList.getName())));
        holder.description.setText(capitalizeFirstLetter(displayList.getDescription()));
        holder.subject.setText(capitalizeFirstLetter(displayList.getBroadcastSubject()));
        Picasso.get().load(displayList.getImagePath()).into(holder.imageView);


        holder.itemView.setOnClickListener(view -> {
            // Start the new activity and pass the clicked card's broadcast ID
            Intent intent = new Intent(context, ViewBroadcastActivity.class); // Change to your actual DetailActivity
            intent.putExtra("BROADCAST_ID", displayList.getBroadcastId());
            context.startActivity(intent);

            // Add click animation
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.click_animation); // Create a click animation resource
            holder.itemView.startAnimation(animation);
        });


    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;
        public TextView subject;
        public ImageView imageView;

        public ViewHolder(View viewHolder) {
            super(viewHolder);
            name = itemView.findViewById(R.id.broadcastname);
            description = itemView.findViewById(R.id.broadcasdescription);
            subject = itemView.findViewById(R.id.broadcastDisplaySubject);
            imageView = itemView.findViewById(R.id.eventImageImageView);
        }
    }
}