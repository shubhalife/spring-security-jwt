package com.codingshuttle.youtube.hospitalManagement.security;

import com.codingshuttle.youtube.hospitalManagement.entity.User;
import com.codingshuttle.youtube.hospitalManagement.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j
public  class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    private final AuthUtil authUtil;

    private  final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            log.info("incoming request url: " + request.getRequestURI());

            final String reqTokenHeader = request.getHeader("Authorization");
            if (reqTokenHeader == null || !reqTokenHeader.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
            }

            String token = reqTokenHeader.split("Bearer")[1];
            String username = authUtil.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByUsername(username).orElseThrow();

                UsernamePasswordAuthenticationToken authtoken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authtoken);

            }

            filterChain.doFilter(request, response);

        }catch (Exception e){
            //pass exception to mvc handler
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }
}
