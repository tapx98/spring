package com.HoangDucTa.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.HoangDucTa.demo.dto.UserProfileDTO;
import com.HoangDucTa.demo.entity.Image;
import com.HoangDucTa.demo.entity.UserInfo;
import com.HoangDucTa.demo.security.jwt.JwtAuthenticationFilter;
import com.HoangDucTa.demo.service.ImageService;
import com.HoangDucTa.demo.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "2.	Quản lý thông tin cá nhân")
public class UserProfileController {

	@Autowired
	private UserProfileService userProfileService;
	@Autowired
	private JwtAuthenticationFilter JwtAuthenticationFilter;
	@Autowired
	private ImageService ImageService;

	private static final String PREFIX = "avatars";

	// xem thông tin user
	@Operation(summary = "Xem thông tin cá nhân")
	@GetMapping("/userProfile")
	public ResponseEntity<?> getUserProfile(@RequestParam(name = "username", required = false) String username,
			HttpServletRequest request) {
		// khi ko nhập username thì sẽ lấy data theo username đã login
		if (username == null) {
			username = JwtAuthenticationFilter.getUsernameFromRequest(request);
		}
		Map<String, Object> userObj = userProfileService.getUserProfileDetail(username);
		if (userObj.get("user") != null) {
			return ResponseEntity.ok().body(userObj);
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("user không tồn tại");
		}
	}

	// upload thông tin user
	@Operation(summary = "Thay đổi thông tin cá nhân")
	@PatchMapping("/userProfile")
	public ResponseEntity<?> updateUserProfile(@RequestBody @Valid UserProfileDTO userProfileDTO,
			HttpServletRequest request) {
		// lấy usernamw từ requet rồi thực hiện get data theo usernam
		UserInfo username = userProfileService.getUserProfiles(JwtAuthenticationFilter.getUsernameFromRequest(request));
		// update thông tin cho user vừa lấy được
		UserInfo userProfile = userProfileService.updateUserProfiles(username, userProfileDTO);

		return ResponseEntity.ok().body(userProfile);
	}

	@Operation(summary = "Thêm ảnh đại diện")
	@PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadAvatar(@RequestPart(value = "file") MultipartFile file, HttpServletRequest request)
			throws IOException {
		if (ImageService.isValidImage(file)) {
			UserInfo user = userProfileService.getUserProfiles(JwtAuthenticationFilter.getUsernameFromRequest(request));

			// Lấy đường dẫn tuyệt đối đến thư mục uploads
			String uploadPath = "D:\\springboot\\workspace\\uploads\\" + PREFIX;

			// Tạo thư mục uploads nếu nó chưa tồn tại
			File directory = new File(uploadPath);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			// Tạo tên tệp mới dựa trên thời gian hiện tại
			String originalFileName = file.getOriginalFilename();

			String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
			String newFileName = "avatar_" + System.currentTimeMillis() + fileExtension;

			// Tạo đường dẫn đến tệp ảnh mới trong thư mục avatars
			Path avatarPath = Paths.get(directory.getAbsolutePath(), newFileName);

			// Lưu file ảnh vào thư mục avatars
			Files.copy(file.getInputStream(), avatarPath);
			ImageService.saveImage(user.getUser_id(), PREFIX, avatarPath.toString());

			return ResponseEntity.ok().body("thêm avatar thành công");
		} else {
			return ResponseEntity.badRequest().body("thêm avatar không thành công");
		}
	}

	@Operation(summary = "Danh sách ảnh đại diện")
	@GetMapping(value = "/avatar")
	public ResponseEntity<?> getListAvatar(HttpServletRequest request) {
		UserInfo user = userProfileService.getUserProfiles(JwtAuthenticationFilter.getUsernameFromRequest(request));
		List<Image> avatars = ImageService.getImagesByRelateId(user.getUser_id(), PREFIX);

		List<String> response = new ArrayList<>();
		avatars.forEach(avatar -> response.add(avatar.getFileName()));

		if (response.size() > 0) {
			return ResponseEntity.ok().body(response);
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(user + "user chưa có avartar");
		}

	}

}
