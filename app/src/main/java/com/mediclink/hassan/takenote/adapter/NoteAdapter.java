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
import android.widget.Toast;

import com.mediclink.hassan.takenote.R;
import com.mediclink.hassan.takenote.activities.EditorActivity;
import com.mediclink.hassan.takenote.data.NoteProvider;

import static android.R.attr.id;
import static com.mediclink.hassan.takenote.activities.MainActivity.EDITOR_REQUEST_CODE;
import static com.mediclink.hassan.takenote.data.NoteContract.NOTES_CONTENT_URI;
import static com.mediclink.hassan.takenote.data.NoteContract.NotesEntry.NOTE_CREATED;
import static com.mediclink.hassan.takenote.data.NoteContract.NotesEntry.NOTE_TEXT;

/**
 * Created by hassan on 8/14/2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;
    private  RecyclerViewClickListener itemListener;

    public NoteAdapter(Context mContext, RecyclerViewClickListener itemListener) {
        this.mContext = mContext;
        this.itemListener = itemListener;
    }

    public interface RecyclerViewClickListener
    {

        void recyclerViewListClicked(View v, int position);
    }




    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.note_list_item, parent, false);

        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteHolder holder, final int position) {

//        try {
            mCursor.moveToPosition(position);
            String noteText = mCursor.getString(
                    mCursor.getColumnIndex(NOTE_TEXT));

            String date = mCursor.getString(
                    mCursor.getColumnIndex(NOTE_CREATED));

            int pos = noteText.indexOf(10);
            if (pos != -1) {
                noteText = noteText.substring(0, pos) + " ...";
            }

            holder.tvNote.setText(noteText);
            holder.tvDate.setText(date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditorActivity.class);
                Uri uri = Uri.parse(NOTES_CONTENT_URI + "/" + position);
                intent.putExtra(NoteProvider.CONTENT_ITEM_TYPE, uri);
               mContext.startActivity(intent);
                Toast.makeText(mContext,uri.toString() , Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {

        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public void swapCursor (Cursor c){
        if(c != null){
            mCursor = c;

            notifyDataSetChanged();
        }

    }


    public class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView docimageIcon;
        TextView tvNote;
        TextView tvDate;
        public NoteHolder(View itemView) {
            super(itemView);

            docimageIcon = (ImageView) itemView.findViewById(R.id.imageDocIcon);
            tvNote = (TextView) itemView.findViewById(R.id.tvNote);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
           itemView.setOnClickListener(this);

//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(mContext, EditorActivity.class);
//            Uri uri = Uri.parse(NoteProvider.CONTENT_URI + "/" + getAdapterPosition());
//            intent.putExtra(NoteProvider.CONTENT_ITEM_TYPE, uri);
//            mContext.startActivity(intent);
//                }

        }

       @Override
        public void onClick(View view) {
            itemListener.recyclerViewListClicked(view, getAdapterPosition());

        }

    }
}
