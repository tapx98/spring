package com.HoangDucTa.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.HoangDucTa.demo.common.Utils;
import com.HoangDucTa.demo.dto.PostDTO;
import com.HoangDucTa.demo.entity.Comment;
import com.HoangDucTa.demo.entity.Friend;
import com.HoangDucTa.demo.entity.Post;
import com.HoangDucTa.demo.security.jwt.JwtAuthenticationFilter;
import com.HoangDucTa.demo.service.CommentService;
import com.HoangDucTa.demo.service.FriendService;
import com.HoangDucTa.demo.service.ImageService;
import com.HoangDucTa.demo.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "3.	đăng bài")
public class PostController {

	private static final String PREFIX = "posts";

	@Autowired
	private PostService postService;

	@Autowired
	private ImageService ImageService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private FriendService friendService;

	@Autowired
	private Utils utils;

	@Autowired
	private JwtAuthenticationFilter JwtAuthenticationFilter;

	@Operation(summary = "Đăng bài viết mới")
	@PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> newPost(@RequestPart(value = "contents", required = false) String contents,
			@RequestPart(value = "files", required = false) MultipartFile[] files, HttpServletRequest request

	) throws IOException {
		// check tồn tại bài post
		if (postService.isValidPost(contents, files)) {
			// lưu bài post và lấy ra postId
			Long postId = postService.insertPost(JwtAuthenticationFilter.getUsernameFromRequest(request), contents);
			// lưu ảnh bài post
			if (files != null) {
				for (MultipartFile file : files) {
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
					String newFileName = "Post" + System.currentTimeMillis() + fileExtension;

					// Tạo đường dẫn đến tệp ảnh mới trong thư mục Post
					Path postPath = Paths.get(directory.getAbsolutePath(), newFileName);

					// Lưu file ảnh vào thư mục Post
					Files.copy(file.getInputStream(), postPath);
					ImageService.saveImage(postId, PREFIX, postPath.toString());
				}
			}

			return ResponseEntity.ok().body("thêm mới bài viết thành công");
		} else {
			return ResponseEntity.badRequest().body("thêm mới bài viết không thành công");
		}
	}

	@Operation(summary = "Chỉnh sửa bài viết")
	@PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> editPost(@PathVariable Long postId,
			@RequestPart(value = "contents", required = false) String contents,
			@RequestPart(value = "files", required = false) MultipartFile[] files, HttpServletRequest request)
			throws IOException {

		// kiểm tra quyền
		if (checkPermission(postId, request)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("username không tồn tại bài post");
		}
		// check tồn tại bài post
		if (!postService.isValidPost(contents, files)) {
			return ResponseEntity.badRequest().body("bài post không chính xác");
		}

		Post post = postService.getPostById(postId);

		if (post == null) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("bài post không tồn tại");
		}
		// update content bài pots
		postService.updatePost(post, contents);
		// lưu ảnh baiif post
		if (files != null) {
			// xoá ảnh post cũ
			postService.deleteImageByRelateId(postId, PREFIX);
			for (MultipartFile file : files) {
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
				String newFileName = "Post" + System.currentTimeMillis() + fileExtension;
				// Tạo đường dẫn đến tệp ảnh mới trong thư mục Post
				Path postPath = Paths.get(directory.getAbsolutePath(), newFileName);
				// Lưu file ảnh vào thư mục Post
				Files.copy(file.getInputStream(), postPath);
				ImageService.saveImage(postId, PREFIX, postPath.toString());
			}
		}
		return ResponseEntity.ok().body("update bài post thành công");
	}

	@Operation(summary = "Xóa (hidden) bài viết")
	@DeleteMapping(value = "/{postId}")
	public ResponseEntity<?> inActivePost(@PathVariable Long postId, HttpServletRequest request) {
		// kiểm tra quyền
		if (checkPermission(postId, request)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("username không tồn tại bài post");
		}
		Post post = postService.getPostById(postId);
		// check tồn tại bài post
		if (post == null) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("bài post không tồn tại");
		} else {
			// xoá bài post
			postService.inactivePost(post);
			return ResponseEntity.ok().body("xoá bài post thành công");
		}
	}

	// kiểm tra post ID theo username
	private boolean checkPermission(Long postId, HttpServletRequest request) {
		return !postService.isUserPost(JwtAuthenticationFilter.getUsernameFromRequest(request), postId);
	}

	@Operation(summary = "Xem chi tiết bài viết")
	@GetMapping(value = "/{postId}")
	public ResponseEntity<?> getPost(@PathVariable Long postId) {
		Map<String, Object> postDetail = new HashMap<>();
		Post post = postService.getPostById(postId);
		Page<Comment> comment = commentService.getAllCommentsByPostId(postId, 1, 10);
		if (post == null) {
			// postDetail.put("message", utils.setMessage("post.not-found"));
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("bài post không tồn tại");
		} else {
			postDetail.put("post", postService.responsePost(post));
			postDetail.put("comment", comment.stream().map(commentService::returnComment).collect(Collectors.toList()));
			return ResponseEntity.ok().body(postDetail);
		}
	}

	

	@Operation(summary = "Dòng thời gian")
	@GetMapping(value = "/timeline")
	public ResponseEntity<?> loadTimeLine(
			@RequestParam(name = "currentPage", required = false, defaultValue = "0") Integer currentPage,
			@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
			HttpServletRequest request) {
		String username = JwtAuthenticationFilter.getUsernameFromRequest(request);
		List<Friend> friends = friendService.getFriendByUsername(username);

		List<String> usernames = new ArrayList<>(friends.stream()
				.map(fr -> fr.getUsername().equals(username) ? fr.getUsernameFriend() : fr.getUsername())
				.collect(Collectors.toList()));

		usernames.add(username);
		Page<Post> posts = postService.getAllPostsByUsername(usernames, utils.returnCurrentPage(currentPage),
				utils.returnPageSize(pageSize));
		List<PostDTO> responses = posts.stream().map(postService::responsePost).collect(Collectors.toList());

		return ResponseEntity.ok().body(responses);
	}

}
