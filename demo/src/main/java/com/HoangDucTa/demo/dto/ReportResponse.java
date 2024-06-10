package com.HoangDucTa.demo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportResponse {

	private Long numberPost;

    private Long numberComment;

    private Long numberFriend;

    private Long numberLike;
}
