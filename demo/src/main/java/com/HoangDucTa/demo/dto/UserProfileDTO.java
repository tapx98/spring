package com.HoangDucTa.demo.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
	@NotEmpty(message = "first-name.not-empty")
	String firstname;

	@NotEmpty(message = "last-name.not-empty")
	String lastname;

	@NotEmpty(message = "birthday.not-empty")
	String date_of_birth;

	@NotEmpty(message = "gender.not-empty")
	@Pattern(regexp = "^(Male|Female)$", flags = Pattern.Flag.UNICODE_CASE, message = "gender.invalid")
	String gender;
	String address;
}
