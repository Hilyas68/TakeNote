package com.mediclink.hassan.takenote.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.mediclink.hassan.takenote.data.NoteContract.BASE_PATH;
import static com.mediclink.hassan.takenote.data.NoteContract.CONTENT_AUTHORITY;
import static com.mediclink.hassan.takenote.data.NoteContract.NOTES_CONTENT_URI;
import static com.mediclink.hassan.takenote.data.NoteContract.NotesEntry.NOTE_CREATED;
import static com.mediclink.hassan.takenote.data.NoteContract.NotesEntry.TABLE_NOTES;

/**
 * Created by hassan on 8/14/2017.
 */

public class NoteProvider extends ContentProvider {

    // Constant to identify the requested operation
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;

    public static final String CONTENT_ITEM_TYPE = "Note";

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SQLiteDatabase database;
    private NoteDBOpenHelper helper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(CONTENT_AUTHORITY, BASE_PATH + "/#", NOTES_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
         helper = new NoteDBOpenHelper(getContext());
        return true;
    }

    int mRowsUpdated;

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        final SQLiteDatabase db = helper.getReadableDatabase();

        switch (sUriMatcher.match(uri)) {
            case NOTES_ID:
                cursor = db.query(TABLE_NOTES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case NOTES:
                cursor = db.query(TABLE_NOTES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                         NOTE_CREATED + " DESC");
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        Uri returnUri;
        long id;

        switch (sUriMatcher.match(uri)) {
            case NOTES_ID:
                id = db.insert(TABLE_NOTES, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(NOTES_CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case NOTES:
                id = db.insertWithOnConflict(TABLE_NOTES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(NOTES_CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = helper.getWritableDatabase();

            return db.delete(TABLE_NOTES, selection, selectionArgs);
        }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(TABLE_NOTES,
                contentValues, selection, selectionArgs);
    }
    }
