package org.dbshardingrouting.log;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyLogger {

    @Value("${spring.datasource.shard1.url}")
    private String shard1Url;

    @Value("${spring.datasource.shard2.url}")
    private String shard2Url;

    @PostConstruct
    public void logProperties() {
        System.out.println("Shard1 URL: " + shard1Url);
        System.out.println("Shard2 URL: " + shard2Url);
    }
}