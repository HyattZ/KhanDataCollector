package com.zxt.datacollector;

/**
 * @author 赵笑天
 *
 * @time 2016年2月20日
 * 
 */
public class Badge {
	private String badgeName;
	private String badgeNote;
	private int count;
	
	public String getBadgeName() {
		return badgeName;
	}
	public void setBadgeName(String badgeName) {
		this.badgeName = badgeName;
	}
	public String getBadgeNote() {
		return badgeNote;
	}
	public void setBadgeNote(String badgeNote) {
		this.badgeNote = badgeNote;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "Badge [badgeName=" + badgeName + ", badgeNote=" + badgeNote
				+ ", count=" + count + "]";
	}
}
