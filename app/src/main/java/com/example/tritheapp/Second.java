package com.example.tritheapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tritheapp.databinding.ActivitySecondBinding;
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
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1001;
    ActivitySecondBinding binding;
    MyBroadcast receiver= new MyBroadcast();;
    FirebaseDatabase database;
    ArrayList<String> emergencyContacts;
    private static final int REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 124;
    private static final int REQUEST_CODE_ENABLE_ACCESSIBILITY = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar();
        database = FirebaseDatabase.getInstance();
        new ContactRetrievalTask().execute();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            }
        }
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//        registerReceiver(receiver,filter);

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
        if (!isAccessibilityServiceEnabled()) {
            requestAccessibilityService();
        }
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
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
        } else if (id == R.id.settings) {
            Intent intent = new Intent(Second.this, Profile.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void sendEmergencyAlert() {
        // Get user's location
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
                }
                // Display confirmation to the user
                Toast.makeText(this, "Emergency alert sent", Toast.LENGTH_SHORT).show();
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
            ArrayList<String> contacts = new ArrayList<>();
            database.getReference().child("EmergencyContacts").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        String phone = snapshot1.getValue(String.class);
                        contacts.add(phone);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
            return contacts;
        }

        @Override
        protected void onPostExecute(ArrayList<String> contacts) {
            super.onPostExecute(contacts);
            // Process the retrieved contacts
            emergencyContacts = contacts;
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
            if (resultCode == RESULT_OK) {

            } else {
                Toast.makeText(Second.this, "Battery optimization permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CODE_ENABLE_ACCESSIBILITY) {
            if (isAccessibilityServiceEnabled()) {
                Toast.makeText(this, "Accessibility service enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Accessibility service not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
    private boolean isAccessibilityServiceEnabled() {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + MyAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            // Accessibility settings not found
        }

        String settingValue = Settings.Secure.getString(
                getApplicationContext().getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return settingValue != null && settingValue.contains(service);
    }

    private void requestAccessibilityService() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ACCESSIBILITY);
    }
}