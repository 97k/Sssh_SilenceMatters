package com.example.android.sssh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by aadi on 27/7/17.
 */

public class GeofencingBroadcastReceiver extends BroadcastReceiver {

    private Context mContext;
    private static final String TAG_NAME = GeofencingBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
         if (geofenceTransition== Geofence.GEOFENCE_TRANSITION_ENTER){

         }
    }

    public void setMode(Context context, int geofenceTransition){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if (geofenceTransition==Geofence.GEOFENCE_TRANSITION_ENTER){
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
    }
}
