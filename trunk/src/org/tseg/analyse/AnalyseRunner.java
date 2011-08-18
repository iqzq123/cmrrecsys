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
	private int progress = 0;	
	
	public void addAnalyse(Analyse analyse){
		this.analyseList.add(analyse);
	}

	public void test() throws IOException {
		
		LogBasicAnalyse lba = new LogBasicAnalyse();
		MarkovModelBuilder marbuilder = new MarkovModelBuilder();

		Preprocessor.readMapFile(this.siteDataPath);

		FileReader fr = new FileReader(this.inputPath);
		BufferedReader reader = new BufferedReader(fr);
		String str;
		reader.readLine();
		int cnt = 0;
		while ((str = reader.readLine()) != null) {

			String[] strArray = str.split(",");
			Preprocessor.process(strArray);
			
			marbuilder.buildGlobalModel(strArray);
			marbuilder.buildPersonModel(strArray);
			lba.readLog(strArray);

			cnt++;
			if (cnt % 10000 == 0) {
				this.progress++;
				System.out.println(cnt);
			}
		}
		
		String outFloder = this.inputPath + ".out";
		String subOutFloder1 = outFloder + "/pm.txt";
		String subOutFloder2 = outFloder + "/gm.txt";
		Ulits.newFolder(outFloder);
		Ulits.newFolder(subOutFloder1);
		Ulits.newFolder(subOutFloder2);

		System.out.println(outFloder);

		// //////////15277093617

		long a=System.currentTimeMillis();
		lba.savePerHis(outFloder + "/his.txt");
		long b=System.currentTimeMillis();
		String s=String.valueOf(a-b)+"mm";
		System.out.println(s);
		lba.getFrePath(outFloder + "/fp.txt");
		lba.savePaths(outFloder + "/path.txt");

		marbuilder.getGlobalModel().saveModel(outFloder + "/am.txt", 0,
				false);
		marbuilder.getGlobalModel().saveModelMXL(outFloder + "/am.xml",
				400, false);
		marbuilder.savePersonModels(subOutFloder1);

		lba.saveSenNumHistogram(outFloder + "/logNum.xml");
		lba.saveDurationHistogram(outFloder + "/duration.xml");

//		Cluster c = new Cluster();
//		c.setDataIter(this.marbuilder.getPersonModelMap());
//		c.getSimGraph(outFloder + "/simGraph.txt");
//		c.runFastGN(outFloder + "/simGraph.txt", outFloder + "/cluster.txt");
//
//		this.marbuilder.buildGroupModel(c.getCommunityList(), subOutFloder2);
		System.out.println("success11111111111111111111111");

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
			

			String[] strArray = str.split(",");
			//Preprocessor.process(strArray);
			for(Analyse analyse:this.analyseList){
				String[] proArray=Preprocessor.run(strArray, analyse.getType());
				analyse.onReadLog(proArray);
			}
			
			cnt++;
			if (cnt % 10000 == 0) {
				this.progress++;
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

			b.setInputPath("E:/data/pagevisit/pv6.txt");
			b.setOutputPath(b.getInputPath()+".out2");
			b.setSiteDataPath("E:/data");
			b.setNegCate(true);
			
					
			SeqLBA slba=new SeqLBA();	
			slba.setMaxCacheNum(1000);
			b.addAnalyse(slba);
		
			SeqMKBuilder mk=new SeqMKBuilder();
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

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public boolean isNegCate() {
		return Preprocessor.isNegCate();
	}

	public void setNegCate(boolean isNegCate) {
		//this.replacer.setNegCate(isNegCate);
		Preprocessor.setNegCate(isNegCate);
	}

}
