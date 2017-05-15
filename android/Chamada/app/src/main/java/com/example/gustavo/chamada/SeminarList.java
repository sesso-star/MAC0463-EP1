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

    SeminarList() {
        seminarArrayList = new ArrayList<>();
    }


    SeminarList(JSONObject seminarArrrayJSON) throws JSONException {
        seminarArrayList = new ArrayList<>();
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
        seminarArrayList.add(s);
    }


    public void removeSeminar(Seminar s) {
        seminarArrayList.remove(s);
    }


    public Seminar[] getSeminarArray() {
        return seminarArrayList.toArray(new Seminar[seminarArrayList.size()]);
    }


    void cleanSeminarList() {
        seminarArrayList.clear();
    }
}
