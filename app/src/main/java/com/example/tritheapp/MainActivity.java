package com.example.tritheapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.tritheapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukeshsolanki.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    String mail;
    String pass;
    String phoneNumber;
    String verificationID;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        dialog = new ProgressDialog(this);;
        dialog.setMessage("Sending OTP...");
        dialog.setCancelable(false);
        binding.verifyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber=binding.phone.getText().toString();
                if(phoneNumber.isEmpty()){
                    binding.phone.setError("This field cannot be empty");
                }
                else {
                    dialog.show();
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(auth)
                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(MainActivity.this)                 // (optional) Activity for callback binding
                                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {

                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                            super.onCodeSent(verifyId, forceResendingToken);
                                            verificationID = verifyId;
                                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                                            binding.otpView.requestFocus();
                                            dialog.dismiss();
                                        }
                                    }).build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });
        binding.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mail=binding.email.getText().toString();
                pass=binding.password.getText().toString();
                if(mail.isEmpty()){
                    binding.email.setError("This field cannot be empty");
                }
                else if(pass.isEmpty()){
                    binding.password.setError("set a password");
                }
                else{
                    if(verificationID.equals(binding.otpView.getText().toString())) {
                        registerUser(mail,pass);
                    }
                }
            }
        });
    }
    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this,"Registration completed",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this,LogIn.class);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}