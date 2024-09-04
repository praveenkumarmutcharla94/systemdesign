package com.praveenloadbalancer.loadbalancer.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class LoadBalancerController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);
    private final Random random = new Random();
    private final ConcurrentHashMap<String, AtomicInteger> connections = new ConcurrentHashMap<>();
    private final NavigableMap<Integer, String> hashCircle = new TreeMap<>();

    public LoadBalancerController() throws NoSuchAlgorithmException {
        initializeHashCircle();
    }

    // List of backend server URLs
    private final List<String> backendServers = Arrays.asList(
            "http://localhost:8081/process/server1",
            "http://localhost:8082/process/server2",
            "http://localhost:8083/process/server3",
            "http://localhost:8084/process/server4"
    );

    @GetMapping("/process")
    public String processRequest() {
        // Use a dynamic key for consistent hashing
        String key = "request-id-" + UUID.randomUUID(); // Example of using a unique key
        String backendServer = selectBackendServerConsistentHashing(key);

        System.out.println("Selected backend server: " + backendServer);

        try {
            String response = restTemplate.getForObject(backendServer, String.class);
            System.out.println("Response received: " + response);
            return response;
        } catch (Exception e) {
            System.err.println("Failed to connect to backend server: " + e.getMessage());
            return "Error: Unable to process request.";
        }
    }


    private void initializeHashCircle() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        for (String server : backendServers) {
            for (int i = 0; i < 10; i++) { // Adding virtual nodes for better distribution
                int hash = hashFunction(md, server + "#" + i);
                hashCircle.put(hash, server);
            }
        }

        hashCircle.forEach((hash, server) -> {
            System.out.println("Hash: " + hash + ", Server: " + server);
        });
    }

    private int hashFunction(MessageDigest md, String key) {
        byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
        return Math.abs(Arrays.hashCode(digest)); // Convert digest to integer hash
    }

    private String selectBackendServerConsistentHashing(String key) {
        if (hashCircle.isEmpty()) {
            throw new IllegalStateException("Hash circle is empty. No backend servers available.");
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }

        int hash = hashFunction(md, key);
        Map.Entry<Integer, String> entry = hashCircle.ceilingEntry(hash);

        if (entry == null) {
            entry = hashCircle.firstEntry(); // Wrap around the circle if no entry is found
        }

        return entry.getValue();
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
