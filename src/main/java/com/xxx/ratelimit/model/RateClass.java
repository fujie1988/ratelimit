package com.xxx.ratelimit.model;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xxx.ratelimit.model.enums.TicketTypeEnum;
import com.xxx.ratelimit.support.constant.Constants;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class RateClass implements Serializable {
	private static final long serialVersionUID = 1L;

	private String className;

	private List<RateMethod> methods = Lists.newArrayList();

	private Map<String, RateMethod> methodMap = Maps.newHashMap();

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<RateMethod> getMethods() {
		return methods;
	}

	public void setMethods(List<RateMethod> methods) {
		this.methods = methods;
	}

	public Map<String, RateMethod> getMethodMap() {
		return methodMap;
	}

	public void setMethodMap(Map<String, RateMethod> methodMap) {
		this.methodMap = methodMap;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
