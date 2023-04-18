package com.rrtv.rpc.consumer.controller;

import com.rrtv.rpc.api.service.ComeService;
import com.rrtv.rpc.client.annotation.RpcAutowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Comecontroller {

   @RpcAutowired(version = "1.0")
    private ComeService comeService;

    @GetMapping("/hello/come")
    public ResponseEntity<String> pullServiceInfo(@RequestParam("name") String name){
        return  ResponseEntity.ok(comeService.sayHello(name));
    }
}
