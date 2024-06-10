package com.HoangDucTa.demo.service;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Tuple;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.HoangDucTa.demo.dto.ReportResponse;
import com.HoangDucTa.demo.repository.UserInfoRepository;

@Service
public class ReportExcelService {

	@Autowired
	UserInfoRepository userInfoRepository;

	// khởi tạo excel
	public void exportFile(ReportResponse data, List<String> header, HttpServletResponse response) {
		// định dạng XLSX.
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("report");
		// Ghi tiêu đề và dữ liệu
		this.writeHeader(header, sheet);
		this.writeData(data, sheet);

		try {
			// để ghi workbook vào phản hồi HTTP, sau đó đóng workbook và stream.
			ServletOutputStream outputStream = response.getOutputStream();
			workbook.write(outputStream);
			workbook.close();
			outputStream.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Lấy dữ liệu từ cơ sở dữ liệu
	public ReportResponse getDataToReport(String username) {
		Tuple data = userInfoRepository.findDataToReport(username);
		// Dữ liệu được chuyển đổi thành một đối tượng
		return ReportResponse.builder().numberPost(data.get("numberPost", BigInteger.class).longValue())
				.numberComment(data.get("numberComment", BigInteger.class).longValue())
				.numberFriend(data.get("numberFriend", BigInteger.class).longValue())
				.numberLike(data.get("numberLike", BigInteger.class).longValue()).build();
	}

	//Tạo hàng đầu tiên và điền vào các tiêu đề từ danh sách headers.
	private void writeHeader(List<String> headers, XSSFSheet sheet) {
		Row firtRow = sheet.createRow(0);
		for (String header : headers) {
			firtRow.createCell(headers.indexOf(header)).setCellValue(header);
		}
	}

	//Tạo hàng thứ hai và điền dữ liệu đã get được
	private void writeData(ReportResponse data, XSSFSheet sheet) {
		Row secondRow = sheet.createRow(1);
		secondRow.createCell(0).setCellValue(data.getNumberPost());
		secondRow.createCell(1).setCellValue(data.getNumberComment());
		secondRow.createCell(2).setCellValue(data.getNumberFriend());
		secondRow.createCell(3).setCellValue(data.getNumberLike());
	}

}
