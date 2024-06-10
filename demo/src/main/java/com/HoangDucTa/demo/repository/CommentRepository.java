package com.HoangDucTa.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.HoangDucTa.demo.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	 Page<Comment> findAllByPostId(Long postId, Pageable pageable);

	 @Query("SELECT c FROM Comment c, Post p Where p.postId = c.postId And (p.username=?1 OR c.username = ?1) AND c.commentId=?2")
	    Comment findByCommentInUserPost(String username, Long commentId);

	    boolean existsByCommentIdAndUsername(Long commentId, String username);
}
