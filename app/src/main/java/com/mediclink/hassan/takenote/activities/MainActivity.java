package com.mediclink.hassan.takenote.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.mediclink.hassan.takenote.R;
import com.mediclink.hassan.takenote.adapter.NoteAdapter;
import com.mediclink.hassan.takenote.data.NoteProvider;

import static android.R.attr.id;
import static com.mediclink.hassan.takenote.data.NoteContract.NOTES_CONTENT_URI;
import static com.mediclink.hassan.takenote.data.NoteContract.NotesEntry.NOTE_TEXT;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, NoteAdapter.RecyclerViewClickListener{

    public static final int EDITOR_REQUEST_CODE = 1001;
    public static final int NOTE_LOADER_ID = 0;

    private RecyclerView recyclerView;
    ShowcaseView.Builder showcaseView;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                openEditorForNewNote(view);
            }
        });

        //ImageView imageView = (ImageView) findViewById(R.id.emptyImageNote);
        //TextView emptyTextView = (TextView) findViewById(R.id.emptyTv);
        recyclerView = (RecyclerView) findViewById(R.id.rv_note);

        //Displaying Grid item using GridLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        noteAdapter = new NoteAdapter(this, this);
        recyclerView.setAdapter(noteAdapter);

//        if(noteAdapter.getItemCount() == 0){
//            recyclerView.setVisibility(View.GONE);
//            // emptyTextView.setVisibility(View.VISIBLE);
//            imageView.setVisibility(View.VISIBLE);
//        }else{
//            recyclerView.setVisibility(View.VISIBLE);
//            imageView.setVisibility(View.GONE);
//            // emptyTextView.setVisibility(View.GONE);
//        }

        getSupportLoaderManager().initLoader(NOTE_LOADER_ID, null, this);

        //Make code run once
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!preferences.getBoolean(getString(R.string.first_run), false)){
            rePositioningShowcaseViewBtn();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(getString(R.string.first_run), false);
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
        showcaseView =  new ShowcaseView.Builder(this, false);
        showcaseView.setTarget(target)
                .setContentTitle("Add New Notes")
                .setContentText("Click on the fab button to add new note.")
                .setStyle(1)
                .hideOnTouchOutside()
                .build()
                .setButtonPosition(lps);

    }


    private void insertNote(String noteText) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTE_TEXT, noteText);
        Uri noteUri = getContentResolver().insert(NOTES_CONTENT_URI, contentValues);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_all) {
            deleteAllNotes();
            return true;
        }

        if (id == R.id.action_create_sample) {
            insertSampleData();
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openEditorForNewNote(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    public void restartLoader() {
        getSupportLoaderManager().restartLoader(NOTE_LOADER_ID, null, this);
    }

    private void deleteAllNotes() {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Insert Data management code here
                            getContentResolver().delete(
                                    NOTES_CONTENT_URI, null, null
                            );
                            restartLoader();

                            Toast.makeText(MainActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void insertSampleData() {
        insertNote("Simple note");
        insertNote("Multi-line\nnote");
        insertNote("Very long note with a lot of text that exceeds the width of the screen");
        restartLoader();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, NOTES_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        noteAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        noteAdapter.swapCursor(null);
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        Intent intent = new Intent(this, EditorActivity.class);
            Uri uri = Uri.parse(NOTES_CONTENT_URI + "/" + position);
            intent.putExtra(NoteProvider.CONTENT_ITEM_TYPE, uri);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
        //Toast.makeText(getApplicationContext(), id + " " + position, Toast.LENGTH_SHORT).show();
    }
}