package com.zxt.datacollector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.python.util.PythonInterpreter;



/**
 * @author 赵笑天
 *
 * @time 2016年2月19日
 * 
 */
public class KhanDataCollector {
	
	@SuppressWarnings("deprecation")
	private DefaultHttpClient httpClient = null;
	private CollectedData collectedData = new CollectedData();
	public static int MAX_NUM = 10000000;
	
	public CollectedData getCollectedData() {
		return collectedData;
	}

	public void setCollectedData(CollectedData collectedData) {
		this.collectedData = collectedData;
	}
	/*构造函数，支持gzip压缩格式*/
	@SuppressWarnings("deprecation")
	public KhanDataCollector(){
		httpClient = new DefaultHttpClient();
		httpClient.addRequestInterceptor(new HttpRequestInterceptorImplementation());
		
		httpClient.addResponseInterceptor(new HttpResponseInterceptor(){
			public void process(HttpResponse response, HttpContext context)
					throws HttpException, IOException {
				
				HttpEntity entity = response.getEntity();  
	               Header ceheader = entity.getContentEncoding();  
	               if (ceheader != null) {  
	                   HeaderElement[] codecs = ceheader.getElements();  
	                   for (int i = 0; i < codecs.length; i++) {  
	                       if (codecs[i].getName().equalsIgnoreCase("gzip")) {  
	                           response.setEntity(  
	                                   new GzipDecompressingEntity(response.getEntity()));   
	                           return;  
	                       }  
	                   }  
	               }  
			}
			
		});
	}
	
