package com.example.gustavo.chamada;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/* This class is used to define a singleton containing the user of the application */
class AppUser {

    private static User currentUser;

    public static void logIn(User usr) {
        currentUser = usr;
    }

    public static void logOut() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean hasLoggedInUser() {
        return currentUser != null;
    }

}