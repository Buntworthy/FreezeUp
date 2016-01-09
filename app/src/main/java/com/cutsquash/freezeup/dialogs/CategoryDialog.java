package com.cutsquash.freezeup.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cutsquash.freezeup.R;

/**
 * Created by Justin on 16/12/2015.
 */
public class CategoryDialog extends DialogFragment {

    public static final String TAG = CategoryDialog.class.getSimpleName();

    // Interface to communicate category selection
    public interface CategoryDialogListener {
        public void categorySelected(int category);
    }

    private class CategoryArrayAdapter extends ArrayAdapter<String> {

        public CategoryArrayAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_category_item, null);

            TextView itemView = (TextView) view.findViewById(R.id.categoryDialog_text);
            itemView.setText(getItem(position));
            return view;
        }
    }

    // Use this instance of the interface to deliver action events
    private CategoryDialogListener mListener;

    public void setListener(CategoryDialogListener listener) {
        mListener = listener;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final CategoryArrayAdapter arrayAdapter = new CategoryArrayAdapter(
                getContext());
        String[] categoryNames = getResources().getStringArray(R.array.category_strings);
        arrayAdapter.addAll(categoryNames);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.categorySelected(which);

            }
        });

        builder.setTitle("Select category");

        return builder.create();
    }
}
