package org.tseg.analyse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tseg.Ulits.AnalyseType;
import org.tseg.Ulits.Separator;
import org.tseg.Ulits.Ulits;
import org.tseg.algorithm.FPAlgorithm;
import org.tseg.model.PVHistory;
import org.tseg.preprocess.Preprocessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class FPAnalyser {

	private String inputPath;
	private String outputPath;
	private String siteDataPath;
	private String logSplit="\\|";
	private PVHistory curPVHis = null;
	private FPAlgorithm fpAlgo = new FPAlgorithm();
	private int maxFPLenght = 10;
	private int curFPLenght = 1;
	private Byte analyseType = AnalyseType.NegCate;
	private boolean isClosed=false;
	private HashMap<String,Integer> pageMap=new HashMap<String,Integer>();
	private AtomicInteger progress = null;
	
	private int pathCnt=0;
	private int cnt=0;
	private int amount=0;
	
	/**
	 * @param param
	 * 读取参数
	 */
	public void readParam(String param){
		
		String []paramArray=param.split(Separator.PARAM_SEPARATOR1);
		this.siteDataPath=paramArray[0];
		this.inputPath=paramArray[1];
		this.outputPath=paramArray[2];
		this.analyseType=Byte.parseByte(paramArray[3]);
		this.setClosed(Boolean.parseBoolean(paramArray[4]));
		this.maxFPLenght=Integer.parseInt(paramArray[5]);
		this.fpAlgo.setMinRatio(Double.parseDouble(paramArray[6]));
		this.fpAlgo.setDecayRatio(Double.parseDouble(paramArray[7]));
	}

	public void run() throws IOException {
		
		Ulits.newFolder(this.outputPath);
		Preprocessor.readMapFile(this.siteDataPath);
		
		while (this.curFPLenght < this.maxFPLenght) {

			fpAlgo.setCurCandiPathMap(new HashMap<String, Integer>());
			fpAlgo.setPathNum(0);
			fpAlgo.setCurFPLenght(this.curFPLenght);
			this.curPVHis = null;
			this.cnt=0;		
			File inputFile = new File(this.inputPath);
			this.amount=Ulits.getFileSize(inputFile.getName());		
			if (inputFile.isDirectory()) {			
				File []fileArray=inputFile.listFiles();
				for(File file:fileArray){
					runForSingleFile(file.getPath());
				}
			} else {
				runForSingleFile(this.inputPath);
			}		
			System.out.println(this.cnt);		
			this.onReadEnd();
			System.out.println("curFPLenght:" + this.curFPLenght + "	pathNum:"
					+ this.fpAlgo.getPathNum() + "\n");
			System.out.println("fpNum:"
					+ this.fpAlgo.getFpMapList().get(this.curFPLenght).size());

			this.curFPLenght++;
			if (this.fpAlgo.getCurCandiPathMap().size() == 0) {
				break;
			}
		}

		
		this.fpAlgo.getFpMapList().remove(0);
		if(this.isClosed==true){
			System.out.println("start close\n");
			closeFP(this.fpAlgo.getFpMapList());
		}
		saveResult();
		saveResultXML();
		this.progress.set(-1);
		System.out.println("FPAnalyser end!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("pathCnt......................"+this.pathCnt);
	}

	private void runForSingleFile(String file) throws FileNotFoundException, IOException {
		
		FileReader fr = new FileReader(file);
		BufferedReader reader = new BufferedReader(fr);
		String str;
		//reader.readLine();
		while ((str = reader.readLine()) != null) {
			
			String[] strArray = str.split(this.logSplit);
			if (strArray.length < 22) {
				continue;
			}
			String[] log = Preprocessor.run(strArray, analyseType);
		
			// Preprocessor.tranPageToCate(log);
			this.onReadLog(log);
			if (cnt % 10000 == 0) {
				System.out.println(cnt+"\n");
				System.out.println(this.curFPLenght+"\n");			
			}
			cnt++;
			int percent=(int)((cnt*1.0/this.amount)*100000000);
			this.progress.set(percent);
			
		}
		
	}

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		/**
		 * param 参数的顺序如下：
		 * 站点相关数据的路径
		 * 输入路径
		 * 输出路径
		 * 分析类型
		 * 是否就闭集
		 * 最长频繁路径程度
		 * 最小支持度
		 * 支持度衰减比例
		 */
		FPAnalyser fp = new FPAnalyser();
		fp.getProgress(new AtomicInteger(0));
		fp.setLogSplit("\\|");
		String param="E:/data"+Separator.PARAM_SEPARATOR1+
		"E:/data/test_a/pvdata2.txt"+Separator.PARAM_SEPARATOR1+
		"E:/data/test14"+Separator.PARAM_SEPARATOR1+
		AnalyseType.NegCate+Separator.PARAM_SEPARATOR1+
		true+Separator.PARAM_SEPARATOR1+
		10+Separator.PARAM_SEPARATOR1+
		0.001+Separator.PARAM_SEPARATOR1+
		0.9+Separator.PARAM_SEPARATOR1;
		fp.readParam(param);
		

	
		try {
			fp.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	

	}

	private void sortFpListByLift(List<String> fpList) {

		for (int i = 0; i < fpList.size(); i++) {
			for (int j = i + 1; j < fpList.size(); j++) {
				double p1 = Double.parseDouble(fpList.get(i).split("\t")[2]);
				double p2 = Double.parseDouble(fpList.get(j).split("\t")[2]);
				if (p2 > p1) {
					String tmp = fpList.get(i);
					fpList.set(i, fpList.get(j));
					fpList.set(j, tmp);

				}
			}
		}
		System.out.println("sort done...........");
	}

	public void closeFP(List<HashMap<String, Integer>> fpMapList) {

		HashMap<String, Integer> preFpMap = null;
		for (HashMap<String, Integer> fpMap : fpMapList) {
			if (preFpMap != null) {
				Iterator iter = fpMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					String[] pageArray = key.split(Separator.pathSeparator);
					ArrayList<String> list = new ArrayList<String>();

					// /////////generate subPath
					for (int i = 0; i < pageArray.length; i++) {
						String subPath = "";
						for (int j = 0; j < pageArray.length; j++) {
							if (i != j) {
								subPath += pageArray[j]
										+ Separator.pathSeparator;
							}
						
						}
						subPath = subPath
						.substring(0, subPath.length() - 1);
						if (preFpMap.containsKey(subPath)) {
							preFpMap.remove(subPath);
						}

					}
				}
				
			}
			preFpMap=fpMap;
			
		}
	}

	public void saveResultXML() {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (Exception pce) {
			System.err.println(pce);
		}
		Document doc = db.newDocument();
		Element root = doc.createElement("FrequentPath");
		doc.appendChild(root);

		for (HashMap<String, Integer> map : this.fpAlgo.getFpMapList()) {		
			Iterator iter = map.entrySet().iterator();
			List<String> fpList = new ArrayList<String>();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				Integer value = (Integer) entry.getValue();
				String[] pageArray = key.split(Separator.pathSeparator);
				double lift = 1.0;
				for (String s : pageArray) {
					Integer pageCnt = this.pageMap.get(s);
					if (pageCnt == null) {
						System.out.println("pageCnt ==null\n");
						System.out.println(s);
						pageCnt = -1;
					}
					lift = lift * (value * 1.0 / pageCnt);				
				}
				fpList.add(key + "\t" + value.toString() + "\t" + lift);
			}
			sortFpListByLift(fpList);
		
			for (String s : fpList) {
				Element pathElement = doc.createElement("Path");
				String[] strArray=s.split("\t");
				String path=strArray[0];
				String[] pageArray=path.split(Separator.pathSeparator);
				pathElement.setAttribute("pathNum", strArray[1]);
				pathElement.setAttribute("lift", strArray[2]);
				for(String page:pageArray){
					Element pageElement = doc.createElement("Page");
					pageElement.setAttribute("pageName", Preprocessor.getPageName(page));
					pathElement.appendChild(pageElement);
				}
				root.appendChild(pathElement);
			}
			
		}

		try {
			// 用xmlserializer把document的内容进行串化
			FileOutputStream os = null;
			OutputFormat outformat = new OutputFormat(doc);
			os = new FileOutputStream(this.outputPath+"/频繁路径"+this.analyseType+".xml");
			XMLSerializer xmlSerilizer = new XMLSerializer(os, outformat);
			xmlSerilizer.serialize(doc);

		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}

	}

	public void saveResult() {

		try {
			FileWriter fw = new FileWriter(this.outputPath+"/频繁路径"+this.analyseType+".txt");
			BufferedWriter writer = new BufferedWriter(fw);
			int len = 0;			
			for (HashMap<String, Integer> map : this.fpAlgo.getFpMapList()) {
				Iterator iter = map.entrySet().iterator();
				// System.out.println("路径长度....................................."+len);
				// System.out.println("路径总数....................................."+this.fpAlgo.getPathNum()+"\n");
				len++;
				writer.write("路径长度....................................." + len
						+ "\n");

				List<String> fpList = new ArrayList<String>();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					Integer value = (Integer) entry.getValue();
					String[] pageArray = key.split(Separator.pathSeparator);
					String path = "";
					double lift = 1.0;
					for (String s : pageArray) {
					
						Integer pageCnt = this.pageMap.get(s);
						if (pageCnt == null) {
							System.out.println("pageCnt ==null\n");
							System.out.println(s);
							pageCnt = -1;
						}

						lift = lift * (value * 1.0 / pageCnt);
						path += Preprocessor.getPageName(s) + ",";
					}
					fpList.add(path + "\t" + value.toString() + "\t" + lift);
					// writer.write(path + "\t" + value.toString() +
					// "\t"+lift+"\n");
				}
				sortFpListByLift(fpList);
				for (String s : fpList) {
					writer.write(s + "\n");
					System.out.println(s + "\n");
				}

			}
			writer.flush();
			writer.close();
		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}

	}

	public void onReadLog(String[] strArray) {

		Long id = Long.parseLong(strArray[0]);
		if (this.curPVHis == null) {
			this.curPVHis = new PVHistory();
			this.curPVHis.setId(id);
			this.curPVHis.addLog(strArray);
		} else {
			if (this.curPVHis.getId() == id) {
				this.curPVHis.addLog(strArray);
			} else {
				// ////////////////////////////////////////////////////
				onReadHis(this.curPVHis);
				this.curPVHis = new PVHistory();
				this.curPVHis.setId(id);
				this.curPVHis.addLog(strArray);
			}

		}

	}

	void onReadHis(PVHistory his) {

		// System.out.print(his.toString());
		for (String path : his.getPathString()) {
			// System.out.print(path+"\n");
			onReadPath(path);
		}

	}

	public void onReadPath(String path) {
		
		List<String> pList = new ArrayList<String>();
		String[] nodeArray = path.split(",");
		if (nodeArray.length > 20) {
			// System.out.println("大于 20");
			return;
		}
		Set<String> set=new HashSet<String>();
		if(this.curFPLenght==1){
			this.pathCnt++;
			for(String page:nodeArray){
				
				if(!set.contains(page)){
					Integer cnt=this.pageMap.get(page);
					if(cnt!=null){
						cnt++;
						this.pageMap.put(page, cnt);
					}else{
						this.pageMap.put(page, 1);
					}
					set.add(page);
				}		
			
			}
		}
	
		
	
		fpAlgo.getCandiPath(nodeArray, 0, null, this.curFPLenght, pList);
		fpAlgo.countPath(pList);
		fpAlgo.increasePathNum();

	}

	public void onReadEnd() {

		fpAlgo.removeNoFrePath();
		fpAlgo.getFpMapList()
				.add(this.curFPLenght, fpAlgo.getCurCandiPathMap());
		// fpAlgo.curCandiPathMap=new HashMap<String,Integer>();

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

	public int getMaxFPLenght() {
		return maxFPLenght;
	}

	public void setMaxFPLenght(int maxFPLenght) {
		this.maxFPLenght = maxFPLenght;
	}

	public FPAlgorithm getFpAlgo() {
		return fpAlgo;
	}

	public void setFpAlgo(FPAlgorithm fpAlgo) {
		this.fpAlgo = fpAlgo;
	}

	public Byte getAnalyseType() {
		return analyseType;
	}

	public void setAnalyseType(Byte analyseType) {
		this.analyseType = analyseType;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public String getSiteDataPath() {
		return siteDataPath;
	}

	public void setSiteDataPath(String siteDataPath) {
		this.siteDataPath = siteDataPath;
	}

	public String getLogSplit() {
		return logSplit;
	}

	public void setLogSplit(String logSplit) {
		this.logSplit = logSplit;
	}

	public void getProgress(AtomicInteger progress) {
		this.progress = progress;
	}

}
