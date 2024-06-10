package com.HoangDucTa.demo.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.HoangDucTa.demo.dto.PostDTO;
import com.HoangDucTa.demo.entity.Like;
import com.HoangDucTa.demo.entity.Post;
import com.HoangDucTa.demo.repository.LikeRepository;
import com.HoangDucTa.demo.repository.PostRepository;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private ImageService imageService;

	@Autowired
	private LikeRepository likeRepository;

	private static final String PREFIX = "posts";

	// lưu bài post
	public Long insertPost(String username, String contents) {
		Post post = new Post();

		post.setUsername(username);
		post.setContents(contents);
		post.setDeleteFlg(0);
		Post postInsert = postRepository.save(post);
		return postInsert.getPostId();
	}

	// xoá bài post
	public void inactivePost(Post post) {
		post.setDeleteFlg(1);
		postRepository.save(post);
	}

	// check tồn tại bài post
	public boolean isValidPost(String contents, MultipartFile[] files) throws IOException {
		if (contents == null && files == null)
			return false;
		if (files != null) {
			for (MultipartFile file : files) {
				if (!imageService.isValidImage(file)) {
					return false;
				}
			}
		}
		return true;
	}

	public Post getPostById(Long postId) {
		return postRepository.findPostById(postId);
	}

	public boolean isUserPost(String username, Long postId) {
		return postRepository.existsByUsernameAndPostId(username, postId);
	}

	// update nội dung bài post
	public void updatePost(Post post, String contents) {
		post.setContents(contents);

		postRepository.save(post);
	}

	// xoá ảnh bài post
	public void deleteImageByRelateId(Long relateId, String prefix) {
		imageService.deleteImage(relateId, prefix);
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

	//tạo tri tiết bài post
	public PostDTO responsePost(Post post) {
		return PostDTO.builder().postId(post.getPostId()).username(post.getUsername()).contents(post.getContents())
				.photos(imageService.getListImageByRelateId(post.getPostId(), PREFIX))
				.usersLiked(getUsersLiked(post.getPostId()))
				//.isEdited(post.getCreatedAt().before(post.getUpdatedAt()))
				.build();
	}

	// thay đổi trạng thái like và dislike
	public Long changeReactionPost(Long postid, String username) {
		Like like = likeRepository.findByPostIdAndUsername(postid, username);

		if (like == null) {
			Like newLike = new Like();

			newLike.setPostId(postid);
			newLike.setUsername(username);

			Like inssertLike = likeRepository.save(newLike);

			return inssertLike.getLikeId();
		} else {
			likeRepository.delete(like);
			return null;
		}
	}

	public List<Like> getLikesByPostId(Long postid) {
		return likeRepository.findAllByPostId(postid);
	}

	// lấy danh sách like
	public List<String> getUsersLiked(Long postId) {
		List<String> usersLiked = new ArrayList<>();

		List<Like> likes = getLikesByPostId(postId);

		if (!likes.isEmpty()) {
			likes.forEach(like -> usersLiked.add(like.getUsername()));
		}
		return usersLiked;
	}
	
	 public Page<Post> getAllPostsByUsername(List<String> username, int currentPage, int pageSize){
	        return postRepository.findAllByUsername(username,PageRequest.of(currentPage - 1, pageSize));
	    }
}
