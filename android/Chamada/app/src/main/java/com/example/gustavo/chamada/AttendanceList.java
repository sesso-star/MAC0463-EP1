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
class AttendanceList {

    private ArrayList<User> studentArrayList;

    AttendanceList() {
        studentArrayList = new ArrayList<>();
    }


    AttendanceList(JSONObject studentArrrayJSON) throws JSONException {
        studentArrayList = new ArrayList<>();
        try {
            JSONArray studentJArray = new JSONArray(studentArrrayJSON.getString("data"));
            for (int i = 0; i < studentJArray.length(); i++) {
                JSONObject seminarJObj = studentJArray.getJSONObject(i);
                String nusp = seminarJObj.getString("student_nusp");
                User u = new User("", nusp, false);
                this.addStudent(u);
            }
        }
        catch (Exception e){
            if (!studentArrrayJSON.getString("success").equals("true"))
                throw e;
        }
    }


    void addStudent(User u) {
        studentArrayList.add(u);
    }


    public User[] getStudentArray() {
        return studentArrayList.toArray(new User[studentArrayList.size()]);
    }


    void cleanSeminarList() {
        studentArrayList.clear();
    }
}

