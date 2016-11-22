package com.xxx.ratelimit.support;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.xxx.ratelimit.exception.RateLimitException;
import com.xxx.ratelimit.model.RateClass;
import com.xxx.ratelimit.model.RateMethod;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class JsonConfig {

	private final static String configFileName = "rateLimitConfig.json";

	private static Map<String, RateClass> rateClassMap = null;

	private static Logger logger = LoggerFactory.getLogger(JsonConfig.class);

	public static void load() {
		long beginTime = System.nanoTime();
		ClassPathResource resource = new ClassPathResource(configFileName);
		InputStream is = null;
		if (resource.exists()) {
			try {
				is = resource.getInputStream();
				List<RateClass> list = JSON.parseArray(IOUtils.toString(is), RateClass.class);
				if (null == list || list.isEmpty()) {
					throw new RateLimitException(configFileName + " do not have classList");
				}
				for (RateClass rateClass : list) {
					if (rateClass.getMethods() != null) {
						Map<String, RateMethod> map = Maps.newHashMap();
						for (RateMethod rateMethod : rateClass.getMethods()) {
							map.put(rateMethod.getMethodName(), rateMethod);
						}
						rateClass.setMethodMap(map);
						rateClass.setMethods(null);
					}
				}
				toMap(list);
			} catch (IOException e) {
				logger.error(configFileName + " 配置文件加载失败!", e);
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(configFileName + " 流关闭异常!", e);
				}
			}
			logger.info("init config file:{} cost time:{}", configFileName, System.nanoTime() - beginTime);
		} else {
			logger.error("Can not find " + configFileName);
			throw new RateLimitException("Can not find " + configFileName);
		}
	}

	private static void toMap(List<RateClass> list) {

		if (rateClassMap == null || rateClassMap.isEmpty()) {
			rateClassMap = Maps.newHashMap();
			for (RateClass rateClass : list) {
				rateClassMap.put(rateClass.getClassName(), rateClass);
			}
		} else {
			logger.info("rateClassMap not null");
		}
	}


	public static Map<String, RateClass> loadRateClassMap() {
		load();
		return rateClassMap;
	}

	public static void main(String[] args) {
		loadRateClassMap();
		System.out.println(JSON.toJSONString(rateClassMap,true));
	}

}