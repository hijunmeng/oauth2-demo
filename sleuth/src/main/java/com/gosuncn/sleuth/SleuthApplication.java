package com.gosuncn.sleuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SleuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SleuthApplication.class, args);
    }


    @GetMapping("/test")
    public String abc(){
        return "hello";
    }
    @GetMapping("/test11")
    public String abc1(){
        return "hello";
    }

    @RequestMapping("hello")
    public String abc132(){
        return "hello";
    }
}
