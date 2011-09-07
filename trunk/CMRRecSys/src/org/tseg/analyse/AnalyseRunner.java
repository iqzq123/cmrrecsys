package org.tseg.analyse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tseg.Ulits.Ulits;
import org.tseg.preprocess.Preprocessor;

public class AnalyseRunner {

	private String inputPath;
	private String siteDataPath;
	private String outputPath;
	private List<Analyse> analyseList=new ArrayList<Analyse>();	
	private Integer progress = null;	
	private String logSplit="\\|";
	
	public void addAnalyse(Analyse analyse){
		this.analyseList.add(analyse);
	}

	public void seqRun()throws IOException{
		
		
		Preprocessor.readMapFile(this.siteDataPath);
		
		/////////////////create output floder
		String outFloder = this.outputPath;
		Ulits.newFolder(outFloder);
		
		FileReader fr = new FileReader(this.inputPath);
		BufferedReader reader = new BufferedReader(fr);
		String str;
		reader.readLine();
		int cnt = 0;
		System.out.println("seqrun");
		
		for(Analyse analyse:this.analyseList){
			analyse.setOutputPath(outFloder);
			analyse.onInitial();
			System.out.println("............run.........:"+analyse.getName()+analyse.getType());
		}

		while ((str = reader.readLine()) != null) {
			

			String[] strArray = str.split(this.logSplit);
			if(strArray.length<32){
				continue;
			}
			//Preprocessor.process(strArray);
			for(Analyse analyse:this.analyseList){
				String[] proArray=Preprocessor.run(strArray, analyse.getType());
				analyse.onReadLog(proArray);
			}
			
			this.progress++;
			cnt++;
			if (cnt % 10000 == 0) {		
				System.out.println(cnt);
			}
		}
		System.out.println("pv总数为："+cnt);
		for(Analyse analyse:this.analyseList){
			analyse.onReadEnd();
		}
	
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AnalyseRunner b = new AnalyseRunner();
		
	

		try {

			b.setInputPath("E:/data/pagevisit/test.txt");
			b.setOutputPath(b.getInputPath()+".out");
			b.setSiteDataPath("E:/data");
				
			GlobalAnalyse mk=new GlobalAnalyse();
			b.addAnalyse(mk);
			b.seqRun();
			
			System.out.println("success");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getSiteDataPath() {
		return siteDataPath;
	}

	public void setSiteDataPath(String siteDataPath) {
		this.siteDataPath = siteDataPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}



	public void getProgress(Integer progress) {
		this.progress = progress;
	}



	public String getLogSplit() {
		return logSplit;
	}

	public void setLogSplit(String logSplit) {
		this.logSplit = logSplit;
	}

}
