package com.android.offeringhands;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.offeringhands.broadcast.BroadcastAdaptar;
import com.android.offeringhands.broadcast.BroadcastHelper;
import com.android.offeringhands.broadcast.CreateBroadcastActivity;
import com.android.offeringhands.broadcast.DisplayList;
import com.android.offeringhands.broadcast.OnDataReceivedListener;
import com.android.offeringhands.chat.GroupsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private List<DisplayList> lists;
    private RecyclerView.Adapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!new UserData(getApplicationContext()).isLoggedIn()) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        progressBar = findViewById(R.id.progressBarViewRecyclerView);
        progressBar.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = findViewById(R.id.recycleviewBroadcast);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        ImageView chatsView = findViewById(R.id.chats);
        ImageView orphanages = findViewById(R.id.orphanages);
        ImageView oldAgeHomes = findViewById(R.id.oldAgeHomes);
        ImageView createBroadcastOpt = findViewById(R.id.createBroadcastOpt);
        ImageView home = findViewById(R.id.home);
        ImageView donate = findViewById(R.id.webView);


        lists = new ArrayList<>();
        adapter = new BroadcastAdaptar(lists, this);
        recyclerView.setAdapter(adapter);

        BroadcastHelper broadCast = new BroadcastHelper(getApplicationContext());
        broadCast.getBroadCast(new OnDataReceivedListener() {
            @Override
            public void onDataReceived(ArrayList<Map<String, String>> arrayList) {
                progressBar.setVisibility(View.GONE);
                for (Map<String, String> m : arrayList) {
                    DisplayList displayList = new DisplayList(
                            m.get("name"),
                            m.get("description"),
                            m.get("broadcastId"),
                            m.get("subject"),
                            m.get("filepath")
                    );
                    lists.add(displayList);
                }

                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();
            }
        });

        // startActivity(new Intent(HomeActivity.this,MapActivity.class));

        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, WebViewActivity.class));

            }
        });

        chatsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, GroupsActivity.class));
                finish();
            }
        });

        oldAgeHomes.setOnClickListener(new View.OnClickListener() {
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


        createBroadcastOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CreateBroadcastActivity.class));
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
