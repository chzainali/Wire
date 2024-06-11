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
import com.example.wire.adapter.MachinesAdapter;
import com.example.wire.adapter.NotificationsAdapter;
import com.example.wire.databinding.ActivityBookingBinding;
import com.example.wire.databinding.ActivityNotificationsBinding;
import com.example.wire.model.MachineModel;
import com.example.wire.model.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationsActivity extends AppCompatActivity {
    ActivityNotificationsBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefNotifications;
    List<NotificationModel> list = new ArrayList<>();
    NotificationsAdapter adapter;
    String from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() != null) {
            from = getIntent().getStringExtra("from");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefNotifications = FirebaseDatabase.getInstance().getReference("Notifications");

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show();
        dbRefNotifications.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    progressDialog.dismiss();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            NotificationModel model = ds.getValue(NotificationModel.class);
                            if (Objects.equals(from, "user")) {
                                if (Objects.equals(model.getUserId(), auth.getCurrentUser().getUid())) {
                                    list.add(model);
                                }
                            } else {
                                list.add(model);
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    setAdapter();

                    if (list.isEmpty()) {
                        binding.tvNoGoalFound.setVisibility(View.VISIBLE);
                        binding.rvGoals.setVisibility(View.GONE);
                    } else {
                        binding.tvNoGoalFound.setVisibility(View.GONE);
                        binding.rvGoals.setVisibility(View.VISIBLE);
                    }
                } else {
                    progressDialog.dismiss();
                    binding.tvNoGoalFound.setVisibility(View.VISIBLE);
                    binding.rvGoals.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(NotificationsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setAdapter() {
        adapter = new NotificationsAdapter(this, list, pos -> {
            NotificationModel model = list.get(pos);
            dbRefNotifications.child(model.getId()).removeValue();
            list.remove(pos);
            adapter.notifyDataSetChanged();
        });
        binding.rvGoals.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGoals.setAdapter(adapter);
    }

}