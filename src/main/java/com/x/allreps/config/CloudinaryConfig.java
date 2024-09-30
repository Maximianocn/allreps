package com.x.allreps.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dhwmdehqf",
                "api_key", "158185152867859",
                "api_secret", "4KnreyFNHYm_q8Da8v6zaxbK5C4",
                "secure", true
        ));
    }
}
