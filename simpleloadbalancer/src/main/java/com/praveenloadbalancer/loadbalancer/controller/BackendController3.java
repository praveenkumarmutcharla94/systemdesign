package com.praveenloadbalancer.loadbalancer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackendController3 {

    @GetMapping("/process/server3")
    public String processRequest() {
        return "Processed by Backend Server 3";
    }
}