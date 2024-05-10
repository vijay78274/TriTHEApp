package com.example.tritheapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.tritheapp.databinding.ActivityAddContactBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddContact extends AppCompatActivity {
FirebaseDatabase database;
String contact;
ActivityAddContactBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        binding.set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contact=binding.enterNumber.getText().toString();
                if(checkPattern(contact)){
                    database.getReference().child("EmergencyContacts").push().setValue(contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(AddContact.this, Emergency.class);
                            startActivity(intent);
                        }
                    });
                }
                else
                    Toast.makeText(AddContact.this,"Enter a valid phone number",Toast.LENGTH_SHORT).show();
            }
        });
    }
    boolean checkPattern(String contact){
        String phoneNumberPattern = "^\\+(?:[0-9] ?){6,14}[0-9]$";
        Pattern pattern = Pattern.compile(phoneNumberPattern);
        Matcher matcher = pattern.matcher(contact);
        return matcher.matches();
    }
}