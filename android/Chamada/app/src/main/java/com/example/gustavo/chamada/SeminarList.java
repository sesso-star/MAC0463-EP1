package com.example.gustavo.chamada;

import java.sql.Array;
import java.util.ArrayList;

/**
 * Created by gustavo on 5/10/17.
 */

public class SeminarList {
    public static ArrayList<Seminar> seminarArrayList;

    public static void addSeminar(Seminar s) {
        seminarArrayList.add(s);
    }

    public static void removeSeminar(Seminar s) {
        seminarArrayList.remove(s);
    }

    public static Seminar[] getSeminarArray() {
        return (Seminar[]) seminarArrayList.toArray();
    }
}
