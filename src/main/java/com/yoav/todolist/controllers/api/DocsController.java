package com.yoav.todolist.controllers.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocsController {

    @GetMapping("/api/doc")
    public String getApiDocs() {
        return "APIDocs";
    }
}
