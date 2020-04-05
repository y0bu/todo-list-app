package com.yoav.todolist.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
* this page have all the features that this web application provide and will provide
* **/
@Controller
public class FeaturesController {

    /**
     * @return simply return the feature page (static page)
     * **/
    @RequestMapping(value = "/features", method = RequestMethod.GET)
    public String getFeatures() {
        return "features/index";
    }
}
