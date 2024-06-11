package com.example.wire.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.wire.R;
import com.example.wire.auth.LoginActivity;
import com.example.wire.databinding.ActivityAdminMainBinding;
import com.example.wire.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;

public class AdminMainActivity extends AppCompatActivity {
    ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvMachines.setOnClickListener(v -> startActivity(new Intent(AdminMainActivity.this, MachinesActivity.class)));
        binding.cvBookings.setOnClickListener(v -> startActivity(new Intent(AdminMainActivity.this, BookingActivity.class)));
        binding.cvChats.setOnClickListener(v -> startActivity(new Intent(AdminMainActivity.this, UsersActivity.class)));
        binding.ivNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, NotificationsActivity.class);
                intent.putExtra("from", "admin");
                startActivity(intent);
            }
        });

        binding.ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

    }
}