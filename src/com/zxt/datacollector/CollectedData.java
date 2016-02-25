package com.zxt.datacollector;

import java.util.List;

/**
 * @author 赵笑天
 *
 * @time 2016年2月20日
 * 
 */
public class CollectedData {
	/*User Info*/
	private String username;
	private String bio;
	private String userLocation;
	
	/*User Statistics*/
	private String dateJoined;
	private int energyPointsEarned;
	private int videosCompleted;
	
	/*Statistics*/
	private int questions;
	private int votes;
	private int answers;
	private int flagsRaised;
	private int projectHelpRequests;
	private int projectHelpReplies;
	private int comments;
	private int tipsAndThanks;
	
	/*Streak*/
	private int lastStreak;
	private int longestStreak;
	
	/*Badges*/
	private List<Badge> badges;
	
	/*Projects*/
	private List<Project> projects;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}

	public String getDateJoined() {
		return dateJoined;
	}

	public void setDateJoined(String dateJoined) {
		this.dateJoined = dateJoined;
	}

	public int getEnergyPointsEarned() {
		return energyPointsEarned;
	}

	public void setEnergyPointsEarned(int energyPointsEarned) {
		this.energyPointsEarned = energyPointsEarned;
	}

	public int getVideosCompleted() {
		return videosCompleted;
	}

	public void setVideosCompleted(int videosCompleted) {
		this.videosCompleted = videosCompleted;
	}

	public int getQuestions() {
		return questions;
	}

	public void setQuestions(int questions) {
		this.questions = questions;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public int getAnswers() {
		return answers;
	}

	public void setAnswers(int answers) {
		this.answers = answers;
	}

	public int getFlagsRaised() {
		return flagsRaised;
	}

	public void setFlagsRaised(int flagsRaised) {
		this.flagsRaised = flagsRaised;
	}

	public int getProjectHelpRequests() {
		return projectHelpRequests;
	}

	public void setProjectHelpRequests(int projectHelpRequests) {
		this.projectHelpRequests = projectHelpRequests;
	}

	public int getProjectHelpReplies() {
		return projectHelpReplies;
	}

	public void setProjectHelpReplies(int projectHelpReplies) {
		this.projectHelpReplies = projectHelpReplies;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public int getTipsAndThanks() {
		return tipsAndThanks;
	}

	public void setTipsAndThanks(int tipsAndThanks) {
		this.tipsAndThanks = tipsAndThanks;
	}

	public int getLastStreak() {
		return lastStreak;
	}

	public void setLastStreak(int lastStreak) {
		this.lastStreak = lastStreak;
	}

	public int getLongestStreak() {
		return longestStreak;
	}

	public void setLongestStreak(int longestStreak) {
		this.longestStreak = longestStreak;
	}

	public List<Badge> getBadges() {
		return badges;
	}

	public void setBadges(List<Badge> badges) {
		this.badges = badges;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	@Override
	public String toString() {
		return "CollectedData [username=" + username + ", bio=" + bio
				+ ", userLocation=" + userLocation + ", dateJoined="
				+ dateJoined + ", energyPointsEarned=" + energyPointsEarned
				+ ", videosCompleted=" + videosCompleted + ", questions="
				+ questions + ", votes=" + votes + ", answers=" + answers 
				+ ", flagsRaised=" + flagsRaised + ", projectHelpRequests="
				+ projectHelpRequests + ", projectHelpReplies="
				+ projectHelpReplies + ", comments=" + comments
				+ ", tipsAndThanks=" + tipsAndThanks + ", lastStreak="
				+ lastStreak + ", longestStreak=" + longestStreak + ", badges="
				+ badges + ", projects=" + projects + "]";
	}
	
}
