package com.gosuncn.serverb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
public class ServerbApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerbApplication.class, args);
    }


    @GetMapping("/open/test")
    public String test(){
        return "test from server b";
    }


    @GetMapping("/res/name")
    public String name(){
        return "res from server b";
    }

    @PostMapping("/login")
    public Object login(@RequestParam String username, @RequestParam String password){
        RestTemplate restTemplate=new RestTemplate();
        String url = "http://localhost:8011/oauth/token";
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("grant_type","password");
        map.add("client_id","client1");
        map.add("client_secret","secret");
        map.add("username",username);
        map.add("password",password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return restTemplate.postForEntity(url,request,String.class);

    }
}
