package com.pz.api_previsao.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class PageController {

    @GetMapping("/")
    public Object getMethodName() {
        return new ModelAndView("index.html");
    }

}
