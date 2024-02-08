package com.android.offeringhands;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String USERS_ROOT = "Users";
    private static final String USER_ID_KEY = "userid";
    private static final String NAME_KEY = "name";
    private static final String MOBILE_NUMBER_KEY = "mobile_number";
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(USERS_ROOT);
    TextView txtSignUp;
    Button loginButton;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.btnSignIn);
        txtSignUp = findViewById(R.id.txtSignUp);

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = ((EditText) findViewById(R.id.edtSignInEmail)).getText().toString().trim();
                password = ((EditText) findViewById(R.id.edtSignInPassword)).getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields should be filled", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(email, password);

                }
            }
        });

    }


    public void loginUser(final String email, final String password) {
        final String encodedEmail = email.replace('.', ',');

        databaseReference.child(encodedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String storedPassword = dataSnapshot.child(PASSWORD_KEY).getValue(String.class);
                    if (TextUtils.equals(password, storedPassword)) {
                        // Password matches, get and save the user details
                        String userId = dataSnapshot.child(USER_ID_KEY).getValue(String.class);
                        String email = dataSnapshot.child(EMAIL_KEY).getValue(String.class);
                        String name = dataSnapshot.child(NAME_KEY).getValue(String.class);
                        String mobileNumber = dataSnapshot.child(MOBILE_NUMBER_KEY).getValue(String.class);
                        saveUserDetailsToFile(userId, email, mobileNumber, name);
                        showToast("Login successful. Email: " + email);
                        // navigate to home page
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        showToast("Incorrect password. Please try again.");
                    }
                } else {
                    showToast("Email not registered. Please register first.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseHelper", "Error checking if email is registered: " + error.getMessage());
                showToast("Error checking email registration. Please try again.");
            }


        });
    }

    private void saveUserDetailsToFile(String userId, String email, String mobileNUmber, String name) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserId", userId);
        editor.putString("Email", email);
        editor.putString("MobileNumber", mobileNUmber);
        editor.putString("Name", name);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}