package com.HoangDucTa.demo.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.HoangDucTa.demo.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

	// Đây là Native SQL
	@Query(value = "SELECT * FROM Image WHERE relate_id = ?1 AND prefix = ?2 ORDER BY created_at DESC", nativeQuery = true)
	
	//lấy ảnh mới nhất
	List<Image> findLatestByRelateIdAndPrefix(Long relateId, String prefix, Pageable pageable);
	
	List<Image> findAllByRelateIdAndPrefix(Long relateId, String prefix);
	List<Image> findAllFilenameByRelateIdAndPrefix(Long relateId, String prefix);
}
