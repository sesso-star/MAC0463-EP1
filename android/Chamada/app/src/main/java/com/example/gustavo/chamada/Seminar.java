package com.example.gustavo.chamada;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to store a seminar
 */

class Seminar {
    private String name;
    private int id;

    Seminar(String id, String name) {
        this.name = name;
        this.id = Integer.parseInt(id);
    }

    Seminar(int id, String name) {
        this.id = id;
        this.name = name;
    }

    Seminar(JSONObject obj) throws JSONException {
        String name = "";
        String id = "";
        try {
            JSONObject data = obj.getJSONObject("data");
            name = data.getString("name");
            id = data.getString("id");
            this.name = name;
            this.id = Integer.parseInt(id);
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
}
