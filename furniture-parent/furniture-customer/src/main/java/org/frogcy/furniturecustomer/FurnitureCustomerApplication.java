package org.frogcy.furniturecustomer;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EntityScan({"org.frogcy.furniturecommon.entity"})
public class FurnitureCustomerApplication {

    @Value("${cloudinary.cloud.name}")
    private String cloudinaryCloudName;
    @Value("${cloudinary.api.key}")
    private String cloudinaryApiKey;
    @Value("${cloudinary.api.secret.key}")
    private String cloudinarySecretKey;

    @Bean
    public Cloudinary cloudinaryConfig() {
        Cloudinary cloudinary = null;
        Map config = new HashMap();
        config.put("cloud_name", cloudinaryCloudName);
        config.put("api_key",cloudinaryApiKey);
        config.put("api_secret", cloudinarySecretKey);
        cloudinary = new Cloudinary(config);
        return cloudinary;
    }

    public static void main(String[] args) {
        SpringApplication.run(FurnitureCustomerApplication.class, args);
    }

}
