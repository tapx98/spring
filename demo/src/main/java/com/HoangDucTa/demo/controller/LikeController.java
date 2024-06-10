package com.HoangDucTa.demo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.HoangDucTa.demo.entity.Post;
import com.HoangDucTa.demo.security.jwt.JwtAuthenticationFilter;
import com.HoangDucTa.demo.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "5.	Like bài post")
public class LikeController {

	@Autowired
	private JwtAuthenticationFilter JwtAuthenticationFilter;
	
	@Autowired
	private PostService postService;
	
	@Operation(summary = "Like/Unlike bài viết")
	@PostMapping(value = "/{postId}/reaction")
	public ResponseEntity<?> reactionPost(@PathVariable Long postId, HttpServletRequest request) {
		Post post = postService.getPostById(postId);
		if (post == null) {
			// check bài viết tồn tại
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("bài viết không tồn tại");
		} else {
			Long id = postService.changeReactionPost(postId, JwtAuthenticationFilter.getUsernameFromRequest(request));
			if (id != null) {
				// like thành công
				return ResponseEntity.ok().body("đã like bài post");
			} else {
				// bỏ like
				return ResponseEntity.ok().body("đã bỏ like bài post");
			}
		}
	}
}
