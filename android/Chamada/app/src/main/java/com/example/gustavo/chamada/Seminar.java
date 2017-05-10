package com.example.gustavo.chamada;

import org.json.JSONObject;

/**
 * Class used to store a seminar
 */

public class Seminar {
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

    Seminar(JSONObject o) {
        /* TODO: implement this constructor */
        return;
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
