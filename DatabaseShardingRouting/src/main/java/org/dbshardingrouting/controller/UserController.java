package org.dbshardingrouting.controller;

import org.dbshardingrouting.bean.User;
import org.dbshardingrouting.datasource.DatabaseContextHolder;
import org.dbshardingrouting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping
    public void createUser(@RequestParam("username") String username, @RequestParam("email") String email) {
        // Determine the shard key based on the request or some logic
        String shardKey = determineShardKey(username);

        // Set the shard key in the context
        DatabaseContextHolder.setShardKey(shardKey);

        // Call the service to save the user
        userService.saveUser(username, email);

        // Clear the shard key from the context if necessary
        DatabaseContextHolder.clear();
    }

    private String determineShardKey(String username) {
        // Simple sharding logic based on username hash
        int shardKey = username.hashCode() % 2;
        return shardKey == 0 ? "shard1" : "shard2";
    }

}
