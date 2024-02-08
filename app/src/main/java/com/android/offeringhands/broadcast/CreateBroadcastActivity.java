package com.android.offeringhands.broadcast;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.offeringhands.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

public class CreateBroadcastActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 22;
    BroadcastHelper broadCast;
    ProgressBar progressBar;
    private TextInputEditText brodcastSubjectEditText;
    private TextInputEditText brodcastNameEditText;
    private TextInputEditText hostContactNumberEditText;
    private TextInputEditText brodcastDescriptionEditText;
    private TextInputEditText brodcastEventLocationEditText;
    private Button btnCreate;
    private Button uploadImage;
    private ImageView imageViewUploadImage;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_broadcast_actvity);

        brodcastSubjectEditText = findViewById(R.id.brodcastSubject);
        brodcastNameEditText = findViewById(R.id.brodcastName);
        hostContactNumberEditText = findViewById(R.id.hostContactNumber);
        brodcastDescriptionEditText = findViewById(R.id.brodcastDescription);
        brodcastEventLocationEditText = findViewById(R.id.brodcastEventLocation);
        btnCreate = findViewById(R.id.btnCreateBroadcast);
        uploadImage = findViewById(R.id.uploadImage);
        imageViewUploadImage = findViewById(R.id.imageViewUploadImage);
        progressBar = findViewById(R.id.createBroadcastProgressBar);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String subject = Objects.requireNonNull(brodcastSubjectEditText.getText()).toString();
                    String name = Objects.requireNonNull(brodcastNameEditText.getText()).toString();
                    String contactNumber = Objects.requireNonNull(hostContactNumberEditText.getText()).toString();
                    String description = Objects.requireNonNull(brodcastDescriptionEditText.getText()).toString();
                    String eventLocation = Objects.requireNonNull(brodcastEventLocationEditText.getText()).toString();
                    if (validateInputs()) {
                        broadCast = new BroadcastHelper(getApplicationContext(), subject, name, contactNumber, description, eventLocation);
                        if (filePath != null) {
                            progressBar.setVisibility(View.VISIBLE);
                            uploadImage();
                        } else {
                            Toast.makeText(CreateBroadcastActivity.this, "Select event image", Toast.LENGTH_SHORT).show();
                        }


                    }
                } catch (Exception e) {

                }
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();

            if (filePath != null) {
                imageViewUploadImage.setImageURI(filePath);
            }
        }
    }

    private void selectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);

    }

    private boolean validateInputs() {
        if (isEmpty(brodcastSubjectEditText) ||
                isEmpty(brodcastNameEditText) ||
                isEmpty(hostContactNumberEditText) ||
                isEmpty(brodcastDescriptionEditText) ||
                isEmpty(brodcastEventLocationEditText)) {

            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private boolean isEmpty(TextInputEditText editText) {
        return Objects.requireNonNull(editText.getText()).toString().trim().isEmpty();
    }

    void uploadImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String storagePath = "images/";
        String imageName = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child(storagePath + imageName);
        UploadTask uploadTask = imageRef.putFile(filePath);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Image upload successful, get download URL
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    String downloadUrl = uri.toString();
                    broadCast.createBroadcast(downloadUrl, progressBar);
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Something went wrong please try after sometime", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                });
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong please try after sometime", Toast.LENGTH_LONG).show();
                task.getException().printStackTrace();
            }
        });

    }


}