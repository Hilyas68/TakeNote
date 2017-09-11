package com.mediclink.hassan.takenote.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.mediclink.hassan.takenote.data.NoteContract.NotesEntry.NOTE_CREATED;
import static com.mediclink.hassan.takenote.data.NoteContract.NotesEntry.NOTE_ID;
import static com.mediclink.hassan.takenote.data.NoteContract.NotesEntry.NOTE_TEXT;
import static com.mediclink.hassan.takenote.data.NoteContract.NotesEntry.TABLE_NOTES;

/**
 * Created by hassan on 8/14/2017.
 */

public class NoteDBOpenHelper extends SQLiteOpenHelper  {

    //Constants for db name and version
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_TEXT + " TEXT, " +
                    NOTE_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    public NoteDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(sqLiteDatabase);
    }
}

