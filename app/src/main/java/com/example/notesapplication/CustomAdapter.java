package com.example.notesapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<Note> listNote;
    private int idLayout;
    public CustomAdapter( Context context, int layoutToBeInflated,
                                   List<Note> listNote) {
        this.context = context;
        this.listNote = listNote;
        this.idLayout = layoutToBeInflated;
    }

    @Override
    public int getCount() {
        if (listNote != null){
            return listNote.size();
        }
        else{
            listNote = new ArrayList<Note>();
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        Note note = listNote.get(position);

        if(note.getHiddenStatus() == true){
            View row = inflater.inflate(R.layout.hidden_row, null);
            return (row);
        }
        else{
            View row = inflater.inflate(R.layout.custom_row, null);

            TextView noteTile = (TextView) row.findViewById(R.id.title);
            noteTile.setText(note.getTitle());

            TextView noteLastEdit = (TextView) row.findViewById(R.id.last_edit);
            noteLastEdit.setText(note.getLast_edit());

            TextView noteTag = (TextView) row.findViewById(R.id.tag);
            noteTag.setText(note.getTag());
            return (row);
        }
    }

    public void unHiddenAllItems(){
        for (Note note : listNote){
            if (note.getHiddenStatus())
                note.changeHiddenStatus();
        }
        this.notifyDataSetChanged();
    }

    public void setHiddenRow(int pos){
        Note note = listNote.get(pos);
        if(note.getHiddenStatus() == false){
            note.changeHiddenStatus();
            listNote.set(pos, note);
            this.notifyDataSetChanged();
        }
    }


}
