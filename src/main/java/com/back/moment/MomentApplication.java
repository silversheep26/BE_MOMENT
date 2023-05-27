package com.back.moment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableJpaAuditing
public class MomentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MomentApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedOrigins("http://localhost:8080", "http://localhost:3000",
                        "http://moment-photo.ap-northeast-2.amazonaws.com",  // s3
                        "http://moment.cadbf9mahvf5.ap-northeast-2.rds.amazonaws.com")    // db(rds)
//                        .allowedOriginPatterns("*")
                    .exposedHeaders("ACCESS_KEY", "REFRESH_KEY", "Authorization", "USER_ROLE", "USER_EMAIL")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "PATCH")
                    //.allowedHeaders()
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }

}
