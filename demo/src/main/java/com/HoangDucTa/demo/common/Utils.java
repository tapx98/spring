package com.HoangDucTa.demo.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Utils {
	
	@Value("${config.page.page-size-default}")
    private int pageSizeDefault;
	
	public Date convertStringToDate(String date, String format, String timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
	
	 public int returnCurrentPage(Integer currentPage) {
	        return (currentPage == null || currentPage < 1) ? 1 : currentPage;
	    }
	 
	 public int returnPageSize(Integer pageSize) {
	        return pageSize == null ? pageSizeDefault : pageSize;
	    }
}
