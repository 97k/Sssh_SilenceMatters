package com.example.android.sssh.Dialog;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.example.android.sssh.R;
import com.example.android.sssh.provider.PlaceContract;

/**
 * Created by aadi on 18/8/17.
 */

public class EditDialog extends DialogFragment {
    public static final String TAG = EditDialog.class.getSimpleName();

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.edit_place_dialog, null))
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Todo Save the data in the database.
                        Bundle bundle = getArguments();
                        int pos = bundle.getInt(getString(R.string.bundle_position_clicked));
                        saveName(pos);


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void saveName(int pos) {
        EditText name;
        try {
            name = (EditText) getDialog().findViewById(R.id.nameOfPlaceByUser);
            Log.e(TAG, "Name entered by user is " + name.toString());
            String nameOfPlace = name.getText().toString().trim();
            ContentValues values = new ContentValues();
            values.put(PlaceContract.PlaceEntry.COLUMN_PLACE_NAME_BY_USER, nameOfPlace);
            Log.i(TAG, "Position is = " + pos);
            Uri uri = ContentUris.withAppendedId(PlaceContract.PlaceEntry.CONTENT_URI, pos);
            String selection = PlaceContract.PlaceEntry._ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(pos+1)};
            getActivity().getContentResolver().update(uri, values, selection, selectionArgs);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
