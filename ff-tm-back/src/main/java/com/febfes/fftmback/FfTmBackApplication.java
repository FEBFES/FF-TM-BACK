package com.febfes.fftmback;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
public class FfTmBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(FfTmBackApplication.class, args);
    }

}
