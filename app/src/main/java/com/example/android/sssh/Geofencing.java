package com.example.android.sssh;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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

public class Geofencing implements ResultCallback<Status> {

    private static final long GEOFENCE_TIMEOUT = 86400;
    private static final float GEOFENCE_RADI = 25;
    private static final String TAG_NAME = Geofencing.class.getSimpleName();
    private Context mContext;
    private GoogleApiClient mClient;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    public Geofencing(Context context, GoogleApiClient client) {
        mContext=context;
        mClient = client;
        mGeofenceList = new ArrayList<>();
    }

    public void registerAllGeofences(){
        if (mClient==null || !mClient.isConnected()
                || mGeofenceList==null || mGeofenceList.size()==0) return;
        try {
            LocationServices.GeofencingApi.addGeofences(mClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent())
                    .setResultCallback(this);
        }catch (SecurityException e){
            Log.e(TAG_NAME, e.getMessage());
        }
    }

    public void unregisterAllGeofencingRequest(){
        if (mClient==null || !mClient.isConnected()) return;
            try {
                LocationServices.GeofencingApi.removeGeofences(mClient,
                        //The same which we used to register the geofence
                        getGeofencePendingIntent())
                        .setResultCallback(this);

            }catch (SecurityException se){
                Log.e(TAG_NAME, se.getMessage());
            }
    }

    public void updateGeofenceList(PlaceBuffer places){
        mGeofenceList = new ArrayList<>();
        if (places==null || places.getCount()==0) return;
        for (Place place : places){
            String placeId = place.getId();
            double placeLat = place.getLatLng().latitude;
            double placeLng = place.getLatLng().longitude;
            
            Geofence builder = new Geofence.Builder()
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setRequestId(placeId)
                    .setCircularRegion(placeLat, placeLng, GEOFENCE_RADI)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            mGeofenceList.add(builder);
        }
    }

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
    public void onResult(@NonNull Status status) {
        Log.e(TAG_NAME, "This is on result call!");

    }
}