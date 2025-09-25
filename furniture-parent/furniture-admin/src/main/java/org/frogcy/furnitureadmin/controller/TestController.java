package org.frogcy.furnitureadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @GetMapping("/home")
    @ResponseBody
    public String home(){
        return "Welcome home page";
    }
}
