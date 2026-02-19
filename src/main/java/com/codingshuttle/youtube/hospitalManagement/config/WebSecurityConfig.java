package com.codingshuttle.youtube.hospitalManagement.config;

import com.codingshuttle.youtube.hospitalManagement.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final PasswordEncoder passwordEncoder;

    private  final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws  Exception{
        httpSecurity
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->
                        auth.requestMatchers("/public/**", "/auth/**").permitAll()
                                .anyRequest().authenticated()
                              //  .requestMatchers("/admin/**").hasRole("ADMIN")
                               // .requestMatchers("/docters/**").hasAnyRole("ADMIN","DOCTOR")
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        return httpSecurity.build();

    }

    //@Bean
    public UserDetailsService userDetailsService(){

        UserDetails user1  = User
                .withUsername("admin")
                .password(passwordEncoder.encode("pass"))
                .roles("ADMIN").build();

        UserDetails user2  = User
                .withUsername("patient")
                .password(passwordEncoder.encode("pass"))
                .roles("PATIENT").build();

        return new InMemoryUserDetailsManager(user1,user2);
    }
}
