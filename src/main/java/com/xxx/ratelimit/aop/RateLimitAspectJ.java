package com.xxx.ratelimit.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;

public abstract class RateLimitAspectJ implements Ordered, InitializingBean {

	public abstract Object around(ProceedingJoinPoint pjp) throws Throwable;

	// 保证高优先级
	@Override
	public int getOrder() {
		return -1;
	}

	// 预加载配置
	@Override
	public abstract void afterPropertiesSet() throws Exception;
}
