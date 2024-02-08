package com.android.offeringhands.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.offeringhands.R;
import com.android.offeringhands.UserData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final List<Chats> lists = Collections.synchronizedList(new ArrayList<>());
    ImageView broadcastImage;
    TextView broadcastName;
    EditText textInputEditText;
    ImageView sendButton;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference messagesRef = database.getReference("Messages");
    String broadcastId;
    private RecyclerView.Adapter adapter;
    private Handler handler;
    private Runnable methodToCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        RecyclerView recyclerView = findViewById(R.id.broadcastChats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        broadcastImage = findViewById(R.id.profileImage);
        broadcastName = findViewById(R.id.textViewBroadcastName);
        textInputEditText = findViewById(R.id.msgText);
        sendButton = findViewById(R.id.sendButton);

        adapter = new ChatsAdapter(lists, this);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("BROADCAST_ID")) {
            broadcastId = intent.getStringExtra("BROADCAST_ID");
            displayMessages();

        }
        displayBroadcastDetails();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = textInputEditText.getText().toString();
                textInputEditText.setText("");
                if (!msg.isEmpty()) {
                    Chats chats = new Chats("Test", msg, new UserData(getApplicationContext()).getUserId());

                    Date currentDate = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss/SSS", Locale.getDefault());
                    String formattedDateTime = sdf.format(currentDate);

                    Map<String, String> map = new HashMap<>();
                    map.put("message", msg);
                    map.put("id", new UserData(getApplicationContext()).getUserId());
                    map.put("time", formattedDateTime);

                    messagesRef.child(broadcastId).child("chats").push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Append sent message to local list and notify adapter
                            // lists.add(chats);
                            // adapter.notifyDataSetChanged();

                            // Scroll to the bottom to show the latest message
                            recyclerView.scrollToPosition(lists.size() - 1);

                            Toast.makeText(ChatActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Listen for new messages
        DatabaseReference newMessagesRef = FirebaseDatabase.getInstance().getReference("Messages").child(broadcastId).child("chats");
        newMessagesRef.orderByChild("time").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Fetch newly added message and append to the local list
                String messageId = dataSnapshot.child("id").getValue(String.class);
                String message = dataSnapshot.child("message").getValue(String.class);
                String time = dataSnapshot.child("time").getValue(String.class);
                Chats chats = new Chats(time, message, messageId);
                lists.add(chats);

                // Notify adapter about the data change
                adapter.notifyDataSetChanged();

                // Scroll to the bottom to show the latest message
                recyclerView.scrollToPosition(lists.size() - 1);
                // displayMessages();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle changed data if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle removed data if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle moved data if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("Error listening for new messages: " + databaseError.getMessage());
            }
        });





/*

        handler = new Handler(Looper.getMainLooper());

        // Define the method to be called
        methodToCall = new Runnable() {
            @Override
            public void run() {
           displayMessages();
            }
        };

        // Schedule the method to be called every 5 seconds
        handler.postDelayed(methodToCall, 5000);*/
    }


    void displayMessages() {
        // Fetch and display messages
        lists.clear();
        /// adapter.notifyDataSetChanged();


        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Messages").child(broadcastId).child("chats");
        messagesRef.orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lists.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String messageId = childSnapshot.child("id").getValue(String.class);
                    String message = childSnapshot.child("message").getValue(String.class);
                    String time = childSnapshot.child("time").getValue(String.class);
                    Chats chats = new Chats(time, message, messageId);
                    lists.add(chats);
                }

                // Notify adapter outside the loop after adding all messages
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("Error fetching chats: " + databaseError.getMessage());
            }
        });
    }


    void displayBroadcastDetails() {
        DatabaseReference broadcastRef = FirebaseDatabase.getInstance().getReference("Broadcast").child(broadcastId);
        broadcastRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String broadcastNameString = snapshot.child("name").getValue(String.class);
                    String imagePath = snapshot.child("image_path").getValue(String.class);
                    //   Toast.makeText(ChatActivity.this, broadcastNameString + " " + imagePath, Toast.LENGTH_SHORT).show();
                    broadcastName.setText(broadcastNameString);
                    Picasso.get().load(imagePath).into(broadcastImage);
                } else {
                    Toast.makeText(ChatActivity.this, "Broadcast not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error fetching broadcast details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}