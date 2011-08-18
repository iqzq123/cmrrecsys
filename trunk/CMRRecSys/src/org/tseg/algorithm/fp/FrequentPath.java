package org.tseg.algorithm.fp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FrequentPath {
	private Set<String> fp = new HashSet<String>();
	private Set<String> backupPath = new HashSet<String>();
	public void putfp(String s)
	{
		fp.add(s);
	}
	
	public void putbp(String s)
	{
		backupPath.add(s);
	}
	
	public Iterator getfp()
	{
		return fp.iterator();
	}
	public Iterator getbp()
	{
		return backupPath.iterator();
	}
	public int getfpSize()
	{
		return fp.size();
	}
	public int getbpSize()
	{
		return backupPath.size();
	}

}
