package com.example.textapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.textapp.databinding.ActivityOtpactivityBinding;

public class OTPActivity extends AppCompatActivity {

    ActivityOtpactivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        binding.phoneLabel.setText("Verify +91 " + phoneNumber);
    }
}