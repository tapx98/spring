package com.HoangDucTa.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.HoangDucTa.demo.entity.Friend;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

	//sử dụng  Native SQL select friend theo username
	@Query(value = "SELECT * FROM Friends WHERE (username=?1 AND username_friend=?2) OR (username=?2 AND username_friend=?1)", nativeQuery = true)
    Friend findRequestFriendByUsername(String username, String usernameFriend);

    @Query(value = "SELECT * FROM Friends WHERE (username=?1 OR username_friend=?1) AND status=1", nativeQuery = true)
    List<Friend> findAllFriendsByUsername(String username);

    @Query(value = "SELECT * FROM Friends WHERE username_friend=?1 AND status=0", nativeQuery = true)
    List<Friend> findAllRequestFriends(String username);
}
