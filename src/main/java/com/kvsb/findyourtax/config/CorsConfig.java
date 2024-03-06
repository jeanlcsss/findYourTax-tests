package com.kvsb.findyourtax.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*"); // Permitir solicitações de todos os origens
        config.addAllowedMethod("*"); // Permitir todos os métodos HTTP (GET, POST, PUT, DELETE, etc)
        config.addAllowedHeader("*"); // Permitir todos os cabeçalhos
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