	/*加载要采集数据的url*/
	public List<String> loadUrls (String filePath){
		List<String> urls = new ArrayList<String>();
		File file = new File(filePath);
		BufferedReader reader = null;
		if (file != null){
			try {
				reader = new BufferedReader(new FileReader(file));
				String tmpString = null;
				while ((tmpString = reader.readLine()) != null){
					urls.add(tmpString);
				}
			} catch (IOException e) {
				System.out.println("文件读取失败");
				e.printStackTrace();
			}finally{
				if (reader != null){
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return urls;
	}
	
	/*下载页面*/
	public String downloadPage(String url) throws Exception{
		String pageSourceCode = null;
		HttpGet getMethod = new HttpGet(url);
		HttpResponse response = httpClient.execute(getMethod);
		HttpEntity entity = response.getEntity();
		pageSourceCode = EntityUtils.toString(entity);
		return pageSourceCode;
		
	}
	
	/*测试时候存储下载页面*/
	public void downloadPageToFile(String url,String filePath) throws Exception{
		String pageSourceCode = downloadPage(url);
		File targetFile = new File(filePath);
		if (!targetFile.exists()){
			targetFile.createNewFile();
		}
		FileWriter fw = new FileWriter(targetFile);
		fw.write(pageSourceCode);
		
		if (fw != null){
			fw.close();
		}		
	}
	
	/*将数据写入到文件*/
	public void saveData(String content,String filePath) throws IOException{
		File targetFile = new File(filePath);
		if (!targetFile.exists()){
			targetFile.createNewFile();
		}
		FileWriter fw = new FileWriter(targetFile);
		fw.write(content);
		
		if (fw != null){
			fw.close();
		}
	}
	
	/*保存Khan网站抓取下来的数据*/
	public void saveKhanData(CollectedData cd,String filePath) throws IOException{
		saveData(cd.toString(), filePath);
	}
	
	/*解析用户信息*/
	public void extractUserInfo(String pageSourceCode){
		
		//切取所需的json数据
		String profileData = pageSourceCode.substring(pageSourceCode.indexOf("Profile.init")+13, pageSourceCode.indexOf("KA.reportData")-2);
		try {
			JSONObject json = new JSONObject(profileData);
			
			//解析username
			String username = (String) ((JSONObject)json.get("profileData")).get("username");
			if ((username != null) && (!username.trim().equals("")) && (!username.toLowerCase().equals("null"))){
				this.collectedData.setUsername(username);
			}else{
				//如果用户名不存在，则用nickname代替username
				System.out.println("Use nickname!!!");
				username = (String) ((JSONObject)json.get("profileData")).get("nickname");
				this.collectedData.setUsername(username);
			}
			
			//解析bio
			String bio = (String) ((JSONObject)json.get("profileData")).get("bio");
			if ((bio != null) && (!bio.trim().equals("")) && (!bio.toLowerCase().equals("null"))){
				this.collectedData.setBio(bio);
			}else{
				this.collectedData.setBio("Null");
			}
			
			//解析userLocation
			String userLocation = ((JSONObject)json.get("profileData")).getString("userLocation");
			
			if ((userLocation != null) && (!userLocation.trim().equals("")) && (!userLocation.toLowerCase().equals("null"))){
				JSONObject userLocationJson = new JSONObject(userLocation);
				if (!userLocationJson.getString("displayText").trim().equals("")){
					this.collectedData.setUserLocation(userLocationJson.getString("displayText"));
				}else{
					this.collectedData.setUserLocation("Null");
				}
				
			}else{
				this.collectedData.setUserLocation("Null");
			}
			
			//解析dateJoined
			String dateJoined =  ((JSONObject)json.get("profileData")).getString("dateJoined");
			if ((dateJoined != null) && (!dateJoined.trim().equals("")) && (!dateJoined.toLowerCase().equals("null"))){
				this.collectedData.setDateJoined(dateJoined);
			}else{
				this.collectedData.setDateJoined("Null");
			}
			
			//解析energyPointsEarned
			String energyPointsEarned = ((JSONObject)json.get("profileData")).getString("points");
			if ((energyPointsEarned != null) && (!energyPointsEarned.trim().equals("")) && (!energyPointsEarned.toLowerCase().equals("null"))){
				this.collectedData.setEnergyPointsEarned(Integer.parseInt(energyPointsEarned.trim()));
			}else{
				this.collectedData.setEnergyPointsEarned(-1);
			}
			
			//解析countVideosCompleted
			String countVideosCompleted = (String) ((JSONObject)json.get("profileData")).getString("countVideosCompleted");
			if ((countVideosCompleted != null) && (!countVideosCompleted.trim().equals("")) && (!countVideosCompleted.toLowerCase().equals("null"))){
				this.collectedData.setVideosCompleted(Integer.parseInt(countVideosCompleted.trim()));
			}else{
				this.collectedData.setVideosCompleted(-1);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/*解析Discussion面板信息*/
	public void extractDiscussionPanelInfo(String discussionAjaxSourceCode) throws JSONException, ParseException{
		//删除掉头和尾的方括号
		
		JSONArray jsonArray = new JSONArray(discussionAjaxSourceCode);
		
		for (int i = 0 ; i < jsonArray.length(); i++ ){
			if (((JSONObject)jsonArray.get(i)).getString("widgetId").equals("DiscussionWidget")){
				
				JSONObject json = jsonArray.getJSONObject(i);
				
				/*获取statistics数据*/
				String statisticsStr = ((JSONObject)((JSONObject)json.get("renderData")).get("discussionData")).getString("statistics");
				if (statisticsStr != null && !statisticsStr.trim().equals("") && !statisticsStr.toLowerCase().equals("null")){
					JSONObject statisticsJson = new JSONObject(statisticsStr);
					
					this.collectedData.setQuestions(statisticsJson.getInt("questions"));
					this.collectedData.setVotes(statisticsJson.getInt("votes"));
					this.collectedData.setAnswers(statisticsJson.getInt("answers"));
					this.collectedData.setFlagsRaised(statisticsJson.getInt("flags"));
					this.collectedData.setProjectHelpRequests(statisticsJson.getInt("projectquestions"));
					this.collectedData.setProjectHelpReplies(statisticsJson.getInt("projectanswers"));
					this.collectedData.setComments(statisticsJson.getInt("replies"));
					this.collectedData.setTipsAndThanks(statisticsJson.getInt("comments"));
				}
			}
			
			if (((JSONObject)jsonArray.get(i)).getString("widgetId").equals("StreakWidget")){
				JSONObject jsonStreak = jsonArray.getJSONObject(i);
				
				/*获取streak数据*/
				String streakHistoryStr = ((JSONObject)((JSONObject)((JSONObject)jsonStreak.get("renderData")).get("streakWidgetData")).get("streakData")).getString("history");
				
				String regex = "\\d{4}-\\d{2}-\\d{2}";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(streakHistoryStr);
				//记录每一组数据的开头时间和结束时间，当为偶数的时候是开始时间，为奇数是结束时间
				List<Date> startDate = new ArrayList<Date>();
				List<Date> endDate = new ArrayList<Date>();
				int count = 0;
				while (matcher.find()){
					
					if (count % 2 == 0){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						Date date = sdf.parse(matcher.group());
						startDate.add(date);
					}else{
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						Date date = sdf.parse(matcher.group());
						endDate.add(date);
					}
					
					count++;
				}
				
				if (count == 0 ){
					this.collectedData.setLastStreak(0);
					this.collectedData.setLongestStreak(0);
				}else{
					int longestStreak = -1;
					for (int z = 0 ; z < startDate.size() ; ++z){
						int streak = dayBetween(startDate.get(z),endDate.get(z));
						if (streak > longestStreak){
							longestStreak = streak;
						}
					}
					
					this.collectedData.setLongestStreak(longestStreak);
					this.collectedData.setLastStreak(dayBetween(startDate.get(startDate.size()-1), endDate.get(endDate.size()-1)));
				}
				
			}
		}

	}
	
	private int dayBetween(Date smdate, Date bdate) throws ParseException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
        return Integer.parseInt(String.valueOf(between_days))+1;
	}

	/*解析badges数据*/
	public void extractBadges(String summaryAjaxSourceCode) {
		try {
			JSONObject json = new JSONObject(summaryAjaxSourceCode);
			String badgesStr = json.getString("badges");
			
			JSONArray jsonArray = new JSONArray(badgesStr);
			if (jsonArray != null && jsonArray.length() > 0){
				List<Badge> badgeList = new ArrayList<Badge>();
				for (int i = 0 ; i < jsonArray.length() ; ++i ){
					JSONObject badgeJson = jsonArray.getJSONObject(i);
					Badge badge = new Badge();
					badge.setBadgeName(badgeJson.getString("translatedName"));
					badge.setBadgeNote(badgeJson.getString("translatedDescription"));
					badge.setCount(badgeJson.getInt("count"));
					
					badgeList.add(badge);
				}
				
				this.collectedData.setBadges(badgeList);
			}else{
				this.collectedData.setBadges(null);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/*解析projects数据*/
	public void extractProjects(String projectAjaxSourceCode) {
		try {
			JSONObject jsonObject = new JSONObject(projectAjaxSourceCode);
			String projectsStr = jsonObject.getString("scratchpads");
			
			JSONArray jsonArray = new JSONArray(projectsStr);
			if (jsonArray != null && jsonArray.length() > 0){
				List<Project> projectList = new ArrayList<Project>();
				
				for (int i = 0 ; i < jsonArray.length() ; ++i){
					Project project = new Project();
					JSONObject projectJson = jsonArray.getJSONObject(i);
					project.setProjectName(projectJson.getString("title"));
					project.setSpinOffs(projectJson.getInt("spinoffCount"));
					project.setVotes(projectJson.getInt("sumVotesIncremented"));
					projectList.add(project);
				}
				
				this.collectedData.setProjects(projectList);
			}else{
				this.collectedData.setProjects(null);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*支持gzip格式所需的私有类*/
	private class HttpRequestInterceptorImplementation implements HttpRequestInterceptor {
		public void process(HttpRequest request, HttpContext context)
				throws HttpException, IOException {
			if (!request.containsHeader("Accept-Encoding")) {  
		           request.addHeader("Accept-Encoding", "gzip");  
		       }  
			
		}
	}
	
	/*主函数*/
	public static void main(String[] args) throws Exception{
		KhanDataCollector kdc = new KhanDataCollector();
		//加载urls
		List<String> urls = kdc.loadUrls("src/urls.txt");
		if (urls != null && urls.size() != 0){
			for(int i = 0 ; i < urls.size() ; ++i){
				
				/*解析用户信息*/
				System.out.println("Downloading UserInfo Page:"+urls.get(i));
				String pageSourceCode = kdc.downloadPage(urls.get(i));
				kdc.extractUserInfo(pageSourceCode);
				
				/*解析Discussion面板中的信息*/
				
				long timeStamp = new Date().getTime();
				String discussionAjaxUrl = "https://www.khanacademy.org/api/internal/user/"+urls.get(i).substring(urls.get(i).lastIndexOf('/')+1)+"/profile/widgets?lang=en&_="+timeStamp;
				System.out.println("Downloading DiscussionPanel :" + discussionAjaxUrl);
				String discussionAjaxSourceCode = kdc.downloadPage(discussionAjaxUrl);
				kdc.extractDiscussionPanelInfo(discussionAjaxSourceCode);
				
				/*解析summary里面的内容*/
				String summaryAjaxUrl = "https://www.khanacademy.org/api/internal/user/discussion/summary?kaid="+urls.get(i).substring(urls.get(i).lastIndexOf('/')+1)+"&lang=en&_="+timeStamp;
				System.out.println("Downloading summaryAjaxSourceCode :" + summaryAjaxUrl);
				String summaryAjaxSourceCode = kdc.downloadPage(summaryAjaxUrl);
				kdc.extractBadges(summaryAjaxSourceCode);
				
				/*解析project内容*/
				String projectAjaxUrl = "https://www.khanacademy.org/api/internal/user/scratchpads?casing=camel&kaid="+urls.get(i).substring(urls.get(i).lastIndexOf('/')+1)+"&sort=1&subject=all&limit="+KhanDataCollector.MAX_NUM+"&page=0&lang=en&_="+timeStamp;
				System.out.println("Downloading projectAjaxSourceCode:"+projectAjaxUrl);
				String projectAjaxSourceCode = kdc.downloadPage(projectAjaxUrl);
				kdc.extractProjects(projectAjaxSourceCode);
				
				System.out.println(kdc.getCollectedData());
				kdc.saveKhanData(kdc.getCollectedData(), "E:\\khanData\\"+urls.get(i).substring(urls.get(i).lastIndexOf('/')+1)+".txt");
			}
		}

	}
	
	

}
