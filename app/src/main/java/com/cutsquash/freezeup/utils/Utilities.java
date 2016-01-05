package com.cutsquash.freezeup.utils;

import android.content.res.Resources;

import com.cutsquash.freezeup.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Justin on 29/12/2015.
 */
public class Utilities {

    public static String formatDate(Long date) {
        DateFormat df = DateFormat.getDateInstance();
        String itemDate = df.format(new Date(date));
        return itemDate;
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static String quantityToShortString(int quantity) {
        if (quantity >= 0) {
            return Integer.toString(quantity);
        } else {
            switch (quantity) {
                case -1:
                    return "L";
                case -2:
                    return "M";
                case -3:
                    return "H";
                default:
                    return "?";
            }
        }
    }

    public static String getCategoryString(Resources resources, int category) {
        String[] categoryNames = resources.getStringArray(R.array.category_strings);
        return categoryNames[category];
    }

}
