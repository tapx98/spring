package com.HoangDucTa.demo.repository;

import java.util.Optional;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.HoangDucTa.demo.entity.UserInfo;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

	Optional<UserInfo> findByUsername(String username);
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);

	//câu select data xuất báo cáo
	@Query(value = 
		    "SELECT " +
		        "count(DISTINCT p.post_id) as numberPost, " +
		        "count(DISTINCT c.comment_id) as numberComment, " +
		        "count(DISTINCT l.like_id) as numberLike, " +
		        "count(DISTINCT f.friend_id) as numberFriend " +
		    "FROM userinfo u " +
		    "LEFT JOIN posts p ON p.username = u.username AND p.delete_flg = 0 " +
		    "LEFT JOIN comments c ON c.username = u.username " +
		    "LEFT JOIN likes l ON l.post_id = p.post_id " +
		    "LEFT JOIN friends f ON (f.username = u.username OR f.username_friend = u.username) AND f.status=1 " +
		    "WHERE u.username=?1", nativeQuery = true)
		    Tuple findDataToReport(String username);
	
}
