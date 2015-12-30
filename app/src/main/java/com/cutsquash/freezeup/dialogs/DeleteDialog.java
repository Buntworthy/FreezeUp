package com.cutsquash.freezeup.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.cutsquash.freezeup.R;

/**
 * Created by Justin on 29/12/2015.
 */
public class DeleteDialog extends DialogFragment {

    // Interface to communicate category selection
    public interface DeleteDialogListener {
        public void deleteItem();
    }

    // Use this instance of the interface to deliver action events
    private DeleteDialogListener mListener;

    public void setListener(DeleteDialogListener listener) {
        mListener = listener;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Testing")
                .setPositiveButton("Positive", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.deleteItem();
                    }
                })
                .setNegativeButton("Negative", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
