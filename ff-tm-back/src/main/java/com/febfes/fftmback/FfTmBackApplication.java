package com.febfes.fftmback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class FfTmBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(FfTmBackApplication.class, args);
    }

}
