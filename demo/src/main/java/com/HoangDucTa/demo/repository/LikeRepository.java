package com.HoangDucTa.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.HoangDucTa.demo.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

	List<Like> findAllByPostId(Long postId);

	Like findByPostIdAndUsername(Long postId, String username);
}
