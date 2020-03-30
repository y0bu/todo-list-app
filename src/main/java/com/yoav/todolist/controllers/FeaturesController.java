package com.yoav.todolist.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FeaturesController {

    @RequestMapping(value = "/features", method = RequestMethod.GET)
    public String getFeatures() {
        return "features/index";
    }
}
