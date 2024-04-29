package com.example.tritheapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.tritheapp.databinding.ActivitySecondBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Second extends AppCompatActivity {
ActivitySecondBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar();
        binding.emergency.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(Second.this, Emergency.class);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        FirebaseAuth.getInstance().signOut();
        if (id == R.id.profile) {
            Intent intent = new Intent(Second.this, Profile.class);
            startActivity(intent);
            return true;
        }
        else if(id==R.id.settings){
            Intent intent = new Intent(Second.this, Profile.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}