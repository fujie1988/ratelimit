package com.xxx.ratelimit.model;

import com.alibaba.fastjson.JSON;
import com.xxx.ratelimit.model.enums.TicketTypeEnum;
import com.xxx.ratelimit.support.constant.Constants;

import java.io.Serializable;

public class RateMethod implements Serializable {

	private static final long serialVersionUID = 1L;

	private String methodName;
	private double qps = 1000.0;

	/**
	 * 获取ticket的方式
	 * 0:尝试获取，获取不到立即失败
	 * 1:根据最长等待timeOut获取，获取不到返回失败
	 * 2:阻塞等待获取
	 */
	private int ticketType = TicketTypeEnum.TRY_ACQUIRE.getType();
	private long timeout = 0;
	private long warmupPeriod = Constants.NONE_WARMUP_PERIOD;

	public long getWarmupPeriod() {
		return warmupPeriod;
	}

	public void setWarmupPeriod(long warmupPeriod) {
		this.warmupPeriod = warmupPeriod;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public int getTicketType() {
		return ticketType;
	}

	public void setTicketType(int ticketType) {
		this.ticketType = ticketType;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public double getQps() {
		return qps;
	}

	public void setQps(double qps) {
		this.qps = qps;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
