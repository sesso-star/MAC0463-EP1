package com.example.gustavo.chamada;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class TestUser {

    @Test
    public void User_shouldHaveUserType() throws Exception {
        User u = new User("", "", true);
        assertThat(u.getUserType(), is("teacher"));

        u = new User("", "", false);
        assertThat(u.getUserType(), is("student"));
    }

    @Test
    public void AppUser_shouldLogin() throws Exception {
        String name = "abc";
        String nusp = "8888888";
        User u = new User(name, nusp, true);
        AppUser.logIn(u);
        assertThat(AppUser.getCurrentUser(), is(u));
    }

    @Test
    public void AppUser_shouldLogout() throws Exception {
        String name = "abc";
        String nusp = "8888888";
        User u = new User(name, nusp, true);
        AppUser.logOut();
        assertThat(AppUser.getCurrentUser(), is(nullValue(User.class)));
    }

    @Test
    public void AppUser_shouldKnowIfAnyLoggedinUser() throws Exception {
        assertThat(AppUser.hasLoggedInUser(), is(false));
        User u = new User("", "", true);
        AppUser.logIn(u);
        assertThat(AppUser.hasLoggedInUser(), is(true));
    }


    @Test
    public void AttendanceList_shouldCreateArrayOfStudents () throws Exception {
        JSONArray jsonArray = new JSONArray();
        int n = 20;
        for (int i = 0; i < 20; i++) {
            JSONObject obj = new JSONObject();
            obj.put("student_nusp", "" + i);
            jsonArray.put(obj);
        }
        JSONObject serverResponseJSON = new JSONObject();
        String arrayString = jsonArray.toString(0);
        serverResponseJSON.put("data", arrayString.replace("\\\"", "\""));
        AttendanceList attendanceList = new AttendanceList(serverResponseJSON);
        assertThat(attendanceList.getStudentArray().length, is(n));
    }
}
