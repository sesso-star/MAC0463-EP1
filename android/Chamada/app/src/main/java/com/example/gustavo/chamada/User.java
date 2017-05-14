package com.example.gustavo.chamada;

/* Defines the app User */
class User {
    private String name;
    private String nusp;
    private boolean isProfessor;


    public User() {
        name = null;
        nusp = null;
        isProfessor = false;
    }


    User(String name, String nusp, boolean isProfessor) {
        this.name = name;
        this.nusp = nusp;
        this.isProfessor = isProfessor;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return this.name;
    }


    public void setNusp(String nusp) {
        this.nusp = nusp;
    }

    String getNusp() {
        return this.nusp;
    }

    boolean isProfessor() {
        return this.isProfessor;
    }

    String getUserType() {
        if (isProfessor())
            return "teacher";
        else
            return "student";
    }
}
