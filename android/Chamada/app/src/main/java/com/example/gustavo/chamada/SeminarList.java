package com.example.gustavo.chamada;
import java.util.ArrayList;

/**
 * This class stores every seminar that was fetched from the server. It is static because it should
 * be the same for every activity
 */
class SeminarList {
    private static ArrayList<Seminar> seminarArrayList = new ArrayList<>();

    static void addSeminar(Seminar s) {
        seminarArrayList.add(s);
    }

    public static void removeSeminar(Seminar s) {
        seminarArrayList.remove(s);
    }

    static Seminar[] getSeminarArray() {
        return seminarArrayList.toArray(new Seminar[seminarArrayList.size()]);
    }

    static void cleanSeminarList() {
        seminarArrayList.clear();
    }
}
