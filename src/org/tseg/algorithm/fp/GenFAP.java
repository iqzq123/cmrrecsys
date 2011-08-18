package org.tseg.algorithm.fp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;


public class GenFAP {
	
	private  String inputpath = "";
	private  String outputpath = "";
	private  String pathmappath = "";
	private  List<String> inputList = new ArrayList();
	private  double ratio = 0.3;
	private  double supp;
	private int pathMaxLength = 1000;
	private int pathLength = 0;
	private PathMap[] pathmap = new PathMap[pathMaxLength];
	private FrequentPath[] FP  = new FrequentPath[pathMaxLength];
	
	//构造函数，初始化pathmap和FP
	public GenFAP() throws Exception
	{
		for(int i=0;i<pathMaxLength;i++)
		{
			FP[i] = new FrequentPath();
			pathmap[i] = new PathMap();
			pathmap[i].setM(i);
		}
	}
	
	public void readFromFile() throws Exception
	{
		File f = new File(this.inputpath);
		BufferedReader br = new BufferedReader(new FileReader(f));
		String s;
		while((s=br.readLine())!=null)
			read(s);
		br.close();
	}
	
	public void readFromlist() throws Exception
	{
		for(int i=0;i<inputList.size();i++)
			read(inputList.get(i));
	}
		
	/*
	* 读取一条路径，把这条路径拆分成长度从1到k的若干条路径，分别放入pathmap[i]中。
	 * pathmap[i]维护长度为i的路径和条数。 
	 * 
	 */
	public void read(String s) throws Exception
	{
		
		String[] cutpath = s.split(",");	
		if(pathLength<cutpath.length)
			pathLength = cutpath.length;
		for(int len=1;len<=cutpath.length;len++) //len stands for the length of cutting a path.
		{
			String genpath = "";
			for(int i=0,j=i+len-1;j<cutpath.length;i++,j++)
			{
				genpath = "";
				for(int k=i;k<j;k++)
					genpath+=cutpath[k]+",";
				genpath+=cutpath[j];
				pathmap[len].addPath(genpath);
			}
		}
	}
		
		
	/*
	 * 根据预设的比率ratio来计算最小支持度
	 * 比如ratio等于0.2，输入的路径有100条，那么排名在第20条的路径在所有路径中所占的比例即为最小支持度
	 */
	public void calSupp() 
	{
		int N;
		int j;
		int number;
		int a[] = new int[5000];
		double minSupport;
		for(int i=0;i<=pathLength;i++)
		{
			
				j=-1;
				N = pathmap[i].getPathNumber();
//				System.out.println("N is"+N);
				number = (int) (this.ratio*N);
//				System.out.println("number is "+number);
				if(number==0)continue;
				else
				{
					Iterator it = pathmap[i].iterator();
					while(it.hasNext())
					{
						j++;
						String path = (String)(it.next());
						a[j] = pathmap[i].getEachPathNumber(path);
					}
					if(j<0)continue;
					else
					{
						int lastnumber = select(a,0,j,number);
						int totalnumber = pathmap[i].getTotalNumber();
						minSupport = (double)lastnumber/totalnumber;
//						System.out.println("����Ϊ"+i+"��·������С֧�ֶ�Ϊ:"+minSupport);
						pathmap[i].setminSupport(minSupport);
					}
				}										
		}
	}
	
	/*
	 * 根据计算出来的最小支持度，把满足条件的路径放入FP[i]中。
	 */
	public void putFP()
	{
		calSupp();
		for(int i=1;i<=2;i++)
		{
			int totelnumber = pathmap[i].getTotalNumber();
			double minSupp = pathmap[i].getminSupport();
			Iterator it = pathmap[i].iterator();
			while(it.hasNext())
			{
				String path = (String)(it.next());
				int number = pathmap[i].getEachPathNumber(path);
				if(number>=(minSupp*totelnumber))
				{
					FP[i].putfp(path);
				}
			}
		}
		for(int i=2;i<pathLength;i++)
		{
			
			genBackupPath(i);//generate the backup path of length i+1;
			//String pathmap[]
			String[] backuppath = new String[FP[i+1].getbpSize()];
			Iterator it = FP[i+1].getbp();
			for(int k=0;k<backuppath.length;k++)
			{
				String backpath = (String)(it.next());
				backuppath[k]=backpath;
			}
//			System.out.println(i+" "+backuppath.length+" "+FP[i+1].getbpSize());
			for(int j=0;j<backuppath.length;j++)
			{
				int totelnumber = pathmap[i+1].getTotalNumber();
				double minSupp = pathmap[i+1].getminSupport();
//				System.out.println(i+1+" "+totelnumber+" "+minSupp+" ");
				String path = backuppath[j];
//				System.out.println(path);
				//FP[i+1].backupPath.contains(o)
				if(pathmap[i+1].contain(path))
				{
					int number = pathmap[i+1].getEachPathNumber(path);
//					System.out.println(number);
					if(number>0&&number>=(minSupp*totelnumber))
					{
						FP[i+1].putfp(path);
					}
				}								
			}
		}
	}
	
