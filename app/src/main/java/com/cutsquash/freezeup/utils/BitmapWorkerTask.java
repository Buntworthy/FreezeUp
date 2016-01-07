package com.cutsquash.freezeup.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.cutsquash.freezeup.EditActivityFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * Created by Justin on 07/01/2016.
 */ // AsyncTask for processing bitmap from gallery
public class BitmapWorkerTask extends AsyncTask<Void, Void, Void> {
    private final WeakReference<Fragment> fragmentReference;
    private final WeakReference<ImageView> imageViewReference;
    private final File outputFile;
    private final Uri srcUri;

    public BitmapWorkerTask(Fragment fragment, Uri srcUri, File outputFile, ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        this.fragmentReference = new WeakReference<Fragment>(fragment);
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.outputFile = outputFile;
        this.srcUri = srcUri;
    }

    // Decode image in background.
    @Override
    protected Void doInBackground(Void... params) {
        Log.d("BitmapWorkerTask", "Starting...");
        Bitmap bitmap = null;
        final int chunkSize = 1024;  // We'll read in one kB at a time
        byte[] imageData = new byte[chunkSize];
        InputStream in = null;
        OutputStream out = null;

        try {
            in = fragmentReference.get()
                    .getActivity().getContentResolver()
                    .openInputStream(srcUri);
            out = new FileOutputStream(outputFile);

            int bytesRead;
            while ((bytesRead = in.read(imageData)) > 0) {
                out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("BitmapWorkerTask", "finished");
        return null;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Void param) {
        if (imageViewReference != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                Glide.with(fragmentReference.get()).load(outputFile)
                        .override(200, 200)
                        .centerCrop()
                        .signature(new StringSignature(Long.toString(System.currentTimeMillis())))
                        .into(imageView);
            }
        }
    }
}
