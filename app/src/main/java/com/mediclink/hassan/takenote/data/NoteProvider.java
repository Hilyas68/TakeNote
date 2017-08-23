package com.mediclink.hassan.takenote.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.URI;

/**
 * Created by hassan on 8/14/2017.
 */

public class NoteProvider extends ContentProvider {

    private static final String AUTHORITY = "com.mediclink.hassan.takenote.data.NoteProvider";
    private static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Note";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH +  "/#", NOTES_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        NoteDBOpenHelper helper = new NoteDBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    Cursor returnCursor;
    int mRowsUpdated;

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (uriMatcher.match(uri) == NOTES_ID) {
            selection = NoteDBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        returnCursor = database.query(NoteDBOpenHelper.TABLE_NOTES, NoteDBOpenHelper.ALL_COLUMNS,
                selection, null, null, null,
                NoteDBOpenHelper.NOTE_CREATED + " DESC");

        getContext().getContentResolver().notifyChange(uri, null);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long id = database.insert(NoteDBOpenHelper.TABLE_NOTES,
                null, contentValues );

        //Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        mRowsUpdated = database.delete(NoteDBOpenHelper.TABLE_NOTES, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return mRowsUpdated;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
         mRowsUpdated = database.update(NoteDBOpenHelper.TABLE_NOTES,
                contentValues, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return mRowsUpdated;
    }
}
