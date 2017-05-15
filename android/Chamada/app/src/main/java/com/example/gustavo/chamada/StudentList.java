package com.example.gustavo.chamada;

/**
 * Created by gustavo on 5/15/17.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class stores every seminar that was fetched from the server. It is static because it should
 * be the same for every activity
 */
class StudentList {

    private ArrayList<Seminar> studentArrayList;

    StudentList() {
        studentArrayList = new ArrayList<>();
    }


    StudentList(JSONObject seminarArrrayJSON) throws JSONException {
        studentArrayList = new ArrayList<>();
        try {
            JSONArray seminarJArray = new JSONArray(seminarArrrayJSON.getString("data"));
            for (int i = 0; i < seminarJArray.length(); i++) {
                JSONObject seminarJObj = seminarJArray.getJSONObject(i);
                String name = seminarJObj.getString("name");
                String id = seminarJObj.getString("id");
                Seminar s = new Seminar(id, name);
                this.addSeminar(s);
            }
        }
        catch (Exception e){
            throw e;
        }
    }


    void addSeminar(Seminar s) {
        studentArrayList.add(s);
    }


    public void removeSeminar(Seminar s) {
        studentArrayList.remove(s);
    }


    public Seminar[] getSeminarArray() {
        return studentArrayList.toArray(new Seminar[studentArrayList.size()]);
    }


    void cleanSeminarList() {
        studentArrayList.clear();
    }
}

