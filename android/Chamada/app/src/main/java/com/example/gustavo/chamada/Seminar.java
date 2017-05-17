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


    Seminar(String id) {
        this.id = Integer.parseInt(id);
    }


    Seminar(JSONObject obj) throws JSONException {
        try {
            this.name = obj.getString("name");
            this.id = Integer.parseInt(obj.getString("id"));
            this.passcode = obj.getString("data");
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
