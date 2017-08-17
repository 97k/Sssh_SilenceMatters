package com.example.android.sssh.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by aadi on 26/7/17.
 */

public class PlaceContract  {
    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.android.sssh";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "places" directory
    public static final String PATH_PLACES = "places";

    public static final class PlaceEntry implements BaseColumns{
        //Task entry content uri.
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACES).build();

        /*
         * Table Name and Column Name for the database.
         */
        public static final String TABLE_NAME = "places";
        public static final String COLUMN_PLACE_ID = "placeID";
        public static final String COLUMN_PLACE_NAME_BY_USER = "placeName";
        public static final int SILENT_MODE = 1;
        public static final int VIBRATE_MODE = 2;
        public static final int UNKNOWN_MODE = 0;
    }
}
