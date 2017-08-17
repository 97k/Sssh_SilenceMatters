package com.example.android.sssh.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.android.sssh.R;

/**
 * Created by aadi on 17/8/17.
 */

public class Dialog extends DialogFragment {
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        build.setTitle(R.string.action_choose)
                .setItems(R.array.selection_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                DialogFragment dialog = new EditDialog();
                                dialog.show(getActivity().getSupportFragmentManager(), "EditDialog");
                                break;
                            case 1:
                                //TODO Implement delete behaviour
                        }
                    }
                });

        return build.create();
    }
}
