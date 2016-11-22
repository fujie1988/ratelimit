package com.xxx.ratelimit.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.xxx.ratelimit.aop.RateLimit;

@Component
public class DemoService {
	
	final private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	AtomicInteger counter = new AtomicInteger();
	
	@RateLimit(qps=0)
	public String querySQL(String name) {		
		LOGGER.info("=======     name={},counter={}   ========", name, counter.getAndIncrement());
//		if(1==1) throw new RuntimeException();
		return "good luck!";
	}

	public void testJson() {
		System.out.println("AAA,yes!!!");
	}
}
