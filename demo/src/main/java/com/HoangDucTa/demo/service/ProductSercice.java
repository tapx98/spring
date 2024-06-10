package com.HoangDucTa.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.HoangDucTa.demo.dto.ForgotPasswordDTO;
import com.HoangDucTa.demo.dto.LoginDTO;
import com.HoangDucTa.demo.dto.SignUpDTO;
import com.HoangDucTa.demo.entity.UserInfo;
import com.HoangDucTa.demo.repository.UserInfoRepository;
import com.HoangDucTa.demo.security.jwt.TokenProvider;

@Service
public class ProductSercice {

	@Autowired
	private UserInfoRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	public OTPService otpService;

	@Autowired
	public TokenProvider tokenProvider;

	// thêm mới user
	public ResponseEntity<?> SignUp(SignUpDTO signUpDTO) {
		if (repository.existsByEmail(signUpDTO.getEmail())) {
			// trả về text ko hợp lệ
			return ResponseEntity.badRequest().body("email đã tồn tại");
		}
		if (repository.existsByUsername(signUpDTO.getUsername())) {
			return ResponseEntity.badRequest().body("username đã tồn tại");
		}

		UserInfo userInfo = new UserInfo();
		userInfo.setUsername(signUpDTO.getUsername());
		userInfo.setEmail(signUpDTO.getEmail());
		// mã hóa password trước khi lưu
		userInfo.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
		userInfo.setPhone(signUpDTO.getPhone());
		repository.save(userInfo);
		return ResponseEntity.ok().body("thêm mới user thanh công");
	}

	// check username passwod
	public ResponseEntity<?> login(LoginDTO loginDTO) {
		try {
			// check username va password lay tu FE so sanh trong DB
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
			// Lớp này lưu trữ hiện trạng bảo mật hiện tại
			SecurityContextHolder.getContext().setAuthentication(authentication);
			int otp = otpService.generateOTP(loginDTO.getUsername());
			return ResponseEntity.ok().body("Username và Password đúng!     " + "OTP" + "  " + otp);
		} catch (Exception e) {
			// exception tra ve loi 403
			return ResponseEntity.badRequest().body("Username hoặc Password sai");
		}

	}

	public boolean forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
		if (repository.existsByUsername(forgotPasswordDTO.getUsername())) {
			// trả về text
			return true;
		}
		return false;
	}

}
