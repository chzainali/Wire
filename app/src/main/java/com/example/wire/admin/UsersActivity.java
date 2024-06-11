package com.example.wire.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.wire.R;
import com.example.wire.adapter.UsersAdapter;
import com.example.wire.databinding.ActivityUsersBinding;
import com.example.wire.model.OnItemClick;
import com.example.wire.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {
    ActivityUsersBinding binding;
    DatabaseReference dbRefUsers;
    FirebaseAuth auth;
    UsersAdapter usersAdapter;
    ProgressDialog progressDialog;
    ArrayList<UserModel> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show();
        dbRefUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    usersList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            UserModel model = ds.getValue(UserModel.class);
                            if (!model.getId().equals(auth.getCurrentUser().getUid())) {
                                usersList.add(model);
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (usersList.isEmpty()) {
                        binding.noContactFound.setVisibility(View.VISIBLE);
                        binding.rvUsers.setVisibility(View.GONE);
                    } else {
                        userAdapter();
                        binding.noContactFound.setVisibility(View.GONE);
                        binding.rvUsers.setVisibility(View.VISIBLE);
                    }

                } else {
                    binding.noContactFound.setVisibility(View.VISIBLE);
                    binding.rvUsers.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(UsersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void userAdapter() {
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(UsersActivity.this));
        usersAdapter = new UsersAdapter(usersList, UsersActivity.this, new OnItemClick() {
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(UsersActivity.this, ChatActivity.class);
                intent.putExtra("userId", usersList.get(pos).getId());
                startActivity(intent);
            }
        });
        binding.rvUsers.setAdapter(usersAdapter);
    }
    
}