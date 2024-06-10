package com.HoangDucTa.demo.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.HoangDucTa.demo.dto.ReportResponse;
import com.HoangDucTa.demo.security.jwt.JwtAuthenticationFilter;
import com.HoangDucTa.demo.service.ReportExcelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reports")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "7. Báo cáo")
public class ReportController {

	@Autowired
	private JwtAuthenticationFilter JwtAuthenticationFilter;

	@Autowired
	ReportExcelService reportService;

	@Operation(summary = "Export file báo cáo")
	@GetMapping(value = "")
	public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
		// Thiết lập loại nội dung của phản hồi là "application/octet-stream" để báo
		// trình duyệt biết rằng đây là một file tải về.
		response.setContentType("application/octet-stream");

		// Tạo định dạng ngày và lấy ngày hiện tại để đặt tên file.
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDate = dateFormat.format(new Date());

		// Thiết lập header "Content-Disposition" để chỉ định rằng đây là một file đính
		// kèm với tên file cụ thể.
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=reports_" + currentDate + ".xlsx";

		// tạo tiêu đề
		response.setHeader(headerKey, headerValue);
		List<String> headers = List.of("Post number", "Comment number", "Friend number", "Like number");

		ReportResponse data = reportService.getDataToReport(JwtAuthenticationFilter.getUsernameFromRequest(request));
		// ghi dữ liệu vào file sau đó gửi file này trong phản hồi HTTP.
		reportService.exportFile(data, headers, response);

	}

}
