package com.android.offeringhands.broadcast;


import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.offeringhands.UserData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BroadcastHelper {

    private static final String BROADCAST_ROOT = "Broadcast";
    private static final String NAME_KEY = "name";
    private static final String SUBJECT_KEY = "subject";
    private static final String BROADCAST_ID_KEY;
    private static final String MOBILE_NUMBER_KEY = "mobile_number";
    private static final String ADDRESS_KEY = "address";
    private static final String CREATOR_ID = "creator_id";
    private static final String CREATOR_NAME = "creator_name";
    private static final String CREATOR_MOBILE_NUMBER = "creator_mobile_number";
    private static final String CREATOR_EMAIL = "creator_email";
    private static final String IMAGE_PATH = "image_path";
    private static final String DESCRIPTION_KEY;

    static {
        BROADCAST_ID_KEY = "broadcastId";
        DESCRIPTION_KEY = "description";
    }

    private final DatabaseReference databaseReference;
    private final Context context;
    private String subject;
    private String name;
    private String contactNumber;
    private String description;
    private String eventLocation;
    private String broadcastId;


    public BroadcastHelper(Context context) {
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference().child(BROADCAST_ROOT);
    }

    public BroadcastHelper(Context context, String subject, String name, String contactNumber, String description, String eventLocation) {
        this.subject = subject;
        this.name = name;
        this.contactNumber = contactNumber;
        this.description = description;
        this.eventLocation = eventLocation;
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference().child(BROADCAST_ROOT);
    }

    public BroadcastHelper(Context context, String broadcastId) {
        this.context = context;
        this.broadcastId = broadcastId;
        databaseReference = FirebaseDatabase.getInstance().getReference().child(BROADCAST_ROOT);
    }

    public void createBroadcast(String imagePath, ProgressBar progressBar) {
        broadcastId = databaseReference.push().getKey();
        UserData userData = new UserData(context);
        Map<String, String> formData = new HashMap<>();
        formData.put(NAME_KEY, name);
        formData.put(MOBILE_NUMBER_KEY, contactNumber);
        formData.put(DESCRIPTION_KEY, description);
        formData.put(ADDRESS_KEY, eventLocation);
        formData.put(BROADCAST_ID_KEY, broadcastId);
        formData.put(SUBJECT_KEY, subject);
        formData.put(CREATOR_ID, userData.getUserId());
        formData.put(CREATOR_NAME, userData.getName());
        formData.put(CREATOR_MOBILE_NUMBER, userData.getMobileNumber());
        formData.put(CREATOR_EMAIL, userData.getEmailKey());
        formData.put(IMAGE_PATH, imagePath);


        databaseReference.child(broadcastId).setValue(formData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.GONE);
                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("image_uri", imagePath);
                map.put("broadcast_id", broadcastId);
                DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
                messagesRef.child(broadcastId).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Created", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Something went wrong, please try after sometime", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    public void getBroadCast(OnDataReceivedListener listener) {
        DatabaseReference broadcastRef = FirebaseDatabase.getInstance().getReference("Broadcast");

        // Add a listener to retrieve data under "Broadcast"
        broadcastRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Map<String, String>> arrayList = new ArrayList<>();

                // Iterate through all children (IDs) under "Broadcast"
                for (DataSnapshot idSnapshot : dataSnapshot.getChildren()) {
                    String broadcastId = idSnapshot.child("broadcastId").getValue(String.class);
                    String description = idSnapshot.child("description").getValue(String.class);
                    String name = idSnapshot.child("name").getValue(String.class);
                    String subject = idSnapshot.child("subject").getValue(String.class);
                    String filePath = idSnapshot.child("image_path").getValue(String.class);


                    // Create a new map for each iteration
                    Map<String, String> map = new HashMap<>();
                    map.put("broadcastId", broadcastId);
                    map.put("description", description);
                    map.put("name", name);
                    map.put("subject", subject);
                    map.put("filepath", filePath);
                    System.out.println(name);

                    arrayList.add(map);
                }

                // Invoke the listener with the result
                listener.onDataReceived(arrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("Error: " + databaseError.getMessage());
            }
        });
    }


    public void joinBroadcast() {
        String userId = new UserData(context).getUserId();

// Check if the user is already a member of the broadcast
        databaseReference.child(broadcastId).child("members")
                .child(userId)
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // User is already a member, show a message or handle accordingly
                            Toast.makeText(context, "Already a member", Toast.LENGTH_SHORT).show();
                        } else {
                            // User is not a member, add them
                            databaseReference.child(broadcastId).child("members")
                                    .child(userId)
                                    .setValue(true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            updateUserOnBroadcastJoin();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Something went wrong, please try after some time", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error if needed
                        Toast.makeText(context, "Error checking broadcast members", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void getMembers(final MembersCallback callback) {
        DatabaseReference membersRef = databaseReference.child(broadcastId).child("members");
        ArrayList<String> userIds = new ArrayList<>();
        membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                    String userId = memberSnapshot.getValue(String.class);
                    userIds.add(userId);
                }
                // Call the callback method with the result
                callback.onMembersLoaded(userIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Call the callback method with the error message
                callback.onMembersError("Error: " + databaseError.getMessage());
            }
        });
    }


    void updateUserOnBroadcastJoin() {
        DatabaseReference broadcastRef = FirebaseDatabase.getInstance().getReference("Users");
        String emailKey = new UserData(context).getEmailKey().replace('.', ',');

// Check if the broadcast ID already exists in the user's joined_broadcasts
        broadcastRef.child(emailKey).child("joined_broadcasts")
                .child(broadcastId)
                .equalTo(broadcastId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Broadcast ID already exists, show a message or handle accordingly
                            Toast.makeText(context, "Already joined", Toast.LENGTH_SHORT).show();
                        } else {
                            // Broadcast ID does not exist, add it
                            broadcastRef.child(emailKey).child("joined_broadcasts")
                                    .child(broadcastId)
                                    .setValue(true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Joined", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Something went wrong, please try after some time", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error if needed
                        Toast.makeText(context, "Error checking joined broadcasts", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
