package com.example.gustavo.chamada;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

public class TestUser {
    @Test
    public void AppUser_shouldLogin() throws Exception {
        String name = "abc";
        String nusp = "8888888";
        boolean isProfessor = true;
        User u = new User(name, nusp, isProfessor);
        AppUser.logIn(u);
        assertThat(AppUser.getCurrentUser(), is(u));
    }
}
