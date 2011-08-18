package org.tseg.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public class FPAlgorithm {
	
	private List<String> pathList=new ArrayList<String>();
	private HashMap<String,Integer> curCandiPathMap=new HashMap<String,Integer>();
	private List<HashMap<String,Integer>> fpMapList=new ArrayList<HashMap<String,Integer>>();
	private int maxFPLenght=10;
	private double minRatio=0.03;
	private double decayRatio=0.8;
	private int curFPLenght=1;
	private int pathNum=0;
	

	public FPAlgorithm() {
		super();
		// TODO Auto-generated constructor stub
		fpMapList.add(new HashMap<String,Integer>());
		
	}

	public void run(){
		
		for(int i=1;i<=this.maxFPLenght;i++){
			this.curCandiPathMap=new HashMap<String,Integer>();
			this.curFPLenght=i;
			for(String path:pathList){
				List<String> pList=new ArrayList<String>();
				getCandiPath(path.split(","),0,null,i,pList);
				countPath(pList);
				this.increasePathNum();
			}
			removeNoFrePath();
			if(this.curCandiPathMap.size()==0){
				break;
			}
			fpMapList.add(i,this.curCandiPathMap);
		}
		
	}

	public void increasePathNum(){
		this.pathNum++;
	}
	public void removeNoFrePath(){
		
	
		
		double ratio=this.minRatio*Math.pow(this.decayRatio, this.curFPLenght-1);
		int minCnt=(int)(this.pathNum*ratio);
		System.out.println("minCnt......................."+minCnt);
		Iterator iter=this.curCandiPathMap.entrySet().iterator();
		List<String> removeKeys=new ArrayList<String>();
		while(iter.hasNext()){
			Map.Entry entry=(Map.Entry)iter.next();
			String key=(String)entry.getKey();
			Integer cnt=(Integer)entry.getValue();
			if(cnt<minCnt){
				removeKeys.add(key);
			}
		}
		for(String key:removeKeys){
			this.curCandiPathMap.remove(key);
		}
	}
	
	public void countPath(List<String> pList){
		 
		for(String path:pList){
			Integer cnt=this.curCandiPathMap.get(path);
			if(cnt==null){
				this.curCandiPathMap.put(path, 1);
			}else{
				cnt++;
				this.curCandiPathMap.put(path, cnt);
			}
		}
	}
	/**
	 * @param nodeArray 路径的存放数组
	 * @param startIndex 指定后续子路径的起始点
	 * @param prefix 子路径的前缀
	 * @param len 候选路径的长度
	 * @param list 存放候选路径的list
	 * 产生一个路径的候选路径的算法
	 */
	public void getCandiPath(String []nodeArray,int startIndex,String prefix,int len,List<String> list){
		
	
	
		if(nodeArray.length-startIndex<len||len<=0){
			return;
		}
		if(nodeArray.length-startIndex==len){
			String s="";
			for(int i=startIndex;i<nodeArray.length;i++){
				s+=nodeArray[i]+",";
			}
			s=s.substring(0, s.length()-1);
			list.add(s);
			return;
		}
		if(prefix!=null){
			int prelen=prefix.split(",").length;
			if(this.fpMapList.get(prelen).get(prefix)==null){
				return;
			}
		}
		for(int i=startIndex;i<=nodeArray.length-len;i++){
		
//			if(i>=nodeArray.length){
//				break;
//			}
			String s=nodeArray[i];
			String nextPrefix="";
			if(prefix==null){
				nextPrefix=s;
			}else{
				nextPrefix=prefix+","+s;
			}
			List<String> suffixPaths=new ArrayList<String>();
		
			getCandiPath(nodeArray,i+1,nextPrefix,len-1,suffixPaths);
			for(String sP:suffixPaths){
				String p=s+","+sP;
				boolean isSame2Next=s.equals(sP.split(",")[0]);
				/////////////////serious error fix
				if(!list.contains(p)&&!isSame2Next){
					list.add(p);
				}
				
			}
			if(suffixPaths.size()==0&&len==1){
				if(!list.contains(s)){
					list.add(s);
				}
				
			}
			

		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		List<String> list = new ArrayList<String>();
		list.add("a,b,c,d,e");
		list.add("a,b,a,c,f,g,h");
		list.add("d,e,g");
		list.add("e,g,h");
		list.add("a,d,t,s,g");
		list.add("a,b,c,d,e");
		list.add("d,e,g");
		list.add("d,c,g,e,h");
		list.add("b,d,e,f,g,h,i");
		list.add("a,c,d,f,g");
		list.add("a,b,c,e,g");
		list.add("b,c,d");
		list.add("a,b,c,d,f");
		
		FPAlgorithm fp=new FPAlgorithm();
		fp.setPathList(list);
		fp.run();
		for(HashMap map:fp.getFpMapList()){
			System.out.println("........................................");
			System.out.println(map.toString()+"\n");
			
		}
		System.out.println("success");
	}

	public List<String> getPathList() {
		return pathList;
	}

	public void setPathList(List<String> pathList) {
		this.pathList = pathList;
	}

	public int getMaxFPLenght() {
		return maxFPLenght;
	}

	public void setMaxFPLenght(int maxFPLenght) {
		this.maxFPLenght = maxFPLenght;
	}
	public double getMinRatio() {
		return minRatio;
	}
	public void setMinRatio(double minRatio) {
		this.minRatio = minRatio;
	}
	public double getDecayRatio() {
		return decayRatio;
	}
	public void setDecayRatio(double decayRatio) {
		this.decayRatio = decayRatio;
	}

	public HashMap<String, Integer> getCurCandiPathMap() {
		return curCandiPathMap;
	}

	public void setCurCandiPathMap(HashMap<String, Integer> curCandiPathMap) {
		this.curCandiPathMap = curCandiPathMap;
	}

	public List<HashMap<String, Integer>> getFpMapList() {
		return fpMapList;
	}

	public void setFpMapList(List<HashMap<String, Integer>> fpMapList) {
		this.fpMapList = fpMapList;
	}

	public int getCurFPLenght() {
		return curFPLenght;
	}

	public void setCurFPLenght(int curFPLenght) {
		this.curFPLenght = curFPLenght;
	}

	public int getPathNum() {
		return pathNum;
	}

	public void setPathNum(int pathNum) {
		this.pathNum = pathNum;
	}
}
