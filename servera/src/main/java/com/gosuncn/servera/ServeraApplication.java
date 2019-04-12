package com.gosuncn.servera;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class ServeraApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServeraApplication.class, args);
    }


    public static Map<String,String> memory=new HashMap<>();

    @GetMapping("/open/test")
    public String test() {
        return "test from server a";
    }

    @GetMapping("/res/name")
    public String name() {
        return "res from server a";
    }

    /**
     * 实现单设备登录
     *
     * @param deviceCode
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/open/login")
    public Object login(@RequestParam String deviceCode, @RequestParam String username, @RequestParam String password) {
        ResponseEntity responseEntity = auth(username, password);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return "用户名或密码错误";
        }
        String oldDeviceCode = getDeviceCodeByUser(username);
        if (oldDeviceCode == null) {
            updateDeviceCodeWithUser(username, deviceCode);
            return responseEntity;
        }
        if (!oldDeviceCode.equals(deviceCode)) {
            updateDeviceCodeWithUser(username, deviceCode);
            push(oldDeviceCode);
        }
        return responseEntity;

    }

    private ResponseEntity auth(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8011/oauth/token";
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("grant_type", "password");
        map.add("client_id", "client1");
        map.add("client_secret", "secret");
        map.add("username", username);
        map.add("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return restTemplate.postForEntity(url, request, String.class);
    }

    /**
     * 获得用户的登录设备码
     *
     * @param username
     * @return
     */
    private String getDeviceCodeByUser(String username) {

        return memory.get(username);
    }

    /**
     * 更新用户的登录设备码
     *
     * @param username
     * @return
     */
    private void updateDeviceCodeWithUser(String username, String deviceCode) {
        memory.put(username,deviceCode);
    }

    /**
     * 推送通知下线
     *
     * @param deviceCode 即将下线的设备
     */
    private void push(String deviceCode) {

    }


}
