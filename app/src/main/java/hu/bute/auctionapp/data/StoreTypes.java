package hu.bute.auctionapp.data;

import android.content.Context;

import hu.bute.auctionapp.R;

/**
 * Konstansok az áruház-típusokhoz.
 * Created by Varsi on 2014.12.09..
 */
public class StoreTypes {
    public static String ELECTRONIC_DEVICES = "Electronic devices";
    public static String SUPERMARKET = "Supermarket";
    public static String FASHION = "Fashion";
    public static String SERVICES = "Services";
    public static String OTHER = "Other";

    public static boolean isValid(String value) {
        String[] arr = new String[] {ELECTRONIC_DEVICES, SUPERMARKET, FASHION, SERVICES, OTHER};
        for (String s : arr) {
            if (s.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static String[] getValues(Context context) {
        return context.getResources().getStringArray(R.array.store_types);
    }
}
