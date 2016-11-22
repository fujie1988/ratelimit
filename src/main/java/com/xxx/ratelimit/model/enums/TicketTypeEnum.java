package com.xxx.ratelimit.model.enums;

/**
 * Created by hk on 2016/11/22.
 */
public enum TicketTypeEnum {
	TRY_ACQUIRE(0),
	TRY_ACQUIRE_TIME_OUT(1),
	ACQUIRE(2);

	private int type;

	private TicketTypeEnum(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}