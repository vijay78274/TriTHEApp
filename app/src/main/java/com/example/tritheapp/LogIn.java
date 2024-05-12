package com.example.tritheapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.tritheapp.databinding.ActivityLogInBinding;
import com.example.tritheapp.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LogIn extends AppCompatActivity {
ActivityLogInBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    String mail;
    String pass;
    String userName;
    FirebaseDatabase database;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging In");
        dialog.setCancelable(false);
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mail=binding.email.getText().toString();
                pass=binding.password.getText().toString();
                userName = mail.split("@")[0];
                if(mail.isEmpty()){
                    binding.email.setError("This field cannot be empty");
                }
                else if(pass.isEmpty()){
                    binding.password.setError("Enter password");
                }
                else{
                    LogInUser(mail,pass);
                }
            }
        });
    }
    private void LogInUser(String email, String password) {
        dialog.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if(auth.getCurrentUser().isEmailVerified()){
                                Toast.makeText(LogIn.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Users user = new Users(auth.getUid(),userName,"");
                                database.getReference("users").child(auth.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                        startActivity(new Intent(LogIn.this,Second.class));
                                    }
                                });
                            }
                            else{
                                Toast.makeText(LogIn.this,"Please verify your email",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LogIn.this,"Incorrect email or password",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}