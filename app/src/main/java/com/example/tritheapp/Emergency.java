package com.example.tritheapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tritheapp.Adapter.RecyclerAdapter;
import com.example.tritheapp.databinding.ActivityEmergencyBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Emergency extends AppCompatActivity {
ActivityEmergencyBinding binding;
RecyclerAdapter adapter;
FirebaseDatabase database;
ArrayList<String> contact;
String uid;
FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmergencyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        uid=auth.getUid();
        contact=new ArrayList<>();
        adapter= new RecyclerAdapter(this, contact);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setAdapter(adapter);
        database.getReference("EmergencyContacts").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String phone = snapshot1.getValue(String.class);
                        contact.add(phone);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Emergency.this, AddContact.class);
                startActivity(intent);
            }
        });
    }
}