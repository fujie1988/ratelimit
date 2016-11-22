package com.xxx.ratelimit.aop.impl;


import com.alibaba.fastjson.JSON;
import com.xxx.ratelimit.aop.RateLimit;
import com.xxx.ratelimit.aop.RateLimitAspectJ;
import com.xxx.ratelimit.exception.RateLimitException;
import com.xxx.ratelimit.support.RateLimitSupport;
import com.xxx.ratelimit.support.constant.Constants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


/**
 * 不添加自动注入的注解，用户手动添加，可以使用户自定义该类
 */
@Aspect
//@Component
public class AnnotationRateLimitAspectJ extends RateLimitAspectJ {

	@Resource
	private RateLimitSupport rateLimitSupport;

	private static final String executionURL = "@annotation(RateLimit)";

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Around(executionURL)
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		String clazz = pjp.getTarget().getClass().getName();
		String methodName = pjp.getSignature().getName();
		Object[] args = pjp.getArgs();
		String key = this.generateKey(clazz, methodName, args);
		LOGGER.debug("AnnotationRateLimitAspectJ.around method={},param={}", clazz + "." + methodName, JSON.toJSONString(args));

		RateLimit rateLimit = ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(RateLimit.class);

		double qps = rateLimit.qps();
		long timeout = rateLimit.timeout();
		long warmupPeriod = rateLimit.warmupPeriod();
		long beginTime = System.nanoTime();
		if (timeout < 0) {
			double waitTime = rateLimitSupport.acquire(key, qps, warmupPeriod, Constants.PERMITS_EACH_TIME);
			LOGGER.debug("key={},sleepTime={},cost time={}", key, waitTime, System.nanoTime() - beginTime);
		} else {
			boolean permit = rateLimitSupport.tryAcquire(key, qps, warmupPeriod, Constants.PERMITS_EACH_TIME, timeout, TimeUnit.MILLISECONDS);
			LOGGER.debug("key={},qps={},timeout={},permit={},cost time={}", key, qps, timeout, permit, System.nanoTime() - beginTime);
			if (!permit)
				throw new RateLimitException("reject the request that QPS is too high, can not get the ticket.");
		}
		try {
			Object result = pjp.proceed();
			return result;
		} catch (Throwable t) {
			throw t;
		}

	}

	/**
	 * 生成限流的key。用className、methodName和args长度作为key的因子
	 */
	private String generateKey(String clazz, String methodName, Object[] args) {
		int length = Constants.DEFAULT_INTEGER;
		if (null != args) {
			length = args.length;
		}
		String key = clazz + "." + methodName + '.' + length;
		return key;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// nothing to do
		LOGGER.debug("init bean {}",this.getClass().getName());
	}
}