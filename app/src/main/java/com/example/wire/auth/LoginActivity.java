package com.example.wire.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.wire.UserMainActivity;
import com.example.wire.user.MainActivity;
import com.example.wire.R;
import com.example.wire.admin.AdminMainActivity;
import com.example.wire.databinding.ActivityLoginBinding;
import com.example.wire.model.HelperClass;
import com.example.wire.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String adminEmail = "wireadmin@gmail.com";
    String adminPassword = "@wireadmin123";
    String email, password;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.llBottom.setOnClickListener(view ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the input fields are validated
                if (isValidated()){
                    // Show a progress dialog to indicate login process
                    progressDialog.show();

                    // Sign in with email and password using FirebaseAuth
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // When sign-in attempt is complete
                            if (task.isSuccessful()) {
                                // If the sign-in is successful
                                // Check if the user is an admin
                                if (Objects.equals(email, adminEmail) && Objects.equals(password, adminPassword)){
                                    // If user is an admin, redirect to AdminMainActivity
                                    startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
                                    finish();
                                } else {
                                    // If user is not an admin

                                    // Retrieve user data from Firebase Realtime Database
                                    dbRefUsers.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            // If user data exists in the database
                                            if (snapshot.exists()) {
                                                // Retrieve UserModel object from the database
                                                UserModel model = snapshot.getValue(UserModel.class);

                                                // Store user data in a helper class for future reference
                                                HelperClass.users = model;
                                                progressDialog.dismiss();
                                                startActivity(new Intent(LoginActivity.this, UserMainActivity.class));
                                                finishAffinity();
                                            } else {
                                                // If user data doesn't exist
                                                progressDialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // If there's an error retrieving user data
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                // If sign-in attempt fails
                                showMessage(String.valueOf(task.getException()));
                                progressDialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // If sign-in fails due to an exception
                            showMessage(e.getMessage());
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });

    }

    private Boolean isValidated() {
        email = binding.emailEt.getText().toString().trim();
        password = binding.passET.getText().toString().trim();

        if (email.isEmpty()) {
            showMessage("Please enter email");
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
            showMessage("Please enter email in correct format");
            return false;
        }
        if (password.isEmpty()) {
            showMessage("Please enter password");
            return false;
        }

        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}