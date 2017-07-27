package com.example.android.sssh;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.example.android.sssh.provider.PlaceContract;
import com.google.android.gms.common.api.GoogleApiClient;

public class SettingsActivity extends AppCompatActivity {

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;

    // Member Variables.
    private GoogleApiClient mClient;
    private Spinner mModeSelectionSpinner;
    private int ringerMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupSpinner();

    }

    @Override
    public void onResume() {
        super.onResume();
        CheckBox locationPermissionCheckBox = (CheckBox) findViewById(R.id.location_permission_checkbox);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationPermissionCheckBox.setChecked(true);
            locationPermissionCheckBox.setEnabled(false);
        } else
            locationPermissionCheckBox.setChecked(false);
    }

    public void onLocationPermissionClicked(View view) {
        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_FINE_LOCATION);

    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter modeSpinnerSelection = ArrayAdapter.createFromResource(this,
                R.array.mode_selection, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        modeSpinnerSelection.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mModeSelectionSpinner.setAdapter(modeSpinnerSelection);

        // Set the integer mSelected to the constant values
        mModeSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.silent_mode))) {
                        ringerMode = PlaceContract.PlaceEntry.SILENT_MODE; // Male
                    } else if (selection.equals(getString(R.string.vibrate_mode))) {
                        ringerMode = PlaceContract.PlaceEntry.VIBRATE_MODE; // Female
                    } else {
                        ringerMode = PlaceContract.PlaceEntry.UNKNOWN_MODE; // Unknown
                    }
                }
                // Update the ringer mode in the sharedPreferences.

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ringerMode = 0; //Unknown
            }
        });
    }
}
