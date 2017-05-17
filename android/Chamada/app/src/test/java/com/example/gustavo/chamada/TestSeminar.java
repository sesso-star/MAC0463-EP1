package com.example.gustavo.chamada;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestSeminar {

    @Test
    public void Seminar_shouldBeCreatedWithJSON() throws Exception {
        JSONObject obj = new JSONObject();
        String id = "123";
        String name = "seminar name";
        String pass = "seminar passcode";
        obj.put("id", id);
        obj.put("name", name);
        obj.put("data", pass);
        Seminar s = new Seminar(obj);
        assertThat(s.getId(), is(id));
        assertThat(s.getName(), is(name));
        assertThat(s.getPasscode(), is(pass));
    }

    @Test
    public void SeminarList_shouldCreateArrayofSeminars() throws Exception {
        JSONArray jsonArray = new JSONArray();
        int n = 20;
        for (int i = 0; i < n; i++) {
            JSONObject obj = new JSONObject();
            obj.put("id", "" + i);
            obj.put("name", "a name");
            obj.put("data", "a passcode");
            jsonArray.put(obj);
        }
        JSONObject serverResponseJSON = new JSONObject();
        String arrayString = jsonArray.toString(0);
        serverResponseJSON.put("data", arrayString.replace("\\\"", "\""));
        SeminarList seminarList = new SeminarList(serverResponseJSON);
        assertThat(seminarList.getSeminarArray().length, is(n));
    }
}
