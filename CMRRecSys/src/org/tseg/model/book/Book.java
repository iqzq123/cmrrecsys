package org.tseg.model.book;

import java.util.ArrayList;
import java.util.HashMap;

public class Book {
	private int id;
	private String name;
	private int authorId;
	private String mcpId;
	private int typeId;
	private int classId;
	private int chargeType;
	private int totalFee;
	private int contentStatus;
	private int onlineTime;
	private int offlineTime;
	private int serialize;
	private int publishType;
	private int wordCnt;
	private String format;
	private HashMap<Integer, Chapter> chapterMap = new HashMap<Integer, Chapter>();

	public Book(){
		
	}
	public Book(int id, String name, int authorId, String mcpId, int typeId,
			int classId, int chargeType, int totalFee, int contentStatus,
			int onlineTime, int offlineTime, int serialize, int publishType,
			int wordCnt, String format) {
		super();
		this.id = id;
		this.name = name;
		this.authorId = authorId;
		this.mcpId = mcpId;
		this.typeId = typeId;
		this.classId = classId;
		this.chargeType = chargeType;
		this.totalFee = totalFee;
		this.contentStatus = contentStatus;
		this.onlineTime = onlineTime;
		this.offlineTime = offlineTime;
		this.serialize = serialize;
		this.publishType = publishType;
		this.wordCnt = wordCnt;
		this.format = format;
	}

	public void addChapter(Chapter chapter) {
		this.chapterMap.put(chapter.getId(), chapter);
	}
	
	public boolean hasChapter(int chapterId) {
		if ( this.chapterMap.get(chapterId) == null )
			return false;
		else
			return true;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getAuthorId() {
		return authorId;
	}
	
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	
	public String getMcpId() {
		return mcpId;
	}
	
	public void setMcpId(String mcpId) {
		this.mcpId = mcpId;
	}
	
	public int getTypeId() {
		return typeId;
	}
	
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	
	public int getClassId() {
		return classId;
	}
	
	public void setClassId(int classId) {
		this.classId = classId;
	}
	
	public int getChargeType() {
		return chargeType;
	}
	
	public void setChargeType(int chargeType) {
		this.chargeType = chargeType;
	}
	
	public int getTotalFee() {
		return totalFee;
	}
	
	public void setTotalFee(int totalFee) {
		this.totalFee = totalFee;
	}
	
	public int getContentStatus() {
		return contentStatus;
	}
	
	public void setContentStatus(int contentStatus) {
		this.contentStatus = contentStatus;
	}
	
	public int getOnlineTime() {
		return onlineTime;
	}
	
	public void setOnlineTime(int onlineTime) {
		this.onlineTime = onlineTime;
	}
	
	public int getOfflineTime() {
		return offlineTime;
	}
	
	public void setOfflineTime(int offlineTime) {
		this.offlineTime = offlineTime;
	}
	
	public int getSerialize() {
		return serialize;
	}
	
	public void setSerialize(int serialize) {
		this.serialize = serialize;
	}
	
	public int getPublishType() {
		return publishType;
	}
	public void setPublishType(int publishType) {
		this.publishType = publishType;
	}
	
	public int getWordCnt() {
		return wordCnt;
	}
	
	public void setWordCnt(int wordCnt) {
		this.wordCnt = wordCnt;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}

	public HashMap<Integer, Chapter> getChapterMap() {
		return chapterMap;
	}

	public void setChapterMap(HashMap<Integer, Chapter> chapterMap) {
		this.chapterMap = chapterMap;
	}
	
}
