package com.example.tritheapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tritheapp.databinding.ActivityProfileBinding;
import com.example.tritheapp.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Profile extends AppCompatActivity {
ActivityProfileBinding binding;
String userName;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    int PICK_IMAGE_REQUEST=123;
    String uid;
    Uri selectedImageUri;
    ProgressDialog dialog;
    Users user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        storage = FirebaseStorage.getInstance();
        user=new Users();
        //dialog
        dialog = new ProgressDialog(this);
        dialog.setMessage("Saving profile");
        dialog.setCancelable(false);
        database.getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
                if(user!=null){
                    binding.txtname.setText(user.getName());
                    String profile= user.profileImage;
                    if(!profile.equals("")){
                        Glide.with(Profile.this).load(profile)
                                .placeholder(R.drawable.account)
                                .into(binding.profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE_REQUEST);
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = binding.txtname.getText().toString();
                if(userName.isEmpty()){
                    binding.txtname.setError("This field cannot be empty");
                }
                dialog.show();
                if(selectedImageUri!=null){
                    StorageReference reference = storage.getReference().child("Profiles").child(uid);
                    reference.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        Users user = new Users(uid, userName,imageUrl);
                                        database.getReference().child("users").child(uid).
                                                setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(Profile.this, Second.class);
                                                        startActivity(intent);
                                                        finishAffinity();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    Users user = new Users(uid, userName,"");
                    database.getReference().child("users").child(uid).
                            setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Profile.this, Second.class);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            });
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.profile.setImageURI(selectedImageUri);
        }
    }
}