package com.cutsquash.freezeup.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.cutsquash.freezeup.DetailActivityFragment;
import com.cutsquash.freezeup.R;

import java.util.Calendar;

/**
 * Created by Justin on 03/12/2015.
 */
public class DateDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    // Interface to communicate date selection
    public interface DateDialogListener {
        public void dateSelected(int year, int month, int day);
    }

    // Use this instance of the interface to deliver action events
    private DateDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mListener.dateSelected(year, month, day);
    }

    public void setListener(DateDialogListener listener) {
        this.mListener = listener;
    }
}
