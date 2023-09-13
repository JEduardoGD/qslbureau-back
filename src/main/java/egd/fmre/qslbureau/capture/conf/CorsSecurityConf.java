package egd.fmre.qslbureau.capture.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CorsSecurityConf {
    
    @Value("${FRONT_NAMESERVER}")
    private String frontNameserver;
    
    @Value("${QSLBUREAU_PORTAL}")
    private String qslBureauPortal;
    
    @Bean
    WebMvcConfigurer corsConfigurer() {
        log.info("CORS allowed url: {} and {}", frontNameserver, qslBureauPortal);
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(frontNameserver, qslBureauPortal)
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
    
}