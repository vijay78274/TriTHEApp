package com.example.tritheapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tritheapp.databinding.ActivitySecondBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Second extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSIONS = 123;
    ActivitySecondBinding binding;
    FirebaseDatabase database;
    ArrayList<String> emergencyContacts;
    String uid;
    FirebaseAuth auth;
    private static final int REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 124;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        uid=auth.getUid();
        getSupportActionBar();
        emergencyContacts=new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        new ContactRetrievalTask().execute();

        binding.emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Second.this, Emergency.class);
                startActivity(intent);
            }
        });
        binding.emergency.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                sendEmergencyAlert();
                return false;
            }
        });
        binding.meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Second.this,Meeting.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
//        FirebaseAuth.getInstance().signOut();
        if (id == R.id.profile) {
            Intent intent = new Intent(Second.this, Profile.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.settings) {
            Intent intent = new Intent(Second.this, Settings.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void sendEmergencyAlert() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkLocationPermission()) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
//                Uri locationUri = Uri.parse(latitude + "-" + longitude);
                String mapsUrl = "https://www.google.com/maps/search/?api=1&qerry=" + latitude+longitude;
                // Compose alert message
                String alertMessage = "Emergency! I need help! My location: "+latitude+" "+longitude+" Google map: " +mapsUrl;

                SmsManager smsManager = SmsManager.getDefault();
                for (String contact : emergencyContacts) {
                    smsManager.sendTextMessage(contact, null, alertMessage, null, null);
                    Log.d("SMS","send");
                }
                // Display confirmation to the user
                Toast.makeText(this, "Emergency alert sent"+emergencyContacts, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unable to retrieve location", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            grantPermission();
        }
    }

    private class ContactRetrievalTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            // Retrieve contacts from Firebase Realtime Database
            database.getReference().child("EmergencyContacts").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String phone = snapshot1.getValue(String.class);
                        emergencyContacts.add(phone);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return emergencyContacts;
        }
    }
    private boolean checkLocationPermission() {
        // Check if ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions are granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED;
    }
    public void grantPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.SEND_SMS},
                REQUEST_LOCATION_PERMISSIONS);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendEmergencyAlert();
            } else {
                Toast.makeText(Second.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                grantPermission();
            }
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
//            if (resultCode == RESULT_OK) {
//
//            } else {
//                Toast.makeText(Second.this, "Battery optimization permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

}