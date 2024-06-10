package com.HoangDucTa.demo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.HoangDucTa.demo.entity.Image;
import com.HoangDucTa.demo.repository.ImageRepository;

@Service
public class ImageService {

	@Autowired
	private ImageRepository imageRepository;
	
	public void saveImage(Long relateId, String prefix, String url) {
		Image image = new Image();

		image.setFileName(url);
		image.setPrefix(prefix);
		image.setRelateId(relateId);

		imageRepository.save(image);
	}

	// xoá ảnh theo relateId
	public void deleteImage(Long relateId, String prefix) {
		List<Image> images = getImagesByRelateId(relateId, prefix);

		if (!images.isEmpty()) {
			imageRepository.deleteAll(images);
		}
	}

	//lấy ảnh mới nhất theo relateId
	public Image getLatestImagesByRelateId(Long relateId, String prefix) {
        Pageable pageable = PageRequest.of(0, 1);
        List<Image> image = (List<Image>) imageRepository.findLatestByRelateIdAndPrefix(relateId, prefix, pageable);
        if (image.size() > 0) {
            return image.get(0);
        } else {
            return null;
        }
    }
	
	
	
	//lấy list ảnh
	public List<String> getListImageByRelateId(Long relateId, String prefix) {
        List<Image> images = getImagesByRelateId(relateId, prefix);
        List<String> imageUrls = new ArrayList<>(); // Danh sách URL của hình ảnh
        //check tồn tại
        if (!images.isEmpty()) {
            // Lặp qua danh sách Image và tạo URL
            for (Image image : images) {
                String fileName = image.getFileName(); // Tên tệp từ bảng Image
                String fileUrl = fileName;
                imageUrls.add(fileUrl); // Thêm URL vào danh sách
            }
        }

        return imageUrls;
    }
	
	public List<Image> getImageUrl(Long relateId, String prefix){
        return imageRepository.findAllFilenameByRelateIdAndPrefix(relateId, prefix); 
        
	}
	
	
	// check có phải là ảnh
	public boolean isValidImage(MultipartFile file) throws IOException {

		File f = convertMultiPartToFile(file);

		double size = 0;
		String type = "";

		if (ImageIO.read(f) != null) {
			size = f.length() / (1024 * 1024);

			String mimetype = new MimetypesFileTypeMap().getContentType(f.getName());
			type = mimetype.split("/")[0];
		}

		Files.deleteIfExists(f.toPath());
		return type.equals("image") && size < 2;
	}

	public File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));

		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();

		return convFile;
	}

	public List<Image> getImagesByRelateId(Long relateId, String prefix) {
		return imageRepository.findAllByRelateIdAndPrefix(relateId, prefix);
	}
}
