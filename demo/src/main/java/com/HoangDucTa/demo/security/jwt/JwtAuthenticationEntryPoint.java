package com.HoangDucTa.demo.security.jwt;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Trả về mã lỗi 401 Unauthorized khi người dùng không được xác thực
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Access is denied");
    }
}
