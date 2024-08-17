package com.praveenloadbalancer.loadbalancer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackendController2 {

    @GetMapping("/process/server2")
    public String processRequest() {
        return "Processed by Backend Server 2";
    }
}