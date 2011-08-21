package org.tseg;


import java.util.HashMap;

import org.tseg.Ulits.AnalyseType;
import org.tseg.analyse.Analyse;
import org.tseg.analyse.AnalyseRunner;
import org.tseg.analyse.PathFinder;
import org.tseg.analyse.SeqLBA;
import org.tseg.analyse.SeqMKBuilder;
import org.tseg.analyse.Tundish;

public class Starter {

	private String firstSplit = "\n";
	private String secondSplit = "\t";
	private String inputPath="";
	private String outputPath="";
	private String siteDataPath="";
	private boolean isNegCate=false;
	private HashMap<String, Analyse> analyseMap = new HashMap<String, Analyse>();
	
	public Analyse getInstanceByName(String name){
		
		Analyse obj=this.analyseMap.get(name);
		try{
			Analyse newInstance=(Analyse)Class.forName(obj.getClass().getName()).newInstance();
			this.analyseMap.put(newInstance.getName(), newInstance);
		}catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obj;
	}

	public Starter() {
		super();
		// TODO Auto-generated constructor stub
		SeqLBA analyse1 = new SeqLBA();
		analyseMap.put(analyse1.getName(), analyse1);

		Tundish analyse2 = new Tundish();
		analyseMap.put(analyse2.getName(), analyse2);
		
		PathFinder pathFinder = new PathFinder();
		analyseMap.put(pathFinder.getName(), pathFinder);

		SeqMKBuilder mk=new SeqMKBuilder();
		analyseMap.put(mk.getName(), mk);
	}

	
	public void start(String cmd) throws Exception {

		AnalyseRunner b = new AnalyseRunner();
		b.setInputPath(this.inputPath);
		b.setOutputPath(this.outputPath);
		b.setSiteDataPath(this.siteDataPath);
		b.setNegCate(this.isNegCate);
		String[] cmdArray = cmd.split(this.firstSplit);
		for (String cmdLine : cmdArray) {
			String[] strArray = cmdLine.split(this.secondSplit);
			String name = strArray[0];
			String param = "";
			for (int i = 1; i < strArray.length; i++) {
				param += strArray[i] + this.secondSplit;
			}
			param = param.substring(0, param.length() - 1);
			//Analyse analyse = this.analyseMap.get(name);
			Analyse analyse = getInstanceByName(name);
			if (analyse != null) {
				analyse.readParam(param);
				b.addAnalyse(analyse);
			}

		}
		b.seqRun();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Starter s=new Starter();
		s.setInputPath("E:/data/pagevisit/pv6.txt");
		s.setOutputPath("E:/data/pagevisit/pv6_out");
		s.setSiteDataPath("E:/data");
		try{
			String cmd="basic analyse\t"+AnalyseType.NegCate+"\n"+
			"basic analyse\t"+AnalyseType.PageToCate+"\n"+
			"basic analyse\t"+AnalyseType.Original+"\n"+
			"global analyse\t"+AnalyseType.Original+"\n"+
			"global analyse\t"+AnalyseType.PageToCate+"\n"+
			"global analyse\t"+AnalyseType.NegCate;
			s.start(cmd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

	}

	public HashMap<String, Analyse> getAnalyseMap() {
		return analyseMap;
	}

	public String getFirstSplit() {
		return firstSplit;
	}

	public void setFirstSplit(String firstSplit) {
		this.firstSplit = firstSplit;
	}

	public String getSecondSplit() {
		return secondSplit;
	}

	public void setSecondSplit(String secondSplit) {
		this.secondSplit = secondSplit;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getSiteDataPath() {
		return siteDataPath;
	}

	public void setSiteDataPath(String siteDataPath) {
		this.siteDataPath = siteDataPath;
	}

	public boolean isNegCate() {
		return isNegCate;
	}

	public void setNegCate(boolean isNegCate) {
		this.isNegCate = isNegCate;
	}

}
