package com.parimal.linguaconnect.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.parimal.linguaconnect.Service.JwtService;
import com.parimal.linguaconnect.Service.UserInfoService;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final UserInfoService userInfoservice;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response, 
        FilterChain filterChain
        ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

       if(authHeader==null||!authHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid entry");
            return;
       }

       jwt=authHeader.substring(7);
       username=jwtService.extarctUsername(jwt);
       if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){

           
            try{
                UserDetails userDetails= this.userInfoservice.loadUserByUsername(username);
                if(jwtService.isTokenValid(jwt, userDetails)){
                    UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            catch(UsernameNotFoundException e){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid entry");
            }
       }
       filterChain.doFilter(request, response);
    }
}
