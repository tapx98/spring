package com.HoangDucTa.demo.dto;

import com.sun.istack.NotNull;

import lombok.Data;

@Data
public class VerifyTokenRequestDTO {
	
	@NotNull
	private String username;
	
	@NotNull
	private Integer otp;
	
	private Boolean rememberMe;
	
	
	
}
