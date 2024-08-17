package com.praveenloadbalancer.loadbalancer.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackendController1 {

    @GetMapping("/process/server1")
    public String processRequest() {
        return "Processed by Backend Server 1";
    }
}