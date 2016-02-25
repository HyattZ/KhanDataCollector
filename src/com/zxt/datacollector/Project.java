package com.zxt.datacollector;

/**
 * @author 赵笑天
 *
 * @time 2016年2月20日
 * 
 */
public class Project {
	private String projectName;
	private int votes;
	private int spinOffs;
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public int getVotes() {
		return votes;
	}
	public void setVotes(int votes) {
		this.votes = votes;
	}
	public int getSpinOffs() {
		return spinOffs;
	}
	public void setSpinOffs(int spinOffs) {
		this.spinOffs = spinOffs;
	}
	@Override
	public String toString() {
		return "Project [projectName=" + projectName + ", votes=" + votes
				+ ", spinOffs=" + spinOffs + "]";
	}
	
}
