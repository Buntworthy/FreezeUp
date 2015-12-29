package com.cutsquash.freezeup.utils;

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
}
