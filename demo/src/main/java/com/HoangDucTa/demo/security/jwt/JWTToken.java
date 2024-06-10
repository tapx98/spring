package com.HoangDucTa.demo.security.jwt;

import lombok.Data;

@Data
public class JWTToken {
	
	private String idToken;

	public JWTToken(String idToken) {
		this.idToken = idToken;
	}
	
	
}
