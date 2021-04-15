package com.example.notesapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchActivity extends AppCompatActivity {
    static private int SEARCH_EDIT_REQ_CODE = 4;
    private ListView listView;
    private List<Note> noteList;
    private List<Note> searchRes;
    private SearchView searchView;
    private CustomAdapter adapter;
    private Intent intent;
    private ImageView hideItemBtn;
    private boolean hideItemBtnStatus = true;               // true = not clicked, false = clicked
    private ImageView deleteItemBtn;
    private boolean deleteItemBtnStatus = true;             // true = not clicked, false = clicked

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchActi_searchBox);
        searchView.setIconifiedByDefault(false);
        hideItemBtn = findViewById(R.id.toolBar_searchMenu_hideItemBtn);
        deleteItemBtn = findViewById(R.id.toolBar_searchMenu_deleteItemBtn);

        Toolbar toolbar = findViewById(R.id.toolBar_searchMenu);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.resultList);
        loadData();



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchRes.clear();
                adapter.notifyDataSetChanged();
                if (!s.isEmpty()){
                    if (Character.compare(s.charAt(0), '#') == 0){               // Search by tag4
                        searchByTag(s);
                    }
                    else{                                                       //Search by content
                        searchAll(s);
                    }
                }
                return false;
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int selected_item = position;

                new AlertDialog.Builder(SearchActivity.this).
                        setIcon(android.R.drawable.ic_delete)
                        .setTitle("Delete Note with title: " + noteList.get(position).getTitle() + "?")
                        .setMessage("The deleted note can not be recovered. Process?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Note note = searchRes.get(position);
                                Iterator<Note> iter = noteList.listIterator();
                                while(iter.hasNext()){
                                    Note temp = iter.next();
                                    if(temp == note){
                                        iter.remove();
                                        break;
                                    }
                                }
                                searchRes.remove(selected_item);
                                adapter.notifyDataSetChanged();
                                saveData();
                            }
                        })
                        .setNegativeButton("No" , null).show();
                return true;
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (hideItemBtnStatus && deleteItemBtnStatus) {
                    Intent intent = new Intent(getBaseContext(), MainActivity2.class);
                    intent.putExtra("NOTE", searchRes.get(position).toJson() + "|" + position + "");
                    intent.putExtra("REQ_CODE", SEARCH_EDIT_REQ_CODE);
                    startActivityForResult(intent, SEARCH_EDIT_REQ_CODE);
                }
                else if (hideItemBtnStatus == false){
                    Note note = searchRes.get(position);
                    ListIterator<Note> iter = noteList.listIterator();
                    System.out.println(note.toJson());
                    while(iter.hasNext()){
                        Note temp = iter.next();
                        System.out.println(temp.toJson());
                        if(note.getTitle().equals(temp.getTitle()) && note.getTag().equals(temp.getTag()) && note.getContent().equals(temp.getContent())){
                            iter.set(note);
                            break;
                        }
                    }
                    ((CustomAdapter)(parent.getAdapter())).setHiddenRow(position);
                    adapter.notifyDataSetChanged();
                    saveData();
                }
                else if(deleteItemBtnStatus == false){
                    Note note = searchRes.get(position);
                    Iterator<Note> iter = noteList.listIterator();
                    while(iter.hasNext()){
                        Note temp = iter.next();
                        if(temp == note){
                            iter.remove();
                            break;
                        }
                    }
                    searchRes.remove(position);
                    adapter.notifyDataSetChanged();
                    saveData();
                }
            }
        });


        hideItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hideItemBtnStatus == true){
                    hideItemBtnStatus = false;
                    deleteItemBtn.setVisibility(View.INVISIBLE);
                    hideItemBtn.setImageResource(R.drawable.ic_baseline_auto_fix_high_24_gray);
                }
                else{
                    hideItemBtnStatus = true;
                    deleteItemBtn.setVisibility(View.VISIBLE);
                    hideItemBtn.setImageResource(R.drawable.ic_baseline_auto_fix_high_24_green);
                }
            }
        });

        deleteItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteItemBtnStatus == true){
                    deleteItemBtnStatus = false;
                    hideItemBtn.setVisibility(View.INVISIBLE);
                    deleteItemBtn.setImageResource(R.drawable.ic_baseline_delete_24_gray);
                }
                else{
                    Log.d("Delete  btn", "clicked");
                    deleteItemBtnStatus = true;
                    hideItemBtn.setVisibility(View.VISIBLE);
                    deleteItemBtn.setImageResource(R.drawable.ic_baseline_delete_24);
                }
            }
        });
    }


    private void loadData(){
        intent = getIntent();
        String note_data = intent.getStringExtra("NOTE_DATA");
        try{
            ObjectMapper mapper = new ObjectMapper();
            noteList = mapper.readValue(note_data, new TypeReference<List<Note>>(){});
            System.out.println(noteList.toString());
        }
        catch (JsonProcessingException e){
            e.printStackTrace();
        }

        if(noteList == null || noteList.size() == 0){
            Log.d("CHECK", "check");
            new AlertDialog.Builder(SearchActivity.this).setIcon(android.R.drawable.ic_delete)
                    .setTitle("No note found")
                    .setMessage("Returning to main page")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            setResult(Activity.RESULT_OK, intent);
                            SearchActivity.this.finish();
                        }
                    }).show();
        }
        else{
            searchRes = new ArrayList<Note>();
            adapter = new CustomAdapter(this, R.layout.custom_row, searchRes);
            listView.setAdapter(adapter);
        }

    }


    private void searchByTag(String keyword){
        String str = keyword.replaceAll("[^a-zA-Z0-9]", "");
        String[] searchKey = str.split("\\s+");

        for (int i = 0; i < noteList.size(); i++){
            Note note = noteList.get(i);
            List tags = Arrays.asList(note.getTag().toLowerCase().replaceAll("[^a-zA-Z0-9]", " ").split("\\s+"));
            for(String temp : searchKey){
                if (tags.contains(temp)){
                    if(!searchRes.contains(note)){
                        searchRes.add(note);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }


    private void searchAll(String keyword){
        String str = keyword.toLowerCase().replaceAll("[^\\w]", "");
        String[] searchKey = str.split("\\s+");

        for (int i = 0; i < noteList.size(); i++){
            Note note = noteList.get(i);
            List<String> combinedList = (List<String>) Stream.of(Arrays.asList(note.getTag().toLowerCase().replaceAll("[^\\w]", " ").split("\\s+")),
                    Arrays.asList(note.getTitle().toLowerCase().replaceAll("[^\\w]", " ").split("\\s+")),
                    Arrays.asList(note.getContent().toLowerCase().replaceAll("\\<.*?\\>", "").replaceAll("[^\\w]", " ").split("\\s+")))
                    .flatMap(x -> x.stream())
                    .distinct()
                    .collect(Collectors.toList());
            System.out.println(combinedList);
            for(String temp : searchKey){
                if (combinedList.contains(temp) && !temp.equals("")){
                    if(!searchRes.contains(note)){
                        searchRes.add(note);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK){
                if (requestCode == SEARCH_EDIT_REQ_CODE){
                    String myResult = data.getStringExtra("RES");
                    String[] receivedData = myResult.split("\\|");
                    if(receivedData.length == 2){
                        int pos = Integer.parseInt(receivedData[1]);
                        Note note = searchRes.get(pos);
                        searchRes.set(pos, new Note(receivedData[0]));
                        ListIterator<Note> iter = noteList.listIterator();
                        while(iter.hasNext()){
                            Note temp = iter.next();
                            if(note == temp){
                                iter.set(new Note(receivedData[0]));
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                        this.saveData();
                    }
                }
            }
            else if(resultCode == Activity.RESULT_CANCELED){
                int pos = Integer.parseInt(data.getStringExtra("RES"));
                Note note = searchRes.get(pos);
                Iterator<Note> iter = noteList.listIterator();
                while(iter.hasNext()){
                    Note temp = iter.next();
                    if(temp == note){
                        iter.remove();
                        System.out.println("Removed");
                        break;
                    }
                }
                searchRes.remove(pos);
                adapter.notifyDataSetChanged();
                this.saveData();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveData(){
        String json = new Gson().toJson(noteList);
        SharedPreferences sharedPref = this.getSharedPreferences("NOTE_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("data", json);
        editor.commit();
    }


    @Override
    public void onBackPressed() {
        String json = new Gson().toJson(noteList);
        Log.d("SEARCH - RES", json);
        intent.putExtra("NOTE_DATA", json);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}