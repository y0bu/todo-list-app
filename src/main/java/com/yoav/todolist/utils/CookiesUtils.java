package com.yoav.todolist.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookiesUtils {

    public static void deleteCookie(Cookie cookie, HttpServletResponse response) {
        cookie.setMaxAge(0);
        cookie.setValue(null);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static void addCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        // cookie.setSecure(true); if the protocol HTTPS is used so uncomment this line
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setPath("/"); // global cookie accessible every where
        response.addCookie(cookie);
    }

}
