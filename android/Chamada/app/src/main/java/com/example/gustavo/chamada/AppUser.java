package com.example.gustavo.chamada;

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