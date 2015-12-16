package com.cutsquash.freezeup.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.cutsquash.freezeup.R;

/**
 * Created by Justin on 16/12/2015.
 */
public class CategoryDialog extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.dialog_category_item);
        arrayAdapter.add("Testing 1");
        arrayAdapter.add("Testing 2");
        arrayAdapter.add("Testing 3");
        arrayAdapter.add("Testing 4");
        arrayAdapter.add("Testing 5");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("temp", "Clicked!" + Integer.toString(which));
            }
        });

        builder.setTitle("Select category");

        return builder.create();
    }
}
