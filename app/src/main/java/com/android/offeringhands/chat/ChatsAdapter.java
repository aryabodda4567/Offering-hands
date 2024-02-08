package com.android.offeringhands.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.offeringhands.R;
import com.android.offeringhands.UserData;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    public ViewHolder viewHolder;
    Context context;
    private final List<Chats> lists;

    public ChatsAdapter(List<Chats> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.broadcast_chats_layout, parent, false);

        return new ChatsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsAdapter.ViewHolder holder, int position) {
        Chats chats = lists.get(position);
        String userId = new UserData(context).getUserId();
        viewHolder = holder;
        holder.sentMessage.setVisibility(View.VISIBLE);
        if (chats.id().equals(userId)) {
            //current user sent


            holder.sentMessage.setText(chats.getMessage());
            holder.receivedMessage.setVisibility(View.INVISIBLE);

        } else {
            //another user sent
            holder.sentMessage.setVisibility(View.INVISIBLE);
            if (!chats.getMessage().trim().isEmpty()) {
                holder.receivedMessage.setText(chats.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView receivedMessage;
        TextView sentMessage;


        public ViewHolder(View viewHolder) {
            super(viewHolder);
            receivedMessage = itemView.findViewById(R.id.receivedTextView);
            sentMessage = itemView.findViewById(R.id.sentTextView);


        }
    }


}
