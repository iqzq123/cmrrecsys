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
	private AtomicInteger progress = null;

	private String logSplit = "\\|";

	public void addAnalyse(Analyse analyse) {
		this.analyseList.add(analyse);
	}

	public void run() throws IOException {

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
		String[] nameArray=inputFile.getName().split(Separator.FILENAME_SEPARATOR);
		try{
			lineAmount=Integer.parseInt(nameArray[nameArray.length-1])*10000;
		}catch(Exception e){
			lineAmount=100000000;
			System.out.print("fileName Format error");
		}
		
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
		this.progress.set(-1);
	}

	private void runSingleFile(String path) throws FileNotFoundException, IOException {
		
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
			this.progress.set(curLineNum);
			if (curLineNum % 100000 == 0) {
				System.out.println("read line:"+curLineNum);
				
			}
		}
		reader.close();
		fr.close();
	}

	public void seqRun() throws IOException {

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
			this.progress.set(cnt);
			if (cnt % 100000 == 0) {
				System.out.println(this.progress);
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

			b.setInputPath("E:/data/test_a");
			b.setOutputPath(b.getInputPath() + ".out");
			b.setSiteDataPath("E:/data");
			b.getProgress(new AtomicInteger(1));
			GlobalAnalyse mk = new GlobalAnalyse();
			b.addAnalyse(mk);
			b.run();

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

	public void getProgress(AtomicInteger progress) {
		this.progress = progress;
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
