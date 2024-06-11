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
import com.example.wire.databinding.ActivityBookingBinding;
import com.example.wire.databinding.ActivityMachinesBinding;
import com.example.wire.model.MachineModel;
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

public class BookingActivity extends AppCompatActivity {
    ActivityBookingBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefMachines;
    List<MachineModel> list = new ArrayList<>();
    MachinesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefMachines = FirebaseDatabase.getInstance().getReference("Machines");

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
        dbRefMachines.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    progressDialog.dismiss();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            MachineModel model = ds.getValue(MachineModel.class);
                            if (Objects.equals(model.getStatus(), "Booked")){
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
                Toast.makeText(BookingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setAdapter() {
        adapter = new MachinesAdapter(this, list, pos -> {
            MachineModel model = list.get(pos);
            if (Objects.equals(model.getStatus(), "Booked")){
                Intent intent = new Intent(this, BookingDetailsActivity.class);
                intent.putExtra("data", model);
                startActivity(intent);
            }
        });
        binding.rvGoals.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGoals.setAdapter(adapter);
    }

}