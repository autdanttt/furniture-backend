package org.frogcy.furnitureadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"org.frogcy.furniturecommon.entity"})
public class FurnitureAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(FurnitureAdminApplication.class, args);
    }

}
