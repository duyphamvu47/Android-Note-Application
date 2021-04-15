package com.example.notesapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    static private int EDIT_REQ_CODE = 1;
    static private int NEW_REQ_CODE = 2;
    static private int SEARCH_REQ_CODE = 3;
    private CustomAdapter adapter;
    private ListView listView;
    private List<Note> noteList;
    private ImageView addBtn;
    private ImageView searchBtn;
    private ImageView unHideItemBtn;
    private ImageView hideItemBtn;
    private TextView numNote;
    private boolean hideItemBtnStatus = true;               // true = not clicked, false = clicked
    private ImageView deleteItemBtn;
    private boolean deleteItemBtnStatus = true;             // true = not clicked, false = clicked

    String[] titles = { "Data-1", "Data-2", "Data-3", "Data-4", "Data-5",
            "Data-6", "Data-7", "Data-8", "Data-9", "Data-10", "Data-11",
            "Data-12", "Data-13", "Data-14", "Data-15" };

    String[] last_edit = { "Data-1", "Data-2", "Data-3", "Data-4", "Data-5",
            "Data-6", "Data-7", "Data-8", "Data-9", "Data-10", "Data-11",
            "Data-12", "Data-13", "Data-14", "Data-15" };

    String[] tags = { "Data-1", "Data-2", "Data-3", "Data-4", "Data-5",
            "Data-6", "Data-7", "Data-8", "Data-9", "Data-10", "Data-11",
            "Data-12", "Data-13", "Data-14", "Data-15" };

    String[] contents = {"Lorem ipsum dolor sit amet, consectetur adipisicing elit, " +
            "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
            "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.list);
        addBtn = findViewById(R.id.toolBar_mainMenu_addBtn);
        hideItemBtn = findViewById(R.id.toolBar_mainMenu_hideItemBtn);
        unHideItemBtn = findViewById(R.id.toolBar_mainMenu_unHideItemBtn);
        unHideItemBtn.setVisibility(View.INVISIBLE);
        deleteItemBtn = findViewById(R.id.toolBar_mainMenu_deleteItemBtn);
        searchBtn = findViewById(R.id.toolBar_mainMenu_searchBtn);
        numNote = findViewById(R.id.numNote);

        loadData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (hideItemBtnStatus && deleteItemBtnStatus) {
                    Intent intent = new Intent(getBaseContext(), MainActivity2.class);
                    intent.putExtra("NOTE", noteList.get(position).toJson() + "|" + position + "");
                    intent.putExtra("REQ_CODE", EDIT_REQ_CODE);
                    startActivityForResult(intent, EDIT_REQ_CODE);
                }
                else if (hideItemBtnStatus == false){
                    ((CustomAdapter)(parent.getAdapter())).setHiddenRow(position);
                }
                else if(deleteItemBtnStatus == false){
                    noteList.remove(position);
                    adapter.notifyDataSetChanged();
                    saveData();
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity2.class);
                intent.putExtra("NOTE", new Note().toJson() + "|0");
                intent.putExtra("REQ_CODE", NEW_REQ_CODE);
                startActivityForResult(intent, NEW_REQ_CODE);
            }
        });


        hideItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hideItemBtnStatus == true){
                    hideItemBtnStatus = false;
                    addBtn.setVisibility(View.INVISIBLE);
                    deleteItemBtn.setVisibility(View.INVISIBLE);
                    searchBtn.setVisibility(View.INVISIBLE);
                    hideItemBtn.setImageResource(R.drawable.ic_baseline_auto_fix_high_24_gray);
                    unHideItemBtn.setVisibility(View.VISIBLE);
                }
                else{
                    hideItemBtnStatus = true;
                    addBtn.setVisibility(View.VISIBLE);
                    deleteItemBtn.setVisibility(View.VISIBLE);
                    searchBtn.setVisibility(View.VISIBLE);
                    unHideItemBtn.setVisibility(View.INVISIBLE);
                    hideItemBtn.setImageResource(R.drawable.ic_baseline_auto_fix_high_24_green);
                    saveData();
                }
            }
        });

        deleteItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteItemBtnStatus == true){
                    deleteItemBtnStatus = false;
                    addBtn.setVisibility(View.INVISIBLE);
                    searchBtn.setVisibility(View.INVISIBLE);
                    hideItemBtn.setVisibility(View.INVISIBLE);
                    deleteItemBtn.setImageResource(R.drawable.ic_baseline_delete_24_gray);
                }
                else{
                    Log.d("Delete  btn", "clicked");
                    deleteItemBtnStatus = true;
                    addBtn.setVisibility(View.VISIBLE);
                    searchBtn.setVisibility(View.VISIBLE);
                    hideItemBtn.setVisibility(View.VISIBLE);
                    deleteItemBtn.setImageResource(R.drawable.ic_baseline_delete_24);
                    saveData();
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                String json = new Gson().toJson(noteList);
                intent.putExtra("NOTE_DATA", json);
                startActivityForResult(intent, SEARCH_REQ_CODE);
            }
        });

        unHideItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.unHiddenAllItems();
                hideItemBtnStatus = true;
                addBtn.setVisibility(View.VISIBLE);
                deleteItemBtn.setVisibility(View.VISIBLE);
                hideItemBtn.setImageResource(R.drawable.ic_baseline_auto_fix_high_24_green);
                searchBtn.setVisibility(View.VISIBLE);
                unHideItemBtn.setVisibility(View.INVISIBLE);
                saveData();
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int selected_item = position;

                new AlertDialog.Builder(MainActivity.this).
                        setIcon(android.R.drawable.ic_delete)
                        .setTitle("Delete Note with title: " + noteList.get(position).getTitle() + "?")
                        .setMessage("The deleted note can not be recovered. Process?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                noteList.remove(selected_item);
                                adapter.notifyDataSetChanged();
                                saveData();
                            }
                        })
                        .setNegativeButton("No" , null).show();
                return true;
            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK){
                if (requestCode == EDIT_REQ_CODE){
                    String myResult = data.getStringExtra("RES");
                    String[] receivedData = myResult.split("\\|");
                    if(receivedData.length == 2){
                        int temp = Integer.parseInt(receivedData[1]);
                        noteList.set(temp, new Note(receivedData[0]));
                        adapter.notifyDataSetChanged();
                        this.saveData();
                    }
                }
                else if (requestCode == NEW_REQ_CODE){
                    String myResult = data.getStringExtra("RES");
                    String[] receivedData = myResult.split("\\|");
                    if(receivedData.length == 2){
                        noteList.add(new Note(receivedData[0]));
                        adapter.notifyDataSetChanged();
                        this.saveData();
                    }
                }
                else if (requestCode == SEARCH_REQ_CODE){
                    String json = data.getStringExtra("NOTE_DATA");
                    Log.d("SEARCH_RESPONSE", json);
//                    noteList.clear();
//                    noteList = null;
//                    adapter = null;
                    ObjectMapper mapper = new ObjectMapper();
                    noteList = mapper.readValue(json, new TypeReference<List<Note>>(){});
                    saveData();
                    loadData();
//                    adapter = new CustomAdapter(this, R.layout.custom_row, noteList);
//                    listView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
                }
            }
            else if(resultCode == Activity.RESULT_CANCELED){
                if( requestCode == EDIT_REQ_CODE){
                    int pos = Integer.parseInt(data.getStringExtra("RES"));
                    noteList.remove(pos);
                    adapter.notifyDataSetChanged();
                    this.saveData();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLocalDateString(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        simpleDateFormat.format(date);
        return date.toString();
    }



    private void loadData(){
        try{
            SharedPreferences sharedPref = this.getSharedPreferences("NOTE_DATA", Context.MODE_PRIVATE);
            String data = sharedPref.getString("data", "");
            Log.d("DATA", data);
            ObjectMapper mapper = new ObjectMapper();
            noteList = mapper.readValue(data, new TypeReference<List<Note>>(){});
        }
        catch (JsonProcessingException e){
            e.printStackTrace();
            System.out.println("ERROR");
        }

        if(noteList == null){
            noteList = new ArrayList<Note>();
        }
        adapter = new CustomAdapter(this, R.layout.custom_row, noteList);
        listView.setAdapter(adapter);
        numNote.setText(this.noteList.size() + " note(s)");
    }



    private void saveData(){
        String json = new Gson().toJson(noteList);
        SharedPreferences sharedPref = this.getSharedPreferences("NOTE_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("data", json);
        editor.commit();
        numNote.setText(this.noteList.size() + " note(s)");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION - READ", "GRANTED");
                } else {
                    Log.d("PERMISSION - READ", "not GRANTED");
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                return;
            }
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION - WRITE", "GRANTED");
                } else {
                    Log.d("PERMISSION - WRITE", "not GRANTED");
                    Toast.makeText(MainActivity.this, "Permission denied to write your External storage", Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                return;
            }
        }
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        this.saveData();
    }
}