	//generate the backup path of length i+1,
	//which are generated from the frequent path of length i. 
	public void genBackupPath(int i)
	{
	
		Iterator it = FP[i].getfp();
		String [] paths = new String[FP[i].getfpSize()];
//		System.out.println("G: "+i+" "+FP[i].getfpSize());
		for(int j=0;j<paths.length;j++)
			paths[j]=(String)(it.next());
		for(int j=0;j<paths.length-1;j++)
			for(int k=j+1;k<paths.length;k++)
			{
			
				String path1 = paths[j].substring(2);
				String path2 = paths[k].substring(0, paths[k].length()-2);
				String path3 = paths[j].substring(0, paths[j].length()-2);
				String path4 = paths[k].substring(2);
//				System.out.println("path1 is:"+path1+"---path2 is:"+path2);
//				System.out.println("path3 is:"+path3+"---path4 is:"+path4);
				String newpath = "";
				if(path1.equals(path2))
				{
					newpath = paths[j]+paths[k].substring(paths[k].length()-2);
//					System.out.println("new path is:"+newpath);
					FP[i+1].putbp(newpath);
				}
				if(path3.equals(path4))
				{
					newpath = paths[k]+paths[j].substring(paths[j].length()-2);
//					System.out.println("new path is:"+newpath);
					FP[i+1].putbp(newpath);
				}
			}
	}
	
	
	
	/*
	 * 把结果输出到指定的outputpath文件.
	 */
	public void output() throws Exception
	{
		String str = "";
		File f = new File(this.outputpath);
		createparent(f.getAbsolutePath());
		if(!f.exists())f.createNewFile();
		FileWriter fw = new FileWriter(f);
		for(int i=0;i<=pathLength;i++)
		{
			
				//str = str + "长度为" + Integer.toString(i) + "的频繁路径为：" + "\n";
				Iterator it = FP[i].getfp();
				while(it.hasNext())
				{
					str = (String)(it.next())+" "+pathmap[i].getminSupport()+"\n";
					fw.write(str);
				}				
			
		}
		fw.close();
	}
	
	
	/*
	 * 把pathmap数组中存的每条路径与路径条数的映射输出到文件
	 */
	public void outputpathmap() throws Exception
	{
		String str = "";
		for(int i=0;i<=pathLength;i++)
		{			
				Iterator it = pathmap[i].iterator();
				while(it.hasNext())
				{
					String path = (String)(it.next());
					str+=path;
					str+="  ";
					str+=pathmap[i].getEachPathNumber(path);
					str+="\n";
				}			
		}
		File f = new File(this.pathmappath);
		createparent(f.getAbsolutePath());
		if(!f.exists())f.createNewFile();
		FileWriter fw = new FileWriter(f);
		fw.write(str);
		fw.close();
	}
	
	/*
	 * 检查输出路径的上级目录以及再往上的所有目录是否存在，如果不存在，则递归的建立目录。
	 */
	public void createparent(String s)
	{
		File f = new File(s);
		File parent = new File(f.getParent());
//		System.out.println(parent.getAbsolutePath());
		if(parent!=null&&!parent.exists())createparent(f.getParent());
		if(parent!=null&&!parent.exists())
			{
				System.out.println("parent is not exist!");
				if(parent.mkdir())System.out.println("make dir success!");
			}
	}
	
	/*
	 *选择算法，选择一个数组中第i大的数。
	 */
	public int select(int[] A,int p,int r,int i)
	{
		if(p==r)return A[p];
		int q = partition(A,p,r);//返回的q表示前面有q-p个数比自己大
		int k = q-p+1;
		if(k==i)return A[q];
		else if(i<k)
			return select(A,p,q-1,i);
		else return select(A,q+1,r,i-k);
	}
		
