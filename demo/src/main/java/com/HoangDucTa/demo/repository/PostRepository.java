package com.HoangDucTa.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.HoangDucTa.demo.entity.Post;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	//sử dụng  Native SQL select theo post ID
    @Query(value = "SELECT * FROM Posts WHERE post_id = ?1 AND delete_flg = 0", nativeQuery = true)
    Post findPostById(Long postId);
    //sử dụng  Native SQL select theo username ID
    @Query(value = "SELECT * FROM Posts WHERE username IN ?1 AND delete_flg = 0 ORDER BY created_at DESC", nativeQuery = true)
    Page<Post> findAllByUsername(List<String> username, Pageable pageable);

    boolean existsByUsernameAndPostId(String username, Long id);
}
