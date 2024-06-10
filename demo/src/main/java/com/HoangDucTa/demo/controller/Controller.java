package com.HoangDucTa.demo.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.HoangDucTa.demo.dto.ForgotPasswordDTO;
import com.HoangDucTa.demo.dto.LoginDTO;
import com.HoangDucTa.demo.dto.SignUpDTO;
import com.HoangDucTa.demo.dto.VerifyTokenRequestDTO;
import com.HoangDucTa.demo.entity.UserInfo;
import com.HoangDucTa.demo.repository.UserInfoRepository;
import com.HoangDucTa.demo.security.jwt.JWTToken;
import com.HoangDucTa.demo.security.jwt.TokenProvider;
import com.HoangDucTa.demo.service.OTPService;
import com.HoangDucTa.demo.service.ProductSercice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth/")
@Tag(name = "1. login")
public class Controller {
	@Autowired
	private ProductSercice productService;
	@Autowired
	private OTPService otpService;
	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserInfoRepository repository;

	// thêm mới user
	@Operation(summary = "Thêm mới user")
	@PostMapping(value = { "/signup" })
	public ResponseEntity<?> SignUp(@Valid @RequestBody SignUpDTO signUpDTO) {
		return productService.SignUp(signUpDTO);
	}

	// login
	@Operation(summary = "login + sinh OTP")
	@PostMapping(value = { "/signin" })
	public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {

		return productService.login(loginDTO);
	}

	// verify OTP
	@Operation(summary = "verify OTP và sinh token")
	@PostMapping(value = { "/verifyOTP" })
	public ResponseEntity<?> verifyOtp(@RequestBody VerifyTokenRequestDTO verifyTokenRequestDTO) {
		String username = verifyTokenRequestDTO.getUsername();
		Integer otp = verifyTokenRequestDTO.getOtp();
		Boolean isOtpValid = otpService.validateOTP(username, otp);
		if (!isOtpValid) {
			return new ResponseEntity<>("OTP" + "\t" + +otp + "\r\n" + "không chính xác",HttpStatus.UNAUTHORIZED);
			// "OTP" + "\t" + +otp + "\r\n" + "không chính xác",
		}
		String token = tokenProvider.generateToken(username);
		JWTToken response = new JWTToken(token);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// quyên mật khẩu
	@Operation(summary = "Nhập username cần đổi mật khẩu và sinh ra link đổi")
	@PostMapping(value = { "/forgotPassword" })
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
		//check tồn tại username
		if (!productService.forgotPassword(forgotPasswordDTO)) {
			return new ResponseEntity<>("username không tồn tại", HttpStatus.FORBIDDEN);
		}
		//sinh token và tạo link đổi MK
		String token = tokenProvider.generateToken(forgotPasswordDTO.getUsername());
		String resetLink = "http://localhost:8080/api/auth/resetPassword";
		return new ResponseEntity<>(token + "\r\n" + "\r\n" + resetLink, HttpStatus.OK);

	}

	@Operation(summary = "đổi mật khẩu và lưu")
	@PostMapping(value = { "/resetPassword/{token}" })
	public ResponseEntity<?> resetpassword(@PathVariable String token, @RequestParam String newpassword1,
			@RequestParam String newpassword2) {
		//lấy usernamr từ token
		String username = tokenProvider.getUsernameFromToken(token);
		//check token
		boolean result = tokenProvider.validateToken(token);
		if (!result) {
			return new ResponseEntity<>("token không tồn tại", HttpStatus.FORBIDDEN);
		}
		//check nhập password mới
		if (!newpassword1.equals(newpassword2)) {
			return new ResponseEntity<>("password mới không trùng khớp", HttpStatus.ACCEPTED);
		}
		//update password vào DB theo usernam đã get được
		Optional<UserInfo> user = repository.findByUsername(username);
		user.get().setPassword(passwordEncoder.encode(newpassword2));
		repository.save(user.get());
		return new ResponseEntity<>("password đã được đổi", HttpStatus.OK);

	}
}
