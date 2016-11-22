package com.xxx.ratelimit.aop.impl;


import com.google.common.util.concurrent.RateLimiter;
import com.xxx.ratelimit.aop.RateLimitAspectJ;
import com.xxx.ratelimit.exception.RateLimitException;
import com.xxx.ratelimit.model.RateClass;
import com.xxx.ratelimit.model.RateMethod;
import com.xxx.ratelimit.support.JsonConfig;
import com.xxx.ratelimit.support.RateLimitSupport;
import com.xxx.ratelimit.support.constant.Constants;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class JsonRateLimitAspectJ extends RateLimitAspectJ {

	@Resource
	private RateLimitSupport rateLimitSupport;

	private Map<String, RateClass> jsonCfgMap;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	/**
	 * 初始化json限流配置
	 *
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.debug("init bean {}", this.getClass().getName());
		if (jsonCfgMap == null || jsonCfgMap.isEmpty()) {
			jsonCfgMap = JsonConfig.loadRateClassMap();
			for (RateClass rc : jsonCfgMap.values()) {
				String className = rc.getClassName();
				Map<String, RateMethod> methodMap = rc.getMethodMap();
				for (RateMethod rm : methodMap.values()) {
					if (StringUtils.isNotBlank(rm.getMethodName()))
						// 具体到类中的方法
						rateLimitSupport.loadRateLimiter(className + "." + rm.getMethodName(), rm.getQps(), rm.getWarmupPeriod());
				}
			}
		}
	}

	/**
	 * 配置了方法的校验方法，否则校验类
	 *
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		String clazz = pjp.getTarget().getClass().getName();
		String methodName = pjp.getSignature().getName();
		String methodHandle = methodName;
		String fullMethodName = clazz + "." + methodHandle;

		boolean ticketFlag = true;

		RateLimiter clazzRL = rateLimitSupport.getRateLimiter(fullMethodName);
		if (null == clazzRL) {
			methodHandle = "default";
			fullMethodName = clazz + "." + methodHandle;
			clazzRL = rateLimitSupport.getRateLimiter(fullMethodName);
		}

		if (clazzRL != null) {
			RateMethod method = jsonCfgMap.get(clazz).getMethodMap().get(methodHandle);
			long beginTime = System.nanoTime();
			switch (method.getTicketType()) {
				case 0:
					ticketFlag = clazzRL.tryAcquire();
					if (!ticketFlag) {
						LOGGER.debug("key:{}, method:{}, cost time:{}", fullMethodName, clazz + "." + methodName, (System.nanoTime() - beginTime));
						throw new RateLimitException("reject the request that QPS is too high, can not get the token.");
					}
					break;
				case 1:
					ticketFlag = clazzRL.tryAcquire(Constants.PERMITS_EACH_TIME, method.getTimeout(), TimeUnit.MILLISECONDS);
					if (!ticketFlag) {
						LOGGER.debug("key:{}, method:{}, timeOut:{}, cost time:{}", fullMethodName, clazz + "." + methodName, method.getTimeout(), (System.nanoTime() - beginTime));
						throw new RateLimitException("reject the request that QPS is too high, can not get the token.");
					}
					break;
				case 2:
					double waitTime = clazzRL.acquire();
					LOGGER.debug("key:{}, method:{}, waitTime:{},cost time:{}", fullMethodName, clazz + "." + methodName, waitTime, (System.nanoTime() - beginTime));
					break;
				default:
					break;
			}
		}
		try {
			Object result = pjp.proceed();
			return result;
		} catch (Throwable t) {
			throw t;
		}
	}
}
