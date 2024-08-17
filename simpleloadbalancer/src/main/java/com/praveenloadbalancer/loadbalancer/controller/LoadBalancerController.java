package com.praveenloadbalancer.loadbalancer.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class LoadBalancerController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);
    private final Random random = new Random();
    private final ConcurrentHashMap<String, AtomicInteger> connections = new ConcurrentHashMap<>();

    // List of backend server URLs
    private final List<String> backendServers = Arrays.asList(
            "http://localhost:8081/process/server1",
            "http://localhost:8082/process/server2",
            "http://localhost:8083/process/server3",
            "http://localhost:8084/process/server4"
    );

    @GetMapping("/process")
    public String processRequest() {
        // Choose the algorithm here (Round Robin, Random, Least Connections)
        String backendServer = selectBackendServerRoundRobin();
        // String backendServer = selectBackendServerRandom();
        // String backendServer = selectBackendServerLeastConnections();

        return restTemplate.getForObject(backendServer, String.class);
    }

    private String selectBackendServerRoundRobin() {
        int index = roundRobinIndex.getAndUpdate(i -> (i + 1) % backendServers.size());
        return backendServers.get(index);
    }

    private String selectBackendServerRandom() {
        int index = random.nextInt(backendServers.size());
        return backendServers.get(index);
    }

    private String selectBackendServerLeastConnections() {
        return backendServers.stream()
                .min((server1, server2) ->
                        connections.getOrDefault(server1, new AtomicInteger(0)).get() -
                                connections.getOrDefault(server2, new AtomicInteger(0)).get())
                .orElse(backendServers.get(0));
    }
}
