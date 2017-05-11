package com.example.gustavo.chamada;
import java.util.ArrayList;

/**
 * This class stores every seminar that was fetched from the server. It is static because it should
 * be the same for every activity
 */
public class SeminarList {
    public static ArrayList<Seminar> seminarArrayList = new ArrayList<>();

    public static void addSeminar(Seminar s) {
        seminarArrayList.add(s);
    }

    public static void removeSeminar(Seminar s) {
        seminarArrayList.remove(s);
    }

    public static Seminar[] getSeminarArray() {
        return seminarArrayList.toArray(new Seminar[seminarArrayList.size()]);
    }
}
