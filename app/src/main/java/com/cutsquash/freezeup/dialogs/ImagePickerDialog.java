package com.cutsquash.freezeup.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.cutsquash.freezeup.R;

import java.io.File;

/**
 * Created by Justin on 30/12/2015.
 */
public class ImagePickerDialog extends DialogFragment {

    public interface ImagePickerListener {
        void imagePickerSelected(int choice);
    }

    private final String[] testArray = new String[] {"Camera", "Gallery"};
    private ImagePickerListener mImagePickerListener = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("testing")
                .setItems(testArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        if (mImagePickerListener != null) {
                            mImagePickerListener.imagePickerSelected(which);
                        }

                    }
                });
        return builder.create();
    }

    public void setImagePickerListener(ImagePickerListener listener) {
        mImagePickerListener = listener;
    }
}
