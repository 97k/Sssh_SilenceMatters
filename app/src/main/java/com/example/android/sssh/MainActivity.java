package com.example.android.sssh;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.sssh.Dialog.AskForPermissionDialog;
import com.example.android.sssh.Dialog.Dialog;
import com.example.android.sssh.provider.PlaceContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , PlacesRV_Adapter.ItemLongClickListener {

    private static final String TAG_NAME = MainActivity.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;
    private FloatingActionButton addPlaceFab;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private RelativeLayout emptyview;
    private RecyclerView mRecyclerView;
    private PlacesRV_Adapter mAdapter;
    private GoogleApiClient mClient;
    private Geofencing mGeofencing;


    /**
     * @param menu The menu to inflate.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlacesRV_Adapter(this, null, this);
        mRecyclerView.setAdapter(mAdapter);
        addPlaceFab = (FloatingActionButton) findViewById(R.id.add_places_fab);
        emptyview = (RelativeLayout) findViewById(R.id.empty_view);


        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // For android nougat onwards you have to take permission for Notifications through
        // isNotificationPolicyAccessGranted().

        if (Build.VERSION.SDK_INT >= 24 && !nm.isNotificationPolicyAccessGranted()) {
            Toast.makeText(this, "Enable this Permission", Toast.LENGTH_LONG).show();
            DialogFragment afp = new AskForPermissionDialog();
            afp.show(getSupportFragmentManager(), "AskForPermissionDialog");
        }


        // Setting up Google Api client.
        // using enable auto manage so that it automatically manage when to connect and suspend the client.
        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

        mGeofencing = new Geofencing(this, mClient);
        /* If there is data in our data base then we register the geofences. See refreshPlacesData
         *  onResult method.
         */
        boolean mIsEnabled = getPreferences(MODE_PRIVATE).getBoolean(getString(R.string.settings_mode_selection_key), false);
        if (mIsEnabled)
            mGeofencing.registerAllGeofences();


    }


    public void refreshPlaceData() {
        Uri uri = PlaceContract.PlaceEntry.CONTENT_URI;
        Cursor data = getContentResolver().query(uri,
                null,
                null,
                null,
                null);
        if (data == null || data.getCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            emptyview.setVisibility(View.VISIBLE);
            return;
        }
        emptyview.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        // Stores the data in the List.
        List<String> placesIds = new ArrayList<String>();
        while (data.moveToNext()) {
            placesIds.add(data.getString(data.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACE_ID)));
        }
        // Adding all the places in the pending intent and by using getPlaceById we are extracting details
        //from the google server
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mClient,
                placesIds.toArray(new String[placesIds.size()]));

        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                // Update the recycler view.
                mAdapter.swapPlaces(places);
                mGeofencing.updateGeofenceList(places);
                mGeofencing.registerAllGeofences();
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.settings_mode_selection_key), true);
                editor.commit();

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                //TODO perform the delete option here.
                return true;

            case R.id.configure_sssh:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onFABClicked(View view) {
        // Accessing the location Permission and if not have, we ask for it using run time permission model.
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
            return;

        }
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(this);
            startActivityForResult(i, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG_NAME, String.format("PlacePicker Exception: %s", e.getMessage()));
        }

        refreshPlaceData();

    }

    /**
     * This onActivityResult is result of startActivityForResult method which is invoked back in
     * refreshPlaceData method.
     *
     * @param requestCode Request code is same that we have passed while calling startActivityForResult.
     * @param resultCode  The code which we get from the second activity
     * @param data        Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);

            if (place == null) {
                Log.i(TAG_NAME, "No place Selected!");
                return;
            }
            String placeName = place.getName().toString();
            String placeAdd = place.getAddress().toString();
            String placeId = place.getId();
            Log.e(TAG_NAME, "we are getting the place!" + placeId);
            // Insert the new place in the DB.
            ContentValues values = new ContentValues();
            values.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, placeId);
            getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI, values);

            // Update.
            refreshPlaceData();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG_NAME, " API connection successful");
        refreshPlaceData();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG_NAME, "API client connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG_NAME, "API connection failed" + connectionResult);
    }

    @Override
    public void onLongClick(int pos) {
        DialogFragment mDialogFragment = new Dialog();
        mDialogFragment.show(getSupportFragmentManager(), "Dialog");

    }
}
