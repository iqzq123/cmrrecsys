package org.tseg.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PVHistory {

	private long id = 0;
	private HashMap<String, List<Visit>> sessionMap = new HashMap<String, List<Visit>>();
	private int pvNum=0;

	
	public void addLog(String[] strArray){
		
		this.pvNum++;
		Visit v = new Visit(strArray);
		String sessionID = strArray[11];
		List<Visit> list = this.getSessionMap().get(sessionID);
		if (list != null) {
			list.add(v);
			this.getSessionMap().put(sessionID, list);
		} else {
			List<Visit> l = new ArrayList<Visit>();
			l.add(v);
			this.getSessionMap().put(sessionID, l);
		}
		
	}
	public void clean(){
		this.id=0;
		this.sessionMap.clear();
	}
	
	
	public int getDuration(){
		
		int time=0;
		Iterator iter = sessionMap.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			//String key = (String) entry.getKey();
			List<Visit> val = (List<Visit>) entry.getValue();
			for(Visit v:val){
				time+=v.getDruation();
			}
			
		}
		return time;
		
		
	}

	public List<String> getPathString() {
		
		if(id==Long.parseLong("13400427971")){
			
			int aa=0;
			aa++;
					}

		Iterator iter = sessionMap.entrySet().iterator();
		List<String> retList = new ArrayList<String>();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			if(key.equals("13400427971")){
				int a=1;
				a++;
			}
			List<Visit> list = (List<Visit>) entry.getValue();
			String s = list.get(0).getPrePage() + ",";
			String prePage="";
			for (Visit v : list) {
				if(!v.getCurPage().equals(prePage)){
					s += v.getCurPage()+",";
					prePage=v.getCurPage();
				}else{
					int a=0;
				}
				
			}
			s += list.get(list.size() - 1).getNextPage()+",";
			retList.add(s);
		}

		return retList;
	}

	public String toString() {
		
		StringBuffer strBuf=new StringBuffer(1000);
		String s = "#" + id + "\n";
		strBuf.append(s);
		Iterator iter = sessionMap.entrySet().iterator();

		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			List<Visit> val = (List<Visit>) entry.getValue();
			strBuf.append(key + "\t");
			strBuf.append( val.get(0).getPrePage() + ",");
			for (Visit v : val) {
				strBuf.append(v.toString() + ",");

			}
			strBuf.append( val.get(val.size() - 1).getNextPage() + ",");
			strBuf.append("\n") ;

		}

		return strBuf.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setSessionMap(HashMap<String, List<Visit>> sessionTable) {
		this.sessionMap = sessionTable;
	}

	public HashMap<String, List<Visit>> getSessionMap() {
		return sessionMap;
	}


	public int getPvNum() {
		
		return pvNum;
	}


	public void setPvNum(int pvNum) {
		this.pvNum = pvNum;
	}

}
