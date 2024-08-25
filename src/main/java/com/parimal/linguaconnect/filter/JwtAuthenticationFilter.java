package com.parimal.linguaconnect.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.parimal.linguaconnect.Service.JwtService;
import com.parimal.linguaconnect.Service.TokenService;
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
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response, 
        FilterChain filterChain
        ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String email;

       if(authHeader==null||!authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
       }

       jwt=authHeader.substring(7);
       email=jwtService.extarctUsername(jwt);

       if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){    

           boolean isValidToken=tokenService.findByToken(jwt)
                .map(t->!t.isExpired()&&!t.isRevoked())
                .orElse(false);
                
           UserDetails userDetails= this.userInfoservice.loadUserByUsername(email);
            if(jwtService.isTokenValid(jwt, userDetails) && isValidToken){
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
       }

       filterChain.doFilter(request, response);
    }
}
