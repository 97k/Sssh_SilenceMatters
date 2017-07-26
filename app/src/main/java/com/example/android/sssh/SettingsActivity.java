package com.example.android.sssh;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.common.api.GoogleApiClient;

public class SettingsActivity extends AppCompatActivity {

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;

    // Member Variables.
    private GoogleApiClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
    @Override
    public void onResume(){
        super.onResume();
        CheckBox locationPermissionCheckBox = (CheckBox)findViewById(R.id.location_permission_checkbox);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED){
            locationPermissionCheckBox.setChecked(true);
            locationPermissionCheckBox.setEnabled(false);
        }else
            locationPermissionCheckBox.setChecked(false);
    }
    public void onLocationPermissionClicked(View view){
        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_FINE_LOCATION);

    }
}
