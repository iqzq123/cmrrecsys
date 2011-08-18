package org.tseg.model.book;

//BOOKID,CHAPTERID,CHAPTERNAME,CHAPTERTYPE,CHAPTERRANK,WORD_CNT,CHAPTERFEE,
public class Chapter {
	private int bookId;
	private int id;
	private String name;
	private int type;
	private int rank;
	private int wordCnt;
	private int fee;
	private int userNum = 0;
	
	public Chapter(int bookId, int id, String name, int type, int rank,
			int wordCnt, int fee) {
		super();
		this.bookId = bookId;
		this.id = id;
		this.name = name;
		this.type = type;
		this.rank = rank;
		this.wordCnt = wordCnt;
		this.fee = fee;
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getWordCnt() {
		return wordCnt;
	}
	public void setWordCnt(int wordCnt) {
		this.wordCnt = wordCnt;
	}
	public int getFee() {
		return fee;
	}
	public void setFee(int fee) {
		this.fee = fee;
	}
	public int getUserNum() {
		return userNum;
	}
	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}
	

}
