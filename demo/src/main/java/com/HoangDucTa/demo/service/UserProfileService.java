package com.HoangDucTa.demo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.HoangDucTa.demo.common.Utils;
import com.HoangDucTa.demo.dto.UserProfileDTO;
import com.HoangDucTa.demo.entity.Image;
import com.HoangDucTa.demo.entity.UserInfo;
import com.HoangDucTa.demo.repository.UserInfoRepository;

@Service
public class UserProfileService {

	@Autowired
	private UserInfoRepository repository;
	@Autowired
	private ImageService imageService;

	private final Utils utils = new Utils();

	public Map<String, Object> getUserProfileDetail(String username) {

		Map<String, Object> userObj = new HashMap<>();
		// lấy data theo username
		UserInfo user = repository.findByUsername(username).orElse(null);
		if (user != null) {
			// lấy ảnh mới nhất theo userid
			Image avatar = imageService.getLatestImagesByRelateId(user.getUser_id(), "avatars");
			// đây ảnh vào userObj
			userObj.put("avatar", avatar);
		}
		// đẩy thông tin user vào userObj
		userObj.put("user", user);
		return userObj;
	}

	//lấy thông tin từ FE và lưu vào DB
	public UserInfo updateUserProfiles(UserInfo user, UserProfileDTO userProfileDTO) {
		user.setFirstName(userProfileDTO.getFirstname());
		user.setLastName(userProfileDTO.getLastname());
		user.setDateOfBirth(utils.convertStringToDate(userProfileDTO.getDate_of_birth(), "yyyy-MM-dd", "Asia/Hanoi"));
		user.setGender(userProfileDTO.getGender());
		user.setAddress(userProfileDTO.getAddress());

		return repository.save(user);
	}

	//lấy data theo username
	public UserInfo getUserProfiles(String username) {
		return repository.findByUsername(username).orElse(null);
	}

}
