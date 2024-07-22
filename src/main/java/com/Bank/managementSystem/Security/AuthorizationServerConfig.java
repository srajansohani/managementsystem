//package com.Bank.managementSystem.Security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//import org.springframework.security.oauth2.jwt.JoseHeaderNames;
//import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
//import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers;
//import org.springframework.security.oauth2.server.authorization.settings.JwtEncodingContext;
//import org.springframework.security.oauth2.server.authorization.settings.OAuth2AuthorizationServerConfigurer;
//import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContextCustomizer;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class AuthorizationServerConfig {
//
//    @Bean
//    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
//        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
//        return http.formLogin().disable().build();
//    }
//
//    @Bean
//    public JwtEncodingContextCustomizer jwtEncodingContextCustomizer() {
//        return context -> context.getHeaders().put(JoseHeaderNames.TYP, "JWT");
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
