package com.example.gustavo.chamada;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
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
}
