package com.HoangDucTa.demo.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.HoangDucTa.demo.entity.UserInfo;

public class UserInfoUserDetails implements UserDetails {

	/**
	 * 
	 */
	private String username;
	private String password;
//	private List<GrantedAuthority> authorities;

	// convet usermane password
	public UserInfoUserDetails(UserInfo userInfo) {
		username = userInfo.getUsername();
		password = userInfo.getPassword();
//		authorities = Arrays.stream(userInfo.getRoles().split(",")).map(SimpleGrantedAuthority::new)
//				.collect(Collectors.toList());

	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		System.out.println("getAuthorities");
//		return authorities;
		return null;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
