package com.HoangDucTa.demo.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

	@NotEmpty(message = "username.not-empty")
	private String username;
	@NotEmpty(message = "password.not-empty")
	private String password;
	
}
