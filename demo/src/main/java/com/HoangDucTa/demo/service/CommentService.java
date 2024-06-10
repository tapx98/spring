package com.HoangDucTa.demo.service;

import java.io.File;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.HoangDucTa.demo.dto.CommentDTO;
import com.HoangDucTa.demo.entity.Comment;
import com.HoangDucTa.demo.repository.CommentRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private ImageService ImageService;
	
	private static final String PREFIX = "comments";

	// thêm mới comment
	public Comment insertComment(Long postId, String username, String contents) {
		Comment comment = new Comment();
		comment.setPostId(postId);
		comment.setUsername(username);
		comment.setContents(contents != null ? contents : "");

		return commentRepository.save(comment);
	}

	// lấy comment theo id
	public Optional<Comment> getCommentById(Long commentId) {
		return commentRepository.findById(commentId);
	}

	//lấy tất cả comment theo bài post
	public Page<Comment> getAllCommentsByPostId(Long postId, int currentPage, int pageSize) {
		return commentRepository.findAllByPostId(postId, PageRequest.of(currentPage - 1, pageSize));
	}
	
	//tạo tri tiết comment
	public CommentDTO returnComment(Comment c) {
        return CommentDTO.builder()
                .commentId(c.getCommentId())
                .postId(c.getPostId())
                .username(c.getUsername())
                .contents(c.getContents())
                .photos(ImageService.getListImageByRelateId(c.getCommentId(), "comments"))
                .build();
    }
	
	//update comment
	public void updateComment(Long commentId, String contents){
        Comment comment = getCommentById(commentId).orElse(null);

        if (comment != null) {
            comment.setContents(contents != null ? contents : "");
            commentRepository.save(comment);
        }
    }
	
	// xoá ảnh bài post
		public void deleteImageByRelateId(Long relateId, String prefix) {
			ImageService.deleteImage(relateId, prefix);
			// Lấy đường dẫn tuyệt đối đến thư mục uploads
			String uploadPath = "D:\\springboot\\workspace\\uploads\\" + PREFIX;
			File directory = new File(uploadPath);
			if (directory.exists()) {
				// Lặp qua tất cả các tệp trong thư mục và xóa tất cả các tệp có tên liên quan
				// đến relateId
				File[] files = directory.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file.getName().contains(relateId.toString())) {
							file.delete();
						}
					}
				}
			}
		}
	
		
	public boolean canCommentUpdate(Long commentId, String username) {
        return commentRepository.existsByCommentIdAndUsername(commentId, username);
    }
	
	public boolean canCommentDelete(String username, Long commentId) {
        Comment comment = commentRepository.findByCommentInUserPost(username, commentId);
        return (comment != null);
    }
	
	public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }
}
