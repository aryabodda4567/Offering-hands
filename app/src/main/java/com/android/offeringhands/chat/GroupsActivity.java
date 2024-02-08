package com.android.offeringhands.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.offeringhands.HomeActivity;
import com.android.offeringhands.R;
import com.android.offeringhands.UserData;
import com.android.offeringhands.WebViewActivity;
import com.android.offeringhands.broadcast.CreateBroadcastActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {
    private List<Groups> lists;
    private RecyclerView.Adapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewGroups);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        lists = new ArrayList<>();
        adapter = new GroupsAdapter(lists, this);
        recyclerView.setAdapter(adapter);
        progressBar = findViewById(R.id.progressBarGroupView);
        progressBar.setVisibility(View.VISIBLE);

        ImageView home = findViewById(R.id.group_home);
        ImageView createBroadcast = findViewById(R.id.group_createBroadcastOpt);
        ImageView orphanages = findViewById(R.id.group_orphanages);
        ImageView oldageHomes = findViewById(R.id.group_oldAgeHomes);
        ImageView donate = findViewById(R.id.donation);


        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupsActivity.this, WebViewActivity.class));
                finish();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupsActivity.this, HomeActivity.class));
                finish();
            }
        });

        createBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupsActivity.this, CreateBroadcastActivity.class));
                finish();
            }
        });

        oldageHomes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps("Show+near+by+Old-age+Homes");
            }
        });

        orphanages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps("Show+nearby+Orphanages");
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users").child(new UserData(getApplicationContext()).getEmailKey().replace('.', ','));


        // Fetch the joined_broadcasts of the user
        userRef.child("joined_broadcasts").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot joinedBroadcastsSnapshot) {
                // Check if joined_broadcasts data exists
                if (joinedBroadcastsSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot joinedBroadcastSnapshot : joinedBroadcastsSnapshot.getChildren()) {
                        String broadcastId = joinedBroadcastSnapshot.getKey();


                        // Once broadcastId is obtained, query the Broadcasts node to get details
                        DatabaseReference broadcastsRef = database.getReference("Broadcast").child(broadcastId);
                        broadcastsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot broadcastSnapshot) {

                                String name = broadcastSnapshot.child("name").getValue(String.class);
                                String broadcastId = broadcastSnapshot.child("broadcastId").getValue(String.class);
                                String imageUri = broadcastSnapshot.child("image_path").getValue(String.class);

                                Groups groups = new Groups(name, broadcastId, imageUri);
                                lists.add(groups);
                                adapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle database error

                            }
                        });

                    }
                } else {
                    // Callback with null or appropriate response if joined_broadcasts is empty
                    Toast.makeText(GroupsActivity.this, "You have not joined any broadcast yet", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(GroupsActivity.this, "Something Went wrong Please Try After Some Time", Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void openGoogleMaps(String query) {
        // Create a URI for the Google Maps app with the query
        String url = "https://www.google.com/maps/search/?api=1&query=" + query;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);

    }


}