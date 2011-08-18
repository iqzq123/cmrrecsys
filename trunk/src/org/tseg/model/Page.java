package org.tseg.model;

public class Page {

	private String pageName;
	private int duration;
	private int clickNum;
	private int userNum;

	public int getValue(String property){
		if (property.equals("userNum")) {
		
			return this.userNum;
		}
		if (property.equals("clickNum")) {
			return this.clickNum;
		}
		if (property.equals("duration")) {
			return this.duration;
		}
		return -1;
	}
	public boolean isMoreThan(Page p, String property) {
		if (property.equals("userNum")) {
			if (this.userNum < p.getUserNum()) {
				return false;
			} else {
				return true;
			}
		}
		if (property.equals("clickNum")) {
			if (this.clickNum < p.getClickNum()) {
				return false;
			} else {
				return true;
			}
		}
		if (property.equals("duration")) {
			if (this.duration < p.getDuration()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		String retS = pageName;// +"("+clickNum+","+duration+")";
		return retS;
	}

	public Page() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getPageName() {
		return pageName;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}

	public void setClickNum(int clickNum) {
		this.clickNum = clickNum;
	}

	public int getClickNum() {
		return clickNum;
	}

	public int getUserNum() {
		return userNum;
	}

	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}

}
