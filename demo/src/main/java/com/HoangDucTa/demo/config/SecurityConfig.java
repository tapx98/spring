package com.HoangDucTa.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.HoangDucTa.demo.security.CustomUserDetailsService;
import com.HoangDucTa.demo.security.jwt.JwtAuthenticationEntryPoint;
import com.HoangDucTa.demo.security.jwt.JwtAuthenticationFilter;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.Data;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Data
@SecurityScheme(
		//sử dụng khi cần yêu cầu xác thực
		  name = "Bearer Authentication",
		  type = SecuritySchemeType.HTTP,
		  bearerFormat = "JWT",
		  scheme = "bearer"
		)
public class SecurityConfig {


	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	@Autowired
	private JwtAuthenticationFilter JwtAuthenticationFilter;
	
	@Autowired
	private JwtAuthenticationEntryPoint JwtAuthenticationEntryPoint;
	

	// Cấu hình authentication theo đường dẫn
	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(csrf -> csrf
                        .disable())
                .authorizeHttpRequests((authorize) -> authorize
                                .antMatchers("/").permitAll()
                                .antMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                                .anyRequest().authenticated())
                                .exceptionHandling()
                                .authenticationEntryPoint(JwtAuthenticationEntryPoint);
        
        httpSecurity.addFilterBefore(JwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                return httpSecurity.build();
	}

	// Cấu hình Password encoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//xác định cách mà bộ lọc Spring thực hiện xác thực
//	@Bean
//	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//		return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService)
//				.passwordEncoder(passwordEncoder()).and().build();
//	}
	  @Bean
	    public AuthenticationManager authenticationManager(
	                                 AuthenticationConfiguration configuration) throws Exception {
	        return configuration.getAuthenticationManager();
	    }
}
