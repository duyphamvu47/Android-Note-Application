package com.example.notesapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.chinalwb.are.AREditText;
import com.chinalwb.are.styles.toolbar.IARE_Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity2 extends AppCompatActivity{
    private EditText title_text, lastEdit_text, tag_text;
    static private int REQ_CODE = 1;
    private int pos = 0;
    private Intent intent;
    private IARE_Toolbar mToolbar;
    private AREditText content_text;
    private boolean scrollerAtEnd;
    private customEditText customEditText;
    private ImageView deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        title_text = findViewById(R.id.detail_title_text);
        lastEdit_text = findViewById(R.id.detail_lastEdit_text);
        tag_text = findViewById(R.id.detail_tag_text);


        content_text = findViewById(R.id.detail_content_text);
        mToolbar = findViewById(R.id.areToolbar);
        customEditText = new customEditText(mToolbar, content_text);

        intent = getIntent();

        if (intent.getIntExtra("REQ_CODE", 0) == 1 || intent.getIntExtra("REQ_CODE", 0) == 4){
            deleteBtn = findViewById(R.id.toolBar_detail_deleteBtn);
            deleteBtn.setVisibility(View.VISIBLE);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(MainActivity2.this).
                            setIcon(android.R.drawable.ic_delete)
                            .setTitle("You sure you want to do this?")
                            .setMessage("The deleted note can not be recovered. Proceed?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    intent.putExtra("RES", pos + "");
                                    setResult(Activity.RESULT_CANCELED, intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("No" , null).show();
                }
            });
        }

        String received = intent.getStringExtra("NOTE");
        String[] data = received.split("\\|");
        pos = Integer.parseInt(data[1]);
        Log.d("DETAIL - RECEIVE", received);

        try {
            JSONObject json = new JSONObject(data[0]);
            title_text.setText(json.getString("title"));
            lastEdit_text.setText(this.getLocalDateString());
            tag_text.setText(json.getString("tag"));
            content_text.fromHtml(json.getString("content"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void response(){
        Note note = new Note(title_text.getText().toString(), lastEdit_text.getText().toString(), tag_text.getText().toString(), content_text.getHtml());
        Log.d("DETAIL - RES", note.toJson());
        intent.putExtra("RES", note.toJson() + "|" + pos + "");
        setResult(Activity.RESULT_OK, intent);
    }

    private String getLocalDateString(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return LocalDateTime.now().format(dateTimeFormatter);
    }




    @Override
    public void onBackPressed() {
        response();
        finish();
    }

}