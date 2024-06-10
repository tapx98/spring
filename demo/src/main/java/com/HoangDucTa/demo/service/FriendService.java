package com.HoangDucTa.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.HoangDucTa.demo.entity.Friend;
import com.HoangDucTa.demo.repository.FriendRepository;

@Service
public class FriendService {

	@Autowired
	private FriendRepository friendRepository;

	//khởi tạo lời mời kết bạn
	public void createFriend(String username, String usernameFriend) {
		Friend friend = new Friend();
		friend.setUsername(username);
		friend.setUsernameFriend(usernameFriend);
		friend.setStatus(0);

		friendRepository.save(friend);
	}
	//kiểm tra có lời mời kb và chấp thuận lời mời
	public boolean acceptFriend(Friend friend){
        if (friend.getStatus().equals(0)) {
            friend.setStatus(1);
            friendRepository.save(friend);

            return true;
        }

        return false;
    }
	
	//xoá lời kết bạn
	 public boolean rejectFriend(Friend friend){
	        if (friend.getStatus().equals(0)) {
	            friendRepository.delete(friend);

	            return true;
	        }
	        return false;
	    }
	 
	 //xoá bạn bè
	 public boolean unFriend(Friend friend){
	        if (friend.getStatus().equals(1)) {
	            friendRepository.delete(friend);

	            return true;
	        }
	        return false;
	    }

	// lấy danh sách friend
	public Friend getFriendRelate(String username, String usernameFriend) {
		return friendRepository.findRequestFriendByUsername(username, usernameFriend);
	}

	// lấy danh sách yêu cầu kết bạn
	public List<Friend> getRequestFriends(String username) {
		return friendRepository.findAllRequestFriends(username);
	}
	
	public List<Friend> getFriendByUsername(String username){
        return friendRepository.findAllFriendsByUsername(username);
    }
}
