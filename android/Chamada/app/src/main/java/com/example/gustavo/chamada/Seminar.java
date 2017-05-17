package com.example.gustavo.chamada;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to store a seminar
 */

class Seminar {
    private String name;
    private int id;
    private String passcode;

    Seminar(String id, String name) {
        this.name = name;
        this.id = Integer.parseInt(id);
    }

    Seminar(int id, String name) {
        this.id = id;
        this.name = name;
    }

    Seminar(JSONObject obj) throws JSONException {
        try {
            JSONObject data = obj.getJSONObject("data");
            this.name = data.getString("name");
            this.id = Integer.parseInt(data.getString("id"));
            this.passcode = data.getString("data");
        }
        catch (Exception e) {
            throw e;
        }
    }

    void setName(String name) {
        this.name = name;
    }

    void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    String getName() {
        return name;
    }

    String getId() {
        return "" + id;
    }

    String getPasscode() {
        return passcode;
    }
}
