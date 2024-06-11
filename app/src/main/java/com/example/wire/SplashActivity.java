package com.example.wire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.wire.auth.LoginActivity;
import com.example.wire.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    Animation fromBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        binding.tvName.setAnimation(fromBottom);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // Sleep for 3000 milliseconds (3 seconds)
                    sleep(3000);
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    // Handle any interruption exceptions and show a toast message
                    Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        thread.start();

    }
}