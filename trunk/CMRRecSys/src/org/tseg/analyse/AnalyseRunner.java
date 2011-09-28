package org.tseg.analyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.tseg.Ulits.Separator;
import org.tseg.Ulits.Ulits;
import org.tseg.preprocess.Preprocessor;

public class AnalyseRunner {

	private String inputPath;
	private String siteDataPath;
	private String outputPath;
	private List<Analyse> analyseList = new ArrayList<Analyse>();
	private int curLineNum = 0;
	private int lineAmount=1;
	

	private String logSplit = "\\|";

	public void addAnalyse(Analyse analyse) {
		this.analyseList.add(analyse);
	}

	public void run() throws Exception {

		Preprocessor.readMapFile(this.siteDataPath);

		// ///////////////create output floder
		String outFloder = this.outputPath;
		Ulits.newFolder(outFloder);

		for (Analyse analyse : this.analyseList) {
			analyse.setOutputPath(outFloder);
			analyse.onInitial();
			System.out.println("............run.........:" + analyse.getName()
					+ analyse.getType());
		}

		
		File inputFile = new File(this.inputPath);
		this.lineAmount=Ulits.getFileLineNum(inputFile.getName());
		
		if (inputFile.isDirectory()) {			
			File []fileArray=inputFile.listFiles();
			for(File file:fileArray){
				runSingleFile(file.getPath());
			}
		} else {
			runSingleFile(this.inputPath);
		}

		
		System.out.println("pv总数为：" + curLineNum);
		for (Analyse analyse : this.analyseList) {
			analyse.onReadEnd();
		}
		System.out.println("AnalyseRunner end!!!!!!!!!!!!!!");
		this.curLineNum=-1;
	}

	private void runSingleFile(String path) throws Exception {
		
		FileReader fr = new FileReader(path);
		BufferedReader reader = new BufferedReader(fr);
		String str;
		//reader.readLine();	
		System.out.println("runSingleFile");

		while ((str = reader.readLine()) != null) {

			String[] strArray = str.split(this.logSplit);
			if (strArray.length < 22) {
				continue;
			}
			for (Analyse analyse : this.analyseList) {
				String[] proArray = Preprocessor.run(strArray, analyse
						.getType());
				analyse.onReadLog(proArray);
			}
			curLineNum++;
			if (curLineNum % 100000 == 0) {
				System.out.println("read line:"+curLineNum);
				
			}
		}
		reader.close();
		fr.close();
	}

	public void seqRun() throws Exception {

		Preprocessor.readMapFile(this.siteDataPath);

		// ///////////////create output floder
		String outFloder = this.outputPath;
		Ulits.newFolder(outFloder);

		FileReader fr = new FileReader(this.inputPath);
		BufferedReader reader = new BufferedReader(fr);
		String str;
		reader.readLine();
		int cnt = 0;
		System.out.println("seqrun");

		for (Analyse analyse : this.analyseList) {
			analyse.setOutputPath(outFloder);
			analyse.onInitial();
			System.out.println("............run.........:" + analyse.getName()
					+ analyse.getType());
		}

		while ((str = reader.readLine()) != null) {

			String[] strArray = str.split(this.logSplit);
			if (strArray.length < 22) {
				continue;
			}
			// Preprocessor.process(strArray);
			for (Analyse analyse : this.analyseList) {
				String[] proArray = Preprocessor.run(strArray, analyse
						.getType());
				analyse.onReadLog(proArray);
			}
			cnt++;
			
			if (cnt % 100000 == 0) {
				System.out.println(cnt);
			}
		}
		System.out.println("pv总数为：" + cnt);
		for (Analyse analyse : this.analyseList) {
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

			b.setInputPath("E:/data/pvData/pvdata2.txt");
			b.setOutputPath(b.getInputPath() + ".out");
			b.setSiteDataPath("E:/data");
			GlobalAnalyse mk = new GlobalAnalyse();
			mk.setMinLinkNum(1);
			StatAnalyse s=new StatAnalyse();
			b.addAnalyse(mk);
			b.addAnalyse(s);
			b.run();
			System.out.println("success");

		} catch (Exception e) {
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

	public String getLogSplit() {
		return logSplit;
	}

	public void setLogSplit(String logSplit) {
		this.logSplit = logSplit;
	}

	public int getLineAmount() {
		return lineAmount;
	}

	public void setLineAmount(int lineAmount) {
		this.lineAmount = lineAmount;
	}

	public int getCurLineNum() {
		return curLineNum;
	}

	public void setCurLineNum(int curLineNum) {
		this.curLineNum = curLineNum;
	}

}
