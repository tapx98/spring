package com.HoangDucTa.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.HoangDucTa.demo.entity.Friend;
import com.HoangDucTa.demo.entity.UserInfo;
import com.HoangDucTa.demo.security.jwt.JwtAuthenticationFilter;
import com.HoangDucTa.demo.service.FriendService;
import com.HoangDucTa.demo.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "6. Bạn bè")
public class FriendController {

	@Autowired
	private JwtAuthenticationFilter JwtAuthenticationFilter;

	@Autowired
	private FriendService friendService;

	@Autowired
	private UserProfileService userProfileService;

	@Operation(summary = "Gửi lời mời kết bạn")
	@PostMapping(value = "/friend/{usernameFriend}/send-request")
	public ResponseEntity<?> sendFriendRequest(HttpServletRequest request, @PathVariable String usernameFriend) {
		String username = JwtAuthenticationFilter.getUsernameFromRequest(request);
		if (usernameFriend.equals(username)) {
			return ResponseEntity.badRequest().body("không tìm thấy bạn bè");
		}
		Friend friend = friendService.getFriendRelate(username, usernameFriend);
		if (friend == null) {
			friendService.createFriend(username, usernameFriend);
			return ResponseEntity.ok().body("đã gửi yêu cầu kết bạn");
		} else {
			return ResponseEntity.badRequest().body("đã tồn tại yêu cầu kết bạn");
		}
	}

	@Operation(summary = "Danh sách yêu cầu kết bạn")
	@GetMapping(value = "/friend/requests")
	public ResponseEntity<?> getAllRequestFriends(HttpServletRequest request) {
		List<Friend> friends = friendService.getRequestFriends(JwtAuthenticationFilter.getUsernameFromRequest(request));
		if (!friends.isEmpty()) {
			List<UserInfo> users = friends.stream().map(fr -> userProfileService.getUserProfiles(fr.getUsername()))
					.collect(Collectors.toList());

			return ResponseEntity.ok().body(users);
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("không có yêu cầu kết bạn");
		}
	}
	
	 @Operation(summary = "Chấp nhận lời mời kết bạn")
	    @PatchMapping(value = "/friend/{usernameFriend}/accept-friend")
	    public ResponseEntity<?> acceptFriendRequest(HttpServletRequest request, @PathVariable String usernameFriend){
	        Friend friend = friendService.getFriendRelate(JwtAuthenticationFilter.getUsernameFromRequest(request), usernameFriend);
	        if (friend != null && friendService.acceptFriend(friend)) {
	            return ResponseEntity.ok().body("chấp thuận lời mời kết bạn thành công");
	        } else{
	            return ResponseEntity.status(HttpStatus.ACCEPTED).body("không có yêu cầu kết bạn");
	        }
	    }
	 
	   @Operation(summary = "xoá lời mời kết bạn")
	    @DeleteMapping(value = "/friend/{usernameFriend}/reject-request")
	    public ResponseEntity<?> rejectFriendRequest(HttpServletRequest request, @PathVariable String usernameFriend){
	        Friend friend = friendService.getFriendRelate(JwtAuthenticationFilter.getUsernameFromRequest(request), usernameFriend);
	        if (friend != null && friendService.rejectFriend(friend)) {
	            return ResponseEntity.ok().body("đã từ chối lời mời kết bạn");
	        } else{
	            return ResponseEntity.status(HttpStatus.ACCEPTED).body("không có yêu cầu kết bạn");
	        }
	    }
	   
	   @Operation(summary = "Hủy kết bạn")
	    @DeleteMapping(value = "/friend/{usernameFriend}")
	    public ResponseEntity<?> unfriend(HttpServletRequest request, @PathVariable String usernameFriend){
	        Friend friend = friendService.getFriendRelate(JwtAuthenticationFilter.getUsernameFromRequest(request), usernameFriend);
	        if (friend != null && friendService.unFriend(friend)) {
	            return ResponseEntity.ok().body("xoá kết bạn thành công");
	        } else{
	            return ResponseEntity.status(HttpStatus.ACCEPTED).body("bạn bè không tồn tại");
	        }
	    }
}
