package com.HoangDucTa.demo.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.HoangDucTa.demo.entity.UserInfo;
import com.HoangDucTa.demo.repository.UserInfoRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserInfoRepository repository;
	
	//lấy ra thông tin từ DB theo username
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserInfo> userInfo = repository.findByUsername(username);
		if (!userInfo.isPresent()) {
			throw new UsernameNotFoundException("user not found " + username);
		}
		return new UserInfoUserDetails(userInfo.get());
	}
	
//	@Transactional
//	public UserDetails loadUserById(Long user_id) throws UsernameNotFoundException {
//		UserInfo userInfo = repository.findById(user_id).orElseThrow(
//        );
//
//		return new UserInfoUserDetails(userInfo.getUser_id());
//		
//    }
	

}
