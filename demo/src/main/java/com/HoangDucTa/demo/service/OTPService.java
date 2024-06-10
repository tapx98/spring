package com.HoangDucTa.demo.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class OTPService {

	// thoi gian het han cua OTP
	private static final Integer EXPIRE_MINS = 4;
	// load lai cache
	private LoadingCache<String, Integer> otpCache;

	// luu OTP vao bo nho dem
	public OTPService() {
		otpCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {
					@Override
					public Integer load(String key) {
						return 0;
					}
				});
	}

	// taoj OTP random
	public int generateOTP(String key) {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		otpCache.put(key, otp);
		return otp;
	}

	// luu OTP ung vs key
	public int getOtp(String key) {
		try {
			return otpCache.get(key);
		} catch (Exception e) {
			return 0;
		}
	}

	// xoa OTP o cache
	public void clearOTP(String key) {
		otpCache.invalidate(key);
	}
	
	// lay otp bang key
	public Integer getOPTByKey(String key)
    {
        return otpCache.getIfPresent(key);
    }

	
	//xác thực OTP được cung cấp
		public Boolean validateOTP(String key, Integer otpNumber)
	    {
			// get OTP from cache
	        Integer cacheOTP = this.getOPTByKey(key);
	        if (cacheOTP!=null && cacheOTP.equals(otpNumber))
	        {
	        	this.clearOTP(key);
	            return true;
	        }
	        return false;
	    }


}
