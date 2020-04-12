package com.yoav.todolist.controllers.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocsController {

    /**
     * @return the documentation of the REST API
     * **/
    @GetMapping("/api/doc")
    public String getApiDocs() {
        return "APIDocs";
    }
}
