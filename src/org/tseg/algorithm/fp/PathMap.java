package org.tseg.algorithm.fp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PathMap {
	private Map<String,Integer> PathNumber = new HashMap<String,Integer>();
	//路径与路径条数的对应
	private Integer m;
	//路径的长度
	private double MinSupport;
	//最小支持度
	private Integer one = 1;
	
	
	public void addPath (String subpath)
	{
		if(this.PathNumber.containsKey(subpath))
		{
			int t = this.PathNumber.get(subpath);
			t++;
			this.PathNumber.remove(subpath);
			this.PathNumber.put(subpath, t);
		}
		else this.PathNumber.put(subpath, one);
	}
	public Iterator iterator()
	{		
		return this.PathNumber.keySet().iterator();
	}
	public int getEachPathNumber(String subpath) //����ָ��·��������
	{
		return this.PathNumber.get(subpath);
	}
	public boolean contain(String path)
	{
		return this.PathNumber.containsKey(path);
	}
	
	public int getPathNumber() 
	{
		return this.PathNumber.size();
	}
	
	public int getTotalNumber() //��������·���������ܺ�
	{
		Iterator it = this.PathNumber.keySet().iterator();
		int number = 0;
		while(it.hasNext())
		{
			String path = (String)(it.next());
			number+=getEachPathNumber(path);
		}
		return number;
	}
	
	
	public void setM(int n)
	{
		this.m=n;
	}
	public int getM()
	{
		return this.m;
	}
	public void setminSupport(double d)
	{
		MinSupport = d;
	}
	public double getminSupport()
	{
		return MinSupport;
	}
	public boolean isEmpty()
	{
		return this.PathNumber.isEmpty();
	}
	

}
