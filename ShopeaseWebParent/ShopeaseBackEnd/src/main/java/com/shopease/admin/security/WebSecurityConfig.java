package com.shopease.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {   

	@Bean
	public UserDetailsService userDetailsService() {
	     return new ShopeaseUserDetailsService();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	     
	    authProvider.setUserDetailsService(userDetailsService());
	    authProvider.setPasswordEncoder(passwordEncoder());
	 
	    return authProvider;
	}
	
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.authenticationProvider(authenticationProvider());

	}
	
//	// allow log in without username and password
//	// for development only
//	@Bean   
//	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http
//			.authorizeHttpRequests(auth -> auth
//					.anyRequest().permitAll());
//		return http.build();
//	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests(auth -> auth
	        	.requestMatchers("/users/**", "/settings/**").hasAuthority("Admin")
	        	.requestMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")
	            .anyRequest().authenticated()
	        )
	        .formLogin(form -> form
	            .loginPage("/login")
	            .usernameParameter("email")
	            .permitAll()
	         )
	         .logout(logout -> logout.permitAll())
	         .rememberMe(rememberMe -> 
	         	rememberMe.key("AbcdEfghIjklmNopQrsTuvXyz_0123456789")
	         	.tokenValiditySeconds(7 * 24 * 3600));
	       
	      
	    return http.build();
	}
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//	    http
//	        .authorizeHttpRequests(auth -> auth
//	            .requestMatchers("/login").permitAll()
//	            .requestMatchers("/users/**", "/settings/**").hasAuthority("Admin")
//	            .requestMatchers("/somePath/**").hasAnyAuthority("Admin", "Editor", "Salesperson")
//	            // Add more path patterns and authorities as needed
//	            .anyRequest().authenticated()
//	        )
//	        .formLogin(form -> form
//	            .loginPage("/login")
//	            .usernameParameter("email")
//	            .permitAll()
//	        )
//	        .rememberMe(rememberMe -> rememberMe.key("AbcdEfghIjklmNopQrsTuvXyz_0123456789"))
//	        .logout(logout -> logout.permitAll());
//
//	    http.headers().frameOptions().sameOrigin();
//
//	    return http.build();
//	}

	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
	}
	
}