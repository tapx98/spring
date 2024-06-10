package com.HoangDucTa.demo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "post_id")
	    private Long postId;

	    @Column(name = "username")
	    private String username;

	    @Column(name = "contents")
	    private String contents;

	    @Column(name = "delete_flg")
	    private Integer deleteFlg;

	    @Column(name = "created_at", insertable = false, updatable = false)
	    private Date createdAt;

	    @Column(name = "updated_at", insertable = false, updatable = false)
	    private Date updatedAt;
}
