package com.HoangDucTa.demo;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.HoangDucTa.demo.dto.LoginDTO;
import com.HoangDucTa.demo.dto.SignUpDTO;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ControllerTest {
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
    protected MockMvc mockMvc;
	
	
	//test lỗi thêm mới user
	@Test
    void signUp_invalid() throws Exception {
        SignUpDTO userRequest = new SignUpDTO("", "hoangta", "12345", "123");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password", is("password.invalid")))
                .andExpect(jsonPath("$.phone", is("phoneNo.invalid")))
                .andExpect(jsonPath("$.email", is("email.invalid")))
                .andExpect(jsonPath("$.username", is("username.not-empty")));
    }
	//test thêm mới user thành công
	 @Test
	    void signUp_success() throws Exception {
		 SignUpDTO userRequest = new SignUpDTO("hoangtatest", "kikan@gmail.com", "ta1234", "+84388129205");
	        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(objectMapper.writeValueAsString(userRequest)))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$.message", is("sign-up.success")));
	    }
	 
	 @Test
	    void sendOtp_invalid() throws Exception {
	        LoginDTO sendOtpRequest = new LoginDTO("", "");
	        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(objectMapper.writeValueAsString(sendOtpRequest)))
	                .andExpect(status().isBadRequest())
	                .andExpect(jsonPath("$.email", is("email.not-empty")))
	                .andExpect(jsonPath("$.password", is("password.not-empty")));
	    }


}
