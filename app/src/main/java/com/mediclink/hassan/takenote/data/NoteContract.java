/*
 * Copyright (c) 2017. Ibanga Enoobong Ime (World class developer and entrepreneur in transit)
 */

package com.mediclink.hassan.takenote.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class NoteContract {
    static final String CONTENT_AUTHORITY = "com.mediclink.hassan.takenote.data.NoteProvider";
    static final String BASE_PATH = "notes";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri NOTES_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(BASE_PATH).build();

    public static Uri buildRecipeUriWithId(int id) {
        return NOTES_CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
    }

    public static final class NotesEntry implements BaseColumns {

        public static final String TABLE_NOTES = "notes";
        public static final String NOTE_ID = "_id";
        public static final String NOTE_TEXT = "noteText";
        public static final String NOTE_CREATED = "noteCreated";

        public static final String[] ALL_COLUMNS =
                {NOTE_ID, NOTE_TEXT, NOTE_CREATED};
    }
}
