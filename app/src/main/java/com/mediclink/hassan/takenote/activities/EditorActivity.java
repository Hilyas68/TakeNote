package com.mediclink.hassan.takenote.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.mediclink.hassan.takenote.R;
import com.mediclink.hassan.takenote.data.NoteProvider;

import static com.mediclink.hassan.takenote.data.NoteContract.NOTES_CONTENT_URI;
import static com.mediclink.hassan.takenote.data.NoteContract.NotesEntry.ALL_COLUMNS;
import static com.mediclink.hassan.takenote.data.NoteContract.NotesEntry.NOTE_ID;
import static com.mediclink.hassan.takenote.data.NoteContract.NotesEntry.NOTE_TEXT;

public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    private String noteFilter;
    private String oldText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editor = (EditText) findViewById(R.id.editNote);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(editor.getText().toString()).isEmpty()) {
                    Snackbar.make(view, "Note Created", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    finishEditing();
                    //finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Enter Note", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NoteProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    ALL_COLUMNS, noteFilter, null, null);

          if(cursor != null && cursor.moveToFirst()) {
                oldText = cursor.getString(cursor.getColumnIndex(NOTE_TEXT));
           }

            editor.setText(oldText);
            editor.requestFocus();

            Toast.makeText(this, noteFilter, Toast.LENGTH_SHORT).show();
            //cursor.close();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!preferences.getBoolean(getString(R.string.first_run), true)){
            rePositioningShowcaseViewBtn();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(getString(R.string.first_run), true);
            editor.apply();
        }
    }

    private void rePositioningShowcaseViewBtn() {
        // Implementation
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
// This aligns button to the bottom left side of screen
        lps.addRule(RelativeLayout.CENTER_IN_PARENT);
        //lps.addRule(RelativeLayout.ALIGN_PARENT_TOP);
// Set margins to the button, we add 16dp margins here
        int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        Target target = new ViewTarget(R.id.fab, this);
         new ShowcaseView.Builder(this, false)
                .setTarget(target)
                .setContentTitle("Save New Notes")
                .setContentText("Input notes then click on the fab button to save...Enjoy")
                .setStyle(1)
                .hideOnTouchOutside()
                .build()
                .setButtonPosition(lps);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.editor_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                //finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
            break;
        }


        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    inserNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deleteNote();
                } else if (oldText.equals(newText)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText);
                }

        }
    }

    private void inserNote(String noteText) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTE_TEXT, noteText);
        getContentResolver().insert(NOTES_CONTENT_URI, contentValues);
        setResult(RESULT_OK);
    }

    private void updateNote(String noteText) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTE_TEXT, noteText);
        getContentResolver().update(NOTES_CONTENT_URI, contentValues, noteFilter, null);
        Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void deleteNote() {
        getContentResolver().delete(NOTES_CONTENT_URI,
                noteFilter, null);
        Toast.makeText(this, getString(R.string.note_deleted),
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();

    }

}