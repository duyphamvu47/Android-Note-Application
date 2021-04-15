package com.example.notesapplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {
    private String title;
    private String last_edit;
    private String tag;
    private String content;
    private boolean hiddenStatus = false;

    public Note(String title, String last_edit, String tag, String content){
        this.title = title;
        this.last_edit = last_edit;
        this.tag = tag;
        this.content = content;
        this.hiddenStatus = false;
    }

    public Note(){
        this.title = "Title";
        this.last_edit = this.getLocalDateString();
        this.tag = "Tag";
        this.content = "Content";
    }

    public Note(String jsonStr) throws JSONException {
        JSONObject json = new JSONObject(jsonStr);
        this.title = json.getString("title");
        this.last_edit = json.getString("last_edit");
        this.tag = json.getString("tag");
        this.content = json.getString("content");
        this.hiddenStatus = json.getBoolean("hiddenStatus");
    }

    public String getTitle(){
        return this.title;
    }

    public String getLast_edit(){
        return this.last_edit;
    }

    public String getTag(){
        return this.tag;
    }

    public String getContent(){ return this.content;}

    public void setTitle(String title){
        this.title = title;
    }

    public void setLast_edit(String last_edit){
        this.last_edit = last_edit;
    }

    public void setTag(String tag){
        this.tag = tag;
    }

    public void setContent(String content) { this.content = content;}

    public String toJson(){
        String json = "";
        try{
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            json = ow.writeValueAsString(this);

        }
        catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return json;
    }

    private String getLocalDateString(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return LocalDateTime.now().format(dateTimeFormatter);
    }


    public void changeHiddenStatus(){
        this.hiddenStatus = !this.hiddenStatus;
    }
    public boolean getHiddenStatus(){
        return this.hiddenStatus;
    }

}