	public int partition(int[] A,int p,int r)
	{
		int x = A[r];
		int i = p-1;
		for(int j=p;j<r;j++)
		{
			if(A[j]>=x)
			{
				i++;
				int temp = A[i];
				A[i] = A[j];
				A[j] = temp;
			}
		}
		int temp = A[i+1];
		A[i+1] = A[r];
		A[r] = temp;
		return i+1;
	}
	
	
	public void run() throws Exception
	{
		calSupp();
		putFP();
//		outputpathmap();
//		output();
//		outputMinsupp();
//		outputbp();
	}
	
	public static void main(String[] args) throws Exception
	{	
		GenFAP GMP = new GenFAP();
		GMP.setInputpath("D:/path2.txt");
		GMP.setOutputpath("D:/FAP/output.txt");
		GMP.setPathmappath("D:/FAP/pathmap.txt");
		GMP.setRatio(0.3);
		GMP.readFromFile();
		GMP.run();		
		GMP.output();
		System.out.println("main stop!");
//		List<String> list = new ArrayList<String>();
//		List<String> resultlist = new ArrayList<String>();
//		list.add("a,b,c,d,e");
//		list.add("a,b,c,f,g,h");
//		list.add("d,e,g");
//		list.add("e,g,h");
//		list.add("a,d,t,s,g");
//		list.add("a,b,c,d,e");
//		list.add("d,e,g");
//		list.add("d,c,g,e,h");
//		list.add("b,d,e,f,g,h,i");
//		list.add("a,c,d,f,g");
//		list.add("a,b,c,e,g");
//		list.add("b,c,d");
//		list.add("a,b,c,d,f");
//		GMP.setInput(list);
//		GMP.readFromlist();
//		GMP.run();
//		resultlist = GMP.getFrePath(3);
//		for(int i=0;i<resultlist.size();i++)
//			System.out.println(resultlist.get(i));
//		System.out.println(GMP.pathLength);

	}

	public void setInputpath(String inputpath) {
		this.inputpath = inputpath;
	}

	public String getInputpath() {
		return inputpath;
	}

	public void setOutputpath(String outputpath) {
		this.outputpath = outputpath;
	}

	public String getOutputpath() {
		return outputpath;
	}

	public void setPathmappath(String pathmappath) {
		this.pathmappath = pathmappath;
	}

	public String getPathmappath() {
		return pathmappath;
	}
	
	public void setRatio(double ratio){
		this.ratio = ratio;
	}
	
	public double getRatio(){
		return this.ratio;
	}
	
	public void setInput(List<String> list){
		this.inputList = list;
	}
	
	public List<String> getFrePath(int minLength)
	{
		List<String> frePath = new ArrayList<String>();
		String str = "";
		for(int i=minLength;i<=pathLength;i++)
		{
				Iterator it = FP[i].getfp();
				while(it.hasNext())
				{
					str = (String)(it.next())+" "+pathmap[i].getminSupport();
					frePath.add(str);
				}				
			
		}
		return frePath;
	}
	
	
//	public void outputMinsupp() throws Exception
//	{
//		String str = "";
//		for(int i=0;i<size;i++)
//		{
//			if(pathmap[i].isEmpty())continue;
//			else
//			{
//				str = str+Integer.toString(pathmap[i].getPathNumber())+" "+Double.toString(pathmap[i].getminSupport())+" ";
//				double min = (double)pathmap[i].getTotalNumber()*pathmap[i].getminSupport();
//				str = str+Double.toString(min)+"\n";
//			}
//		}
//		File f = new File("D:/FAP/minsupport");
//		createparent(f.getAbsolutePath());
//		if(!f.exists())f.createNewFile();
//		FileWriter fw = new FileWriter(f);
//		fw.write(str);
//		fw.close();
//	}
//	
//	public void outputbp() throws Exception
//	{
//		String str = "";
//		for(int i=0;i<size;i++)
//		{
//			if(FP[i].backupPath.isEmpty())continue;
//			else
//			{
//				Iterator it = FP[i].backupPath.iterator();
//				while(it.hasNext())
//				{
//					String path = (String)(it.next());
//					str = str+path+"\n";
//				}
//			}
//		}
//		File f = new File("D:/FAP/backpath");
//		createparent(f.getAbsolutePath());
//		if(!f.exists())f.createNewFile();
//		FileWriter fw = new FileWriter(f);
//		fw.write(str);
//		fw.close();
//	}

}
