package com.HoangDucTa.demo.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostDTO {
	private Long postId;

    private String username;

    private String contents;

    private List<String> photos;

    private List<String> usersLiked;

    private boolean isEdited;
}
