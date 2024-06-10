package com.HoangDucTa.demo.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentDTO {

	 private Long commentId;

	    private Long postId;

	    private String username;

	    private String contents;

	    private List<String> photos;

	    private boolean isEdited;
}
