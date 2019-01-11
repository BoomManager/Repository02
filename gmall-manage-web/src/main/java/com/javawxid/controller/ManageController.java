package com.javawxid.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ManageController {

    @RequestMapping("index")
    public String doIndex() {
        return "index";
    }

    @RequestMapping("attrListPage")
    public String doAttrListPage() {
        return "attrListPage";
    }
}
