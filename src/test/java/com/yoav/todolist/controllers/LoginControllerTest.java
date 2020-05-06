package com.yoav.todolist.controllers;

import com.yoav.todolist.dao.IAccountDao;
import com.yoav.todolist.models.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@Transactional
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("accountRepository")
    private IAccountDao accountDao;

    @Test
    public void getLoginTest_whenIHaveCookie() throws Exception {
        Cookie cookie = new Cookie("username", "admin");
        // cookie.setSecure(true); if the protocol HTTPS is used so uncomment this line
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setPath("/"); // global cookie accessible every where

        mockMvc.
                perform(get("/login").cookie(cookie)).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    public void getLoginTest_whenDoNotIHaveCookie() throws Exception {
        mockMvc.
                perform(get("/login")).
                andExpect(status().isOk()).
                andExpect(view().name("login/index"));
    }

    @Test
    public void postLoginTest_whenCredentialAreCorrect() throws Exception {
        accountDao.add(new Account("yoav", "abu"));
        mockMvc.
                perform(post("/login").
                        param("username", "yoav").
                        param("password", "abu")).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    public void postLoginTest_whenTheInformationTypedInAreIncorrect() throws Exception {
        mockMvc.
                perform(post("/login").
                        param("username", "notExistUsername").
                        param("password", "notExistPassword")).
                andExpect(status().isOk()).
                andExpect(model().attribute("alert", "not the correct username or/and password")).
                andExpect(view().name("login/index"));
    }

    @Test
    public void postLoginWithRememberMeTest_whenCredentialAreCorrect() throws Exception {
        accountDao.add(new Account("uuuu", "pppp"));
        mockMvc.
                perform(post("/login").
                        param("username", "uuuu").
                        param("password", "pppp").
                        param("rememberMe", (String) null)).
                andExpect(status().is3xxRedirection()).
                andExpect(result -> assertThat(result.getResponse().getCookies()).hasSizeGreaterThan(0)).
                andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    public void postLoginWithRememberMeTest_whenTheInformationTypedInAreIncorrect() throws Exception {
        mockMvc.
                perform(post("/login").
                        param("username", "notExistUsername").
                        param("password", "notExistPassword").
                        param("rememberMe", (String) null)).
                andExpect(status().isOk()).
                andExpect(model().attribute("alert", "not the correct username or/and password")).
                andExpect(view().name("login/index"));
    }

}