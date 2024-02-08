package com.android.offeringhands.broadcast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.offeringhands.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewBroadcastActivity extends AppCompatActivity {

    String broadcastId = "";
    TextView textViewAddress;
    TextView textViewDescription;
    TextView textViewMobileNumber;
    TextView textViewName;
    TextView textViewSubject;
    TextView textViewCreatorName;
    TextView textViewCreatorEmail;
    TextView textViewCreatorMobileNumber;
    Button buttonJoin;
    ImageView imageView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_broadcast);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference broadcastsRef = databaseReference.child("Broadcast");
        textViewAddress = findViewById(R.id.textViewAddress);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewMobileNumber = findViewById(R.id.textViewMobileNumber);
        textViewName = findViewById(R.id.textViewName);
        textViewSubject = findViewById(R.id.textViewSubject);
        textViewCreatorEmail = findViewById(R.id.textViewCreatorEmail);
        textViewCreatorMobileNumber = findViewById(R.id.textViewCreatorMobileNumber);
        textViewCreatorName = findViewById(R.id.textViewCreatorName);
        imageView = findViewById(R.id.eventImage);
        buttonJoin = findViewById(R.id.buttonJoin);
        progressBar = findViewById(R.id.progressBarViewBroadcast);
        progressBar.setVisibility(View.VISIBLE);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("BROADCAST_ID")) {
            broadcastId = intent.getStringExtra("BROADCAST_ID");
            DatabaseReference specificBroadcastRef = broadcastsRef.child(broadcastId);


            specificBroadcastRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Check if the data exists
                    if (dataSnapshot.exists()) {
                        // Retrieve the details from the dataSnapshot
                        progressBar.setVisibility(View.GONE);
                        String address = dataSnapshot.child("address").getValue(String.class);
                        String description = dataSnapshot.child("description").getValue(String.class);
                        String mobileNumber = dataSnapshot.child("mobile_number").getValue(String.class);
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String subject = dataSnapshot.child("subject").getValue(String.class);
                        String creatorName = dataSnapshot.child("creator_name").getValue(String.class);
                        String creatorEmail = dataSnapshot.child("creator_email").getValue(String.class);
                        String creatorMobileNumber = dataSnapshot.child("creator_mobile_number").getValue(String.class);
                        String imagePath = dataSnapshot.child("image_path").getValue(String.class);

                        textViewAddress.setText(address);
                        textViewDescription.setText(description);
                        textViewMobileNumber.setText(mobileNumber);
                        textViewName.setText(name);
                        textViewSubject.setText(subject);
                        textViewCreatorMobileNumber.setText(creatorMobileNumber);
                        textViewCreatorName.setText(creatorName);
                        textViewCreatorEmail.setText(creatorEmail);
                        Picasso.get().load(imagePath).into(imageView);


                    } else {
                        // Handle the case where the broadcast ID does not exist
                        System.out.println("Broadcast ID not found.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                    System.out.println("Error: " + databaseError.getMessage());
                }
            });


        } else {
            makeToast();
        }

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BroadcastHelper broadcastHelper = new BroadcastHelper(getApplicationContext(), broadcastId);
                broadcastHelper.joinBroadcast();
            }
        });


    }


    void makeToast() {
        makeToast("Something went wrong. Please try later.");
    }


    void makeToast(String msg) {
        Toast.makeText(ViewBroadcastActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}