package com.codingshuttle.youtube.hospitalManagement.config;

import com.codingshuttle.youtube.hospitalManagement.entity.type.PermissionType;
import com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType;
import com.codingshuttle.youtube.hospitalManagement.security.JwtAuthFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final PasswordEncoder passwordEncoder;

    private  final JwtAuthFilter jwtAuthFilter;

    private  final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws  Exception{
        httpSecurity
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->
                        auth.requestMatchers("/public/**", "/auth/**").permitAll()
                                .requestMatchers("/admin/**").hasRole(RoleType.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE,"/admin/**").hasAnyAuthority(PermissionType.APPOINTMENT_DELETE.name())
                                .requestMatchers("/docters/**").hasAnyRole(RoleType.DOCTOR.name(),RoleType.ADMIN.name())
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionConfig->
                exceptionConfig.accessDeniedHandler((request,response, accessDeniedException)-> {
                            handlerExceptionResolver.resolveException(request,response,null,accessDeniedException);
                }

                ));


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
