package com.example.gustavo.chamada;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class stores every seminar that was fetched from the server. It is static because it should
 * be the same for every activity
 */
class SeminarList {

    private ArrayList<Seminar> seminarArrayList;

    static int DEFAULT_SEMINAR_LIST = 0;
    static int USER_ATTENDANCE_SEMINAR_LIST = 1;


    private void SeminarListDefault(JSONObject seminarArrayJSON) throws JSONException {
        seminarArrayList = new ArrayList<>();
        try {
            JSONArray seminarJArray = new JSONArray(seminarArrayJSON.getString("data"));
            for (int i = 0; i < seminarJArray.length(); i++) {
                JSONObject seminarJObj = seminarJArray.getJSONObject(i);
                Seminar s = new Seminar(seminarJObj);
                this.addSeminar(s);
            }
        }
        catch (Exception e){
            if (!seminarArrayJSON.getString("success").equals("true"))
                throw e;
        }
    }


    private void SeminarListUserAttendance(JSONObject seminarArrayJSON) throws JSONException {
        seminarArrayList = new ArrayList<>();
        try {
            JSONArray seminarJArray = new JSONArray(seminarArrayJSON.getString("data"));
            for (int i = 0; i < seminarJArray.length(); i++) {
                JSONObject seminarJObj = seminarJArray.getJSONObject(i);
                String id = seminarJObj.getString("seminar_id");
                Seminar s = new Seminar(id);
                this.addSeminar(s);
            }
        }
        catch (Exception e){
            if (!seminarArrayJSON.getString("success").equals("true"))
                throw e;
        }
    }


    SeminarList(JSONObject seminarArrayJSON, int jsonType) throws JSONException {
        if (jsonType == DEFAULT_SEMINAR_LIST)
            SeminarListDefault(seminarArrayJSON);
        else
            SeminarListUserAttendance(seminarArrayJSON);
    }


    SeminarList(JSONObject seminarArrayJson) throws JSONException {
        this(seminarArrayJson, DEFAULT_SEMINAR_LIST);
    }

    private void addSeminar(Seminar s) {
        seminarArrayList.add(s);
    }


    public void removeSeminar(Seminar s) {
        seminarArrayList.remove(s);
    }


    Seminar[] getSeminarArray() {
        return seminarArrayList.toArray(new Seminar[seminarArrayList.size()]);
    }


    void cleanSeminarList() {
        seminarArrayList.clear();
    }
}
