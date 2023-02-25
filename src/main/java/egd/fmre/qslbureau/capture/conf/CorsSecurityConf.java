package egd.fmre.qslbureau.capture.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsSecurityConf {
    
    @Value("${FRONT_NAMESERVER}")
    private String frontNameserver;
    
    @Bean
    WebMvcConfigurer corsConfigurer() {
        
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(frontNameserver)
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
    
}