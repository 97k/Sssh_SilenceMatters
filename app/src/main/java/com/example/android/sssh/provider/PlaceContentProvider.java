package com.example.android.sssh.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by aadi on 26/7/17.
 */

public class PlaceContentProvider extends ContentProvider {
    /**
     * @param PLACES and PLACES_ID used to distinguish the two types of path for content resolver
     */
    private static final String TAG_NAME = PlaceContentProvider.class.getSimpleName();
    public static final int PLACES = 100;
    public static final int PLACES_ID = 101;

    private static UriMatcher sUrimatcher = getUriMatcher();

    public static UriMatcher getUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        /**
         * The calls to addURI() go here, for all of the content URI patterns that the provider
         * should recognize.
         */

        /**
         * Sets the integer value for multiple rows in places to 100. Notice that no wildcard is used
         * in the path
         */
        uriMatcher.addURI(PlaceContract.AUTHORITY, PlaceContract.PATH_PLACES, PLACES);

        /**
         * Sets the code for a single row to 101. In this case, the "#" wildcard is
         * used. "content://com.example.android/Sssh/3" matches, but
         * "content://content://com.example.android/pets doesn't.
         */
        uriMatcher.addURI(PlaceContract.AUTHORITY, PlaceContract.PATH_PLACES + "/#", PLACES_ID);
        return uriMatcher;
    }

    private PlaceDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new PlaceDbHelper(context);
        return true;
    }

    /**
     * Handles request for data by URI.
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // Matching the uri and then fitting the needs inside the switch case.
        int match = sUrimatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case PLACES:
                // Means we have to query for the whole table.
                cursor = database.query(PlaceContract.PlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Can not query Unknown uri : " + uri);


        }
        // Set a notification URI on the Cursor and return that Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.i(TAG_NAME, "Not yet implemented!");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Uri returnUri; /*URi to be returned*/
        //Match the URI.
        int match = sUrimatcher.match(uri);
        switch (match) {
            case PLACES:
                long id = database.insert(PlaceContract.PlaceEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(PlaceContract.PlaceEntry.CONTENT_URI, id);
                } else
                    throw new android.database.SQLException("Failed to insert row in the database " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Check the content uri" + uri);
        }
        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get access to the sqlite database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUrimatcher.match(uri);
        int numberOfPlacesDeleted;
        switch (match) {
            case PLACES_ID:
                selection = "=?";
                selectionArgs = new String[]{uri.getPathSegments().get(1)};/*Gets the first index of the uri starting form zero*/
                numberOfPlacesDeleted = database.delete(PlaceContract.PlaceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Check the uri : " + uri);
        }
        if (numberOfPlacesDeleted!=0) getContext().getContentResolver().notifyChange(uri, null);

        return numberOfPlacesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        Log.i(TAG_NAME, "Update Method called!");
        // Get the access to the database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUrimatcher.match(uri);
        int numOfPlacesUpdated;
        switch (match){
            case PLACES_ID:


                numOfPlacesUpdated = database.update(PlaceContract.PlaceEntry.TABLE_NAME, contentValues,
                        s, strings);
                Log.i(TAG_NAME, "Place Updated");
                break;
            default:
                throw new UnsupportedOperationException("Check the uri(update) : " +uri);


        }
        if (numOfPlacesUpdated!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numOfPlacesUpdated;

    }
}
