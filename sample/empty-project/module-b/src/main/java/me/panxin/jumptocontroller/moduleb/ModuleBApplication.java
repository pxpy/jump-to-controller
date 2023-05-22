package me.panxin.jumptocontroller.moduleb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients({"me.panxin.jumptocontroller"})
@SpringBootApplication
public class ModuleBApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleBApplication.class, args);
    }

}
