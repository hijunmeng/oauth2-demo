package com.gosuncn.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * 鉴权过滤器，只对开放的接口放行，其他则必须验证token正确才放行
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        logger.info("uri=" + serverHttpRequest.getURI().toString());
        String path = serverHttpRequest.getPath().value();
        //fixme:哪些路径应该放行，哪些应该鉴权，这里的逻辑应该做成可配置的；此处为了简单演示，先硬编码了
        if (path.startsWith("/a/open")) {
            logger.info(path + ":开放接口，直接放行");
            return chain.filter(exchange);
        }
        logger.info(path + ":授权接口，需要验证");

        String token = getToken(serverHttpRequest.getHeaders());
        logger.info("token=" + token);


        if (!checkToken(token)) {
            //fixme:此处的返回内容应该自定义协议格式，这里为了简单演示就只用了一段描述
            ServerHttpResponse response = exchange.getResponse();
            DataBuffer buffer = response.bufferFactory().wrap("token验证失败，可能是token错误或者未携带token".getBytes());
            return response.writeWith(Mono.just(buffer));

        }

        // 从token中获得用户信息（用户名+设备码），根据用户信息从数据库查询到此用户的过去登录设备码，如果与token中包含的设备码不一致
        // 则提示此用户已被踢下线，如果一致继续执行
      /*  String username=getUserFromToken();
        String deviceCode=getDeviceCodeFromToken();
        String oldDeviceCode=getDeviceCodeWithUser(username);
        if(!oldDeviceCode.equals(deviceCode)){
            //fixme:此处的返回内容应该自定义协议格式，这里为了简单演示就只用了一段描述
            ServerHttpResponse response = exchange.getResponse();
            DataBuffer buffer = response.bufferFactory().wrap("当前用户已被踢下线，请重新登录".getBytes());
            return response.writeWith(Mono.just(buffer));
        }*/
        return chain.filter(exchange);
        //return chain.filter(exchange);
		/*// 此处写死了，演示用，实际中需要采取配置的方式
		if (getIp(headers).equals("127.0.0.1")) {
			ServerHttpResponse response = exchange.getResponse();
			ResponseData data = new ResponseData();
			data.setCode(401);
			data.setMessage("非法请求");
			byte[] datas = JsonUtils.toJson(data).getBytes(StandardCharsets.UTF_8);
			DataBuffer buffer = response.bufferFactory().wrap(datas);
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
			return response.writeWith(Mono.just(buffer));
		}
		return chain.filter(exchange);*/
    }

    /**
     * 验证token是否有效
     *
     * @param accessToken
     * @return
     */
    private boolean checkToken(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            return false;
        }
        RestTemplate restTemplate = new RestTemplate();
        //fixme:此处的url硬编码了，实际上应该做成可配置的
        String url = "http://localhost:8011/oauth/check_token?token={token}";
        HashMap<String, String> map = new HashMap<>();
        map.put("token", accessToken);
        ResponseEntity<String> responseEntity;
        try {
            //此处如果是token错误则会抛出异常，原因是RestTemplate内部对非2XX的都作了处理
            responseEntity = restTemplate.getForEntity(url, String.class, map);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("/oauth/check_token端点返回错误：" + e.getMessage());
            return false;
        }
        return responseEntity.getStatusCode() == HttpStatus.OK;
    }

    /**
     * 从请求头中获取token
     *
     * @param headers
     * @return 没有则为null
     */
    private String getToken(HttpHeaders headers) {
        String auth = headers.getFirst("Authorization");
        if (auth == null) {
            return null;
        }
        if (auth.isEmpty()) {
            return null;
        }
        if (!auth.startsWith("bearer")) {
            return null;
        }
        String[] arr = auth.split(" ");
        if (arr.length != 2) {
            return null;
        }
        return arr[1];
    }

}
