package com.example.wire.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wire.R;
import com.example.wire.auth.LoginActivity;
import com.example.wire.databinding.ActivityBookingBinding;
import com.example.wire.databinding.ActivityBookingDetailsBinding;
import com.example.wire.model.HelperClass;
import com.example.wire.model.MachineModel;
import com.example.wire.model.UserModel;
import com.example.wire.user.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class BookingDetailsActivity extends AppCompatActivity {
    ActivityBookingDetailsBinding binding;
    MachineModel machineModel;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");

        if (getIntent().getExtras() != null) {
            machineModel = (MachineModel) getIntent().getSerializableExtra("data");
        }

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        dbRefUsers.child(machineModel.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // If user data exists in the database
                if (snapshot.exists()) {
                    // Retrieve UserModel object from the database
                    UserModel model = snapshot.getValue(UserModel.class);
                    if (machineModel != null) {
                        binding.tvStatus.setTextColor(getColor(R.color.yellow));
                        binding.tvStatus.setText(machineModel.getStatus());
                        binding.nameEt.setText(machineModel.getMachineName());
                        binding.brandNameEt.setText(machineModel.getBrandName());
                        binding.capacityEt.setText(machineModel.getWeightCapacity());
                        binding.colorEt.setText(machineModel.getColor());
                        binding.priceEt.setText(machineModel.getPrice());
                        binding.detailsEt.setText(machineModel.getDetails());
                        Glide.with(binding.ivMachine).load(machineModel.getImage()).into(binding.ivMachine);
                        binding.userNameEt.setText(model.getName());
                        binding.userEmailEt.setText(model.getEmail());
                        binding.userPhoneEt.setText(model.getPhone());
                        if (machineModel.getBookingAddress().isEmpty()){
                            binding.layoutAddress.setVisibility(View.GONE);
                            binding.deliveryModeEt.setText("Collection");
                        }else{
                            binding.deliveryModeEt.setText("Delivery");
                            binding.layoutAddress.setVisibility(View.VISIBLE);
                            binding.userAddressEt.setText(machineModel.getBookingAddress());
                        }
                    }

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // If there's an error retrieving user data
                progressDialog.dismiss();
                Toast.makeText(BookingDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}