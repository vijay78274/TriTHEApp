package com.example.tritheapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.tritheapp.databinding.ActivitySecondBinding;

public class Second extends AppCompatActivity {
ActivitySecondBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}