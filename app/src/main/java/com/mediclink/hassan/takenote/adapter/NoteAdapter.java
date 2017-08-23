package com.mediclink.hassan.takenote.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mediclink.hassan.takenote.R;
import com.mediclink.hassan.takenote.activities.EditorActivity;
import com.mediclink.hassan.takenote.data.NoteDBOpenHelper;
import com.mediclink.hassan.takenote.data.NoteProvider;

/**
 * Created by hassan on 8/14/2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;

    public  NoteAdapter(Context context){ this.mContext = context;}


    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.note_list_item, parent, false);

        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteHolder holder, final int position) {

        try {

            String noteText = mCursor.getString(
                    mCursor.getColumnIndex(NoteDBOpenHelper.NOTE_TEXT));

//            long date = mCursor.getLong(
//                    mCursor.getColumnIndex(NoteDBOpenHelper.NOTE_CREATED));
//
//            int pos = noteText.indexOf(10);
//            if (pos != -1) {
//                noteText = noteText.substring(0, pos) + " ...";
//            }

            holder.tvNote.setText(noteText);
          //  holder.tvDate.setText((int) date);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EditorActivity.class);
                    Uri uri = Uri.parse(NoteProvider.CONTENT_URI + "/" + position);
                    intent.putExtra(NoteProvider.CONTENT_ITEM_TYPE, uri);
                    mContext.startActivity(intent);
                }
            });
        }catch (IndexOutOfBoundsException e){
            e.getMessage();
        }
    }


    @Override
    public int getItemCount() {

        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount()+1;
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


//    @Override
//    public void onClick(View view) {
//        Intent intent = new Intent(this, EditorActivity.class);
//        Uri uri = Uri.parse(NoteProvider.CONTENT_URI + "/" + id);
//        intent.putExtra(NoteProvider.CONTENT_ITEM_TYPE, uri);
//        startActivityForResult(intent, MainActivity.EDITOR_REQUEST_CODE);
//
//    }

    public class NoteHolder extends RecyclerView.ViewHolder {

        ImageView docimageIcon;
        TextView tvNote;
        TextView tvDate;
        public NoteHolder(View itemView) {
            super(itemView);

            docimageIcon = (ImageView) itemView.findViewById(R.id.imageDocIcon);
            tvNote = (TextView) itemView.findViewById(R.id.tvNote);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }
}
