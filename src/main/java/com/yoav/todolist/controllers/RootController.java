package com.yoav.todolist.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * home page
 * **/
@Controller
public class RootController {

    /**
     * get the home page
     * **/
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getRoot(){
        return "index";
    }
}
