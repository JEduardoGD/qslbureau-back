package egd.fmre.qslbureau.capture.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import egd.fmre.qslbureau.capture.helper.StaticValuesHelper;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CorsSecurityConf {
    @Value("${ALLOWED_CORS_ORIGINS}")
    private String allowedCorsOrigins;
    
    @Bean
    WebMvcConfigurer corsConfigurer() {
        log.info("CORS allowed url's: {}", allowedCorsOrigins);
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedCorsOrigins.split(StaticValuesHelper.COMMA_REGEX))
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
    
}