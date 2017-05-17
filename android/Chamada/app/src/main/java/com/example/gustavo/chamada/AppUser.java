package com.example.gustavo.chamada;


import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/* This class is used to define a singleton containing the user of the application */
class AppUser {

    private static User currentUser;

    static void logIn(User usr) {
        currentUser = usr;
    }

    static void logOut() {
        currentUser = null;
    }

    static User getCurrentUser() {
        return currentUser;
    }

    static boolean hasLoggedInUser() {
        return currentUser != null;
    }
}
