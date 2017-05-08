package com.example.gustavo.chamada;
import android.content.Context;

/* This class is used to define a singleton containing the user of the application */
public class AppUser {

    private static User currentUser;

    public static User getCurrentUser () {
        if (currentUser == null)
            currentUser = new User ();
        return currentUser;
    }


    public static void setUser (User usr) {
        currentUser = usr;
    }
}