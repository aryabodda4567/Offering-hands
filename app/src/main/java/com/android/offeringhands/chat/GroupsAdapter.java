package com.android.offeringhands.chat;


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

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
    Context context;
    private final List<Groups> lists;

    public GroupsAdapter(List<Groups> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return the input unchanged if it's null or empty
        }
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    @NonNull
    @Override
    public GroupsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_list, parent, false);

        return new GroupsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsAdapter.ViewHolder holder, int position) {
        Groups groups = lists.get(position);
        holder.name.setText((capitalizeFirstLetter(groups.getName())));
        //  holder.imageView.setImageURI();
        Picasso.get().load(groups.getImageUrl()).into(holder.imageView);


        holder.itemView.setOnClickListener(view -> {

            Intent intent = new Intent(context, ChatActivity.class); // Change to your actual DetailActivity
            intent.putExtra("BROADCAST_ID", groups.getBroadcastId());
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
        public ImageView imageView;

        public ViewHolder(View viewHolder) {
            super(viewHolder);
            name = itemView.findViewById(R.id.tv_user_name);
            imageView = itemView.findViewById(R.id.iv_user_photo);
        }
    }
}
