package com.HoangDucTa.demo.security.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private TokenProvider tokenProvider;

	private UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(TokenProvider tokenProvider, UserDetailsService userDetailsService) {
		this.tokenProvider = tokenProvider;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			// get token from http request
			String token = getJwtFromRequest(request);

			// validate token
			if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {

				// get email from token
				String username = tokenProvider.getUsernameFromToken(token);

				// load user associated with token
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			}
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace(); // Ghi log lỗi ra console
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			ObjectMapper mapper = new ObjectMapper();
			PrintWriter writer = response.getWriter();
			writer.write(mapper.writeValueAsString(new String[] { "Token đã hết hạn." }));
			writer.flush();
			writer.close();
		}
	}

	
	public String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}
	
	//get username từ request
	public String getUsernameFromRequest(HttpServletRequest request){
        String username = getJwtFromRequest(request);
        return username != null ? tokenProvider.getUsernameFromToken(username) : null;
    }

}
