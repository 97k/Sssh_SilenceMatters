package com.example.android.sssh.Dialog;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.android.sssh.R;
import com.example.android.sssh.provider.PlaceContract;

/**
 * Created by aadi on 17/8/17.
 */

public class Dialog extends DialogFragment {

    private Bundle bundle;
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        build.setTitle(R.string.action_choose)
                .setItems(R.array.selection_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                //Edit
                                bundle = getArguments();
                                // Retrieving the data from the bundle.
                                int positionClicked = bundle.getInt(getString(R.string.bundle_position_clicked));
                                DialogFragment dialog = new EditDialog();

                                // Sending the position clicked to the dialog fragment
                                Bundle bundledata = new Bundle();
                                bundledata.putInt(getString(R.string.bundle_position_clicked), positionClicked);
                                dialog.setArguments(bundledata);
                                dialog.show(getActivity().getSupportFragmentManager(), "EditDialog");
                                break;
                            case 1:
                                // Delete.
                                // Retrieving the data from the bundle.
                                bundle = getArguments();
                                int positionClicked1 = bundle.getInt(getString(R.string.bundle_position_clicked));
                                Uri uri = ContentUris.withAppendedId(PlaceContract.PlaceEntry.CONTENT_URI, positionClicked1);

                                String selection = PlaceContract.PlaceEntry._ID + "=?";
                                String[] selectionArgs = new String[]{String.valueOf(positionClicked1+1)};
                                getActivity().getContentResolver().delete(uri, selection, selectionArgs);
                                // Retrieving the data from the bundle.
                        }
                    }
                });

        return build.create();
    }
}
