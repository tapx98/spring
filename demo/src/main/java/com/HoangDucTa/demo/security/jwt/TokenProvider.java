package com.HoangDucTa.demo.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.HoangDucTa.demo.entity.UserInfo;
import com.HoangDucTa.demo.repository.UserInfoRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenProvider {

	@Autowired
	private UserInfoRepository repository;

	private String secretKey = "HoangDucTa";

	public String generateToken(String username) {
		UserInfo user = repository.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException("User not found!"));

		Map<String, Object> claims = new HashMap<>();
		return Jwts.builder().setClaims(claims).setSubject(user.getUsername())
				.signWith(SignatureAlgorithm.HS512, secretKey).setIssuedAt(new Date(System.currentTimeMillis()))
				// thời gian hiệu lực
				.setExpiration(new Date(System.currentTimeMillis() + 100 * 60 * 60 * 10)).compact();
	}

	// Lấy ngày hết hiệu lực của token
	public Date getExpirationDateFromToken(String token) {
		final Claims claims = getAllClaimsFromToken(token);
		return claims.getExpiration();
	}

	// Kiểm tra xem token có còn trong thời gian hiệu lực
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// Sử dụng khóa ở trên để giải mã token
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}
	// Lấy tên của user từ token
	public String getUsernameFromToken(String token) {
		Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
		return claimsJws.getBody().getSubject();
	}

	public Long getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		return Long.parseLong(claims.getSubject());
	}

	// Kiểm tra token có hiệu lực
	public Boolean validateToken(String token) {
		return (!isTokenExpired(token));
	}
//	// Kiểm tra token có hiệu lực
//	public Boolean validateToken(String token, UserDetails userDetails) {
//		final String username = getUsernameFromToken(token);
//		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//	}

}
