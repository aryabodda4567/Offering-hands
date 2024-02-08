package com.android.offeringhands;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String USERS_ROOT = "Users";
    private static final String EMAIL_KEY = "email";
    private static final String NAME_KEY = "name";
    private static final String PASSWORD_KEY = "password";
    private static final String USER_ID_KEY = "userid";

    private static final String MOBILE_NUMBER_KEY = "mobile_number";

    TextView txtSignIn;
    Button registerButton;
    private EditText edtSignUpFullName, edtSignUpEmail, edtSignUpPassword, edtSignUpMobile, edtSignUpConfirmPassword;
    private String name, email, password, mobileNumber, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtSignUpFullName = findViewById(R.id.edtSignUpFullName);
        edtSignUpEmail = findViewById(R.id.edtSignUpEmail);
        edtSignUpPassword = findViewById(R.id.edtSignUpPassword);
        edtSignUpMobile = findViewById(R.id.edtSignUpMobile);
        edtSignUpConfirmPassword = findViewById(R.id.edtSignUpConfirmPassword);
        registerButton = findViewById(R.id.btnSignUp);
        txtSignIn = findViewById(R.id.txtSignIn);

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edtSignUpFullName.getText().toString().trim();
                email = edtSignUpEmail.getText().toString().trim();
                password = edtSignUpPassword.getText().toString().trim();
                mobileNumber = edtSignUpMobile.getText().toString().trim();
                confirmPassword = edtSignUpConfirmPassword.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || mobileNumber.isEmpty() || confirmPassword.isEmpty()) {
                    displayToast("Fill all fields");
                } else if (!password.equals(confirmPassword)) {
                    displayToast("Password and Confirm password should be the same");
                } else {

                    registerUser(email, name, password, mobileNumber);
                }
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

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String encodeEmail(String email) {
        return email.replace('.', ',');
    }

    public void registerUser(final String email, final String name, final String password, final String mobileNumber) {
        final String encodedEmail = encodeEmail(email);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(USERS_ROOT);

        // Check if email is already registered
        databaseReference.child(encodedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email already registered, navigate to login page
                    displayToast("Email already registered. Please login.");

                } else {
                    // Email not registered, proceed with registration
                    String userId = databaseReference.push().getKey();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put(EMAIL_KEY, email);
                    userData.put(NAME_KEY, name);
                    userData.put(PASSWORD_KEY, password);
                    userData.put(USER_ID_KEY, userId);
                    userData.put(MOBILE_NUMBER_KEY, mobileNumber);
                    databaseReference.child(encodedEmail).setValue(userData);
                    saveUserDetailsToFile(userId, email, mobileNumber, name);
                    // Return the generated user ID
                    displayToast("User registered successfully. Email: " + email);
                    //navigate to home
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseHelper", "Error checking if email is registered: " + databaseError.getMessage());
                displayToast("Error checking email registration. Please try again.");
            }
        });
    }
}