package com.HoangDucTa.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.HoangDucTa.demo.entity.Comment;
import com.HoangDucTa.demo.security.jwt.JwtAuthenticationFilter;
import com.HoangDucTa.demo.service.CommentService;
import com.HoangDucTa.demo.service.ImageService;
import com.HoangDucTa.demo.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "4. Bình luận")
@RestController
@RequestMapping("/api/comment")
@SecurityRequirement(name = "Bearer Authentication")
public class CommentController {

	private static final String PREFIX = "comments";

	@Autowired
	private PostService postService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private ImageService ImageService;

	@Autowired
	private JwtAuthenticationFilter JwtAuthenticationFilter;

	@Operation(summary = "Bình luận bài viết")
	@PostMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createComment(@PathVariable Long postId,
			@RequestPart(value = "contents", required = false) String contents,
			@RequestPart(value = "files", required = false) MultipartFile[] files, HttpServletRequest request)
			throws IOException {
		// Map<String, Object> mapComment = new HashMap<>();
		// check tồn tại bài post
		if (postService.isValidPost(contents, files)) {
			// lưu comment
			Comment comment = commentService.insertComment(postId,
					JwtAuthenticationFilter.getUsernameFromRequest(request), contents);
			// lưu ảnh comment
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
					String newFileName = "comment" + System.currentTimeMillis() + fileExtension;
					// Tạo đường dẫn đến tệp ảnh mới trong thư mục comment
					Path postPath = Paths.get(directory.getAbsolutePath(), newFileName);
					// Lưu file ảnh vào thư mục commment
					Files.copy(file.getInputStream(), postPath);
					ImageService.saveImage(comment.getCommentId(), PREFIX, postPath.toString());
				}
			}
			return ResponseEntity.ok().body("bình luận bài viết thành công");
		} else {
			return ResponseEntity.badRequest().body("bài viết không tồn tại");
		}
	}

	@Operation(summary = "Chỉnh sửa bình luận")
	@PatchMapping(value = "/{commentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> editComment(@PathVariable Long commentId,
			@RequestPart(value = "contents", required = false) String contents,
			@RequestPart(value = "files", required = false) MultipartFile[] files, HttpServletRequest request)
			throws IOException {
		Map<String, Object> mapComment = new HashMap<>();
		if (!checkPermissionUpdate(commentId, request)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("comment không tồn tại");
		}

		if (!postService.isValidPost(contents, files)) {
			return ResponseEntity.badRequest().body("bài viết không tồn tại");
		}

		Optional<Comment> comment = commentService.getCommentById(commentId);
		if (comment.isPresent()) {
			commentService.updateComment(commentId, contents);

			if (files != null) {
				// xoá ảnh comment cũ
				commentService.deleteImageByRelateId(commentId, PREFIX);
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
					String newFileName = "comment" + System.currentTimeMillis() + fileExtension;
					// Tạo đường dẫn đến tệp ảnh mới trong thư mục comment
					Path postPath = Paths.get(directory.getAbsolutePath(), newFileName);
					// Lưu file ảnh vào thư mục commment
					Files.copy(file.getInputStream(), postPath);
					ImageService.saveImage(commentId, PREFIX, postPath.toString());
				}
			}
			mapComment.put("comment", commentService.returnComment(comment.get()));
			return ResponseEntity.ok().body(mapComment);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("comment không tồn tại");
		}
	}
	
	@Operation(summary = "Xóa bình luận")
	@DeleteMapping(value = "/{commentId}")
	public ResponseEntity<?> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
		if (!checkPermissionDelete(commentId, request)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("comment không tồn tại");
		}
		Optional<Comment> comment = commentService.getCommentById(commentId);
		if (comment.isPresent()) {
			commentService.deleteComment(comment.get());
			ImageService.deleteImage(commentId, PREFIX);
			return ResponseEntity.ok().body("xoá comment thành công");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("comment không tồn tại");
		}
	}
	

	@Operation(summary = "Xem chi tiết bình luận")
	@GetMapping(value = "/{commentId}")
	public ResponseEntity<?> getComment(@PathVariable Long commentId) {
		Optional<Comment> comment = commentService.getCommentById(commentId);
		Map<String, Object> mapComment = new HashMap<>();

		if (comment.isPresent()) {
			mapComment.put("comment", commentService.returnComment(comment.get()));
			return ResponseEntity.ok().body(mapComment);
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("comment không tồn tại");
		}
	}

	// check tồn tại comment
	private boolean checkPermissionUpdate(Long commentId, HttpServletRequest request) {
		return commentService.canCommentUpdate(commentId, JwtAuthenticationFilter.getUsernameFromRequest(request));
	}
	
	private boolean checkPermissionDelete(Long commentId, HttpServletRequest request) {
		return commentService.canCommentDelete(JwtAuthenticationFilter.getUsernameFromRequest(request), commentId);
	}
}
