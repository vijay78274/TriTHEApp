package com.example.tritheapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tritheapp.Adapter.MeetingAdapter;
import com.example.tritheapp.databinding.ActivityMeetingBinding;
import com.example.tritheapp.models.meeting;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Meeting extends AppCompatActivity {
ActivityMeetingBinding binding;
ArrayList<meeting> meetings;
FirebaseDatabase database;
String uid;
FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMeetingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        meetings=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        uid=auth.getUid();
        MeetingAdapter adapter = new MeetingAdapter(meetings,this);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setAdapter(adapter);
        database.getReference().child("meeting").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    meeting meeting1 = dataSnapshot.getValue(meeting.class);
                    meetings.add(meeting1);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.addMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Meeting.this,AddMeeting.class);
                startActivity(intent);
            }
        });
    }
}