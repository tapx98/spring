package com.HoangDucTa.demo.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDTO {

	@NotEmpty(message = "username.not-empty")
	private String username;

	@Email(message = "email.invalid")
	@NotEmpty(message = "email.not-empty")
	private String email;

	@NotEmpty(message = "password.not-empty")
	@Pattern(regexp = "^.{6,12}$", flags = Pattern.Flag.UNICODE_CASE, message = "password.invalid")
	private String password;

	@NotEmpty(message = "phone.not-empty")
	@Pattern(regexp = "^\\+?\\d{10,12}$", message = "phone.invalid")
	private String phone;
}
