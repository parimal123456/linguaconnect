package com.parimal.linguaconnect.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.parimal.linguaconnect.Service.UserInfoService;
import com.parimal.linguaconnect.filter.JwtAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private UserInfoService userInfoService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    public SecurityConfig(UserInfoService userInfoService,JwtAuthenticationFilter jwtAuthenticationFilter){
        this.userInfoService=userInfoService;
        this.jwtAuthenticationFilter=jwtAuthenticationFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(sessionManagement->sessionManagement
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .csrf(csrf -> csrf.disable())
        .formLogin(formLogin->formLogin.disable())
        .httpBasic(httpBasic->httpBasic.disable())
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration 
    ) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
	public AuthenticationProvider authenticationProvider() { 
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(); 
		authenticationProvider.setUserDetailsService(userInfoService); 
		authenticationProvider.setPasswordEncoder(passwordEncoder()); 
		return authenticationProvider; 
	} 

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
