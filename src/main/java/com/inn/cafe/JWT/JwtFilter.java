package com.inn.cafe.JWT;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

@Component
public class JwtFilter extends OncePerRequestFilter{

	@Autowired
	CustomerUserDetailsService customerUserDetailsService;
	
	@Autowired
	JwtUtils jwtUtils;
	
	Claims claims=null;
	
	private String userName=null;
	
	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
			throws ServletException, IOException {
		
		if(httpServletRequest.getServletPath().matches("/user/login|/user/signup|/user/forgotPassword")) {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
			
		}else
		{
			String authorizationHeader=httpServletRequest.getHeader("Authorization");
			String token = null;
		
		    if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
		    	 token=authorizationHeader.substring(7);
		    	userName=jwtUtils.extractUsername(token);
		    	claims = jwtUtils.extractAllClaims(token);
		    	System.out.println("\n Token:"+token);
		    }
		    
		    if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
		    	UserDetails userDetails =customerUserDetailsService.loadUserByUsername(userName);
		    	if(jwtUtils.validateToken(token, userDetails)) {
		    		UsernamePasswordAuthenticationToken passwordAuthenticationToken=
		    				new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
		    		
		    		passwordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
		    		
		    		SecurityContextHolder.getContext().setAuthentication(passwordAuthenticationToken);
		    		
		    		
		    	}
		    }
		    filterChain.doFilter(httpServletRequest, httpServletResponse);
		}	
	}
	
	
	public boolean isAdmin() {
		return "admin".equalsIgnoreCase((String) claims.get("role"));
	}

	public boolean isUser() {
		return "user".equalsIgnoreCase((String) claims.get("role"));
	}
	
	public String getCurrentUser() {
		return userName;
	}
}
