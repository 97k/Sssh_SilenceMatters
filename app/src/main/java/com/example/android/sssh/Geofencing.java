package com.example.android.sssh;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aadi on 26/7/17.
 */

public class Geofencing implements ResultCallback {

    private static final long GEOFENCE_TIMEOUT = 24*60*60*1000;
    private static final float GEOFENCE_RADI = 25;
    private static final String TAG_NAME = Geofencing.class.getSimpleName();
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    public Geofencing(Context context, GoogleApiClient client) {
        mContext=context;
        mGoogleApiClient = client;
        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent=null;
    }

    /***
     * Registers the list of Geofences specified in mGeofenceList with Google Place Services
     * Uses {@code #mGoogleApiClient} to connect to Google Place Services
     * Uses {@link #getGeofencingRequest} to get the list of Geofences to be registered
     * Uses {@link #getGeofencePendingIntent} to get the pending intent to launch the IntentService
     * when the Geofence is triggered
     * Triggers {@link #onResult} when the geofences have been registered successfully
     */
    public void registerAllGeofences(){
        if (mGoogleApiClient ==null || !mGoogleApiClient.isConnected()
                || mGeofenceList==null || mGeofenceList.size()==0) return;
        try {
            Log.e(TAG_NAME, "Geofencing is enabled");
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent())
                    .setResultCallback(this);
        }catch (SecurityException e){
            Log.e(TAG_NAME, "Error in registering geofences : " + e.getMessage());
        }
    }

    /***
     * Unregisters all the Geofences created by this app from Google Place Services
     * Uses {@code #mGoogleApiClient} to connect to Google Place Services
     * Uses {@link #getGeofencePendingIntent} to get the pending intent passed when
     * registering the Geofences in the first place
     * Triggers {@link #onResult} when the geofences have been unregistered successfully
     */
    public void unregisterAllGeofencingRequest(){
        if (mGoogleApiClient ==null || !mGoogleApiClient.isConnected()) return;
            try {
                LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,
                        //The same which we used to register the geofence
                        getGeofencePendingIntent())
                        .setResultCallback(this);

            }catch (SecurityException se){
                Log.e(TAG_NAME, se.getMessage());
            }
    }

    /**
     *
     * @param places The placeBuffer result of the getPlaceById call
     */
    public void updateGeofenceList(PlaceBuffer places){
        mGeofenceList = new ArrayList<>();
        if (places==null || places.getCount()==0) return;
        for (Place place : places){
            String placeId = place.getId();
            double placeLat = place.getLatLng().latitude;
            double placeLng = place.getLatLng().longitude;
            
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeId)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(placeLat, placeLng, GEOFENCE_RADI)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            mGeofenceList.add(geofence);
        }
    }

    /**
     *
     * @return pending intent object.
     */
    public GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    public PendingIntent getGeofencePendingIntent(){
        if (mGeofencePendingIntent!=null)
            return mGeofencePendingIntent;
        Intent intent = new Intent(mContext, GeofencingBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }



    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG_NAME, String.format("Error adding/removing geofence : %s",
                result.getStatus().toString()));
    }
}