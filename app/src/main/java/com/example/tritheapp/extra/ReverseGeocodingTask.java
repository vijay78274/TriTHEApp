package com.example.tritheapp.extra;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReverseGeocodingTask extends AsyncTask<Double, Void, String> {

    private static final String TAG = "ReverseGeocodingTask";
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/reverse?format=jsonv2";

    private ReverseGeocodingListener listener;

    public ReverseGeocodingTask(ReverseGeocodingListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Double... params) {
        double latitude = params[0];
        double longitude = params[1];

        String result = "";
        try {
            URL url = new URL(NOMINATIM_URL + "&lat=" + latitude + "&lon=" + longitude);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();

            result = sb.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error retrieving reverse geocoding response: " + e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject json = new JSONObject(result);
            String displayName = json.optString("display_name");
            listener.onReverseGeocodingCompleted(displayName);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing reverse geocoding response: " + e.getMessage());
            listener.onReverseGeocodingFailed();
        }
    }

    public interface ReverseGeocodingListener {
        void onReverseGeocodingCompleted(String displayName);

        void onReverseGeocodingFailed();
    }
}