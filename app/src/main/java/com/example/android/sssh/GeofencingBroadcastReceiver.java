package com.example.android.sssh;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by aadi on 27/7/17.
 */

public class GeofencingBroadcastReceiver extends BroadcastReceiver {


    private static final String TAG_NAME = GeofencingBroadcastReceiver.class.getSimpleName();
    private NotificationManager nm;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG_NAME, "onReceive called");

        // See the getPendingIntent method in Geofencing class.
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        setMode(context, geofenceTransition);
        // Check the transition type to display relevant image.
        sendNotification(geofenceTransition, context);
    }

    public void setMode(Context context, int geofenceTransition) {

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Getting the shared pref as selected by user.
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            String selectedMode = sharedPrefs.getString(context.getString(R.string.modes_selection_key),
                    context.getString(R.string.settings_mode_selection_default));
            Log.e(TAG_NAME, "selected mode is : " + selectedMode);

            if (selectedMode.equals("Vibrate"))
                setRingerMode(context, AudioManager.RINGER_MODE_VIBRATE);
            else
                setRingerMode(context, AudioManager.RINGER_MODE_SILENT);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            setRingerMode(context, AudioManager.RINGER_MODE_NORMAL);
        }else
            Log.i(TAG_NAME, String.format("Unknown transition : %s", geofenceTransition));
    }

    private void setRingerMode(Context context, int mode){
         /*NotificationManager*/ nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Checking for the DND permission for API 24+
        if (Build.VERSION.SDK_INT<24 || (Build.VERSION.SDK_INT>=24 && nm.isNotificationPolicyAccessGranted())){
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(mode);
        }
    }

    // Helper method for sending the notification.
    private void sendNotification(int geofenceTransition, Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Intent notificationIntent = new Intent(context, MainActivity.class);

        // Construct a Task Stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the Main Activity as parent to the stack.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content intent to the next stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get the Pending Intent to get the entire back stack.
        /**
         * Although actions are optional, you should add at least one action to your notification.
         * An action takes users directly from the notification to an Activity in your application,
         * where they can look at the event that caused the notification or do further work.
         * Inside a notification, the action itself is defined by a PendingIntent containing an
         * Intent that starts an Activity in your application.
         */
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (geofenceTransition==Geofence.GEOFENCE_TRANSITION_ENTER){
            builder.setSmallIcon(R.drawable.ic_do_not_disturb_on_black_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_do_not_disturb_on_black_24dp))
                    .setContentTitle(context.getString(R.string.priority_mode));

        }else if (geofenceTransition==Geofence.GEOFENCE_TRANSITION_EXIT){
            builder.setSmallIcon(R.drawable.ic_do_not_disturb_off_black_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_do_not_disturb_off_black_24dp))
                    .setContentTitle(context.getString(R.string.back_to_normal));
        }
        builder.setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);
        nm.notify(1, builder.build());

    }
}
