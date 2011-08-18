package org.tseg.analyse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tseg.Ulits.AnalyseType;
import org.tseg.Ulits.Separator;
import org.tseg.algorithm.FPAlgorithm;
import org.tseg.model.PVHistory;
import org.tseg.model.Page;
import org.tseg.model.Visit;
import org.tseg.preprocess.Preprocessor;

public class FPAnalyser {

	private String inputPath;
	private String outputPath;
	private PVHistory curPVHis = null;
	private FPAlgorithm fpAlgo = new FPAlgorithm();
	private int maxFPLenght = 10;
	private int curFPLenght = 1;
	private Byte analyseType = AnalyseType.PageToCate;

	public void run() throws IOException {

		Preprocessor.readMapFile("E:/data");
		while (this.curFPLenght < this.maxFPLenght) {

			fpAlgo.setCurCandiPathMap(new HashMap<String, Integer>());
			fpAlgo.setPathNum(0);
			fpAlgo.setCurFPLenght(this.curFPLenght);
			this.curPVHis = null;
			FileReader fr = new FileReader(this.inputPath);
			BufferedReader reader = new BufferedReader(fr);
			String str;
			reader.readLine();
			int cnt = 0;
			while ((str = reader.readLine()) != null) {

				String[] log = Preprocessor.run(str.split(","), analyseType);
				// Preprocessor.tranPageToCate(log);
				this.onReadLog(log);
				if (cnt % 10000 == 0) {
					System.out.println(cnt);
				}
				if (cnt > 2700000) {
					if (cnt % 1000 == 0) {
						System.out.println(cnt);
					}
				}
				cnt++;
			}
			System.out.println(cnt);
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

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		FPAnalyser fp = new FPAnalyser();
		fp.setInputPath("E:/data/pagevisit/pv6.txt");
		fp.setOutputPath("E:/data/pagevisit/fp_origin.txt");
		fp.getFpAlgo().setMinRatio(0.005);
		fp.getFpAlgo().setDecayRatio(0.8);
		fp.getFpAlgo().setMaxFPLenght(2);
		fp.setAnalyseType(AnalyseType.Original);
		try {
			fp.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fp.saveResult();

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

	public void saveResult() {

		try {
			FileWriter fw = new FileWriter(this.outputPath);
			BufferedWriter writer = new BufferedWriter(fw);
			int len = 0;
			HashMap<String, Integer> map0 = this.fpAlgo.getFpMapList().get(1);
			this.fpAlgo.getFpMapList().remove(0);
			for (HashMap<String, Integer> map : this.fpAlgo.getFpMapList()) {
				Iterator iter = map.entrySet().iterator();
				// System.out.println("路径长度....................................."+len);
				// System.out.println("路径总数....................................."+this.fpAlgo.getPathNum()+"\n");
				writer.write("路径长度....................................." + len
						+ "\n");
				len++;
				List<String> fpList = new ArrayList<String>();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					Integer value = (Integer) entry.getValue();
					String[] pageArray = key.split(Separator.pathSeparator);
					String path = "";
					double lift = 1.0;
					for (String s : pageArray) {
						if(map0==null){
							System.out.println("map0 ==null");
						}
						Integer pageCnt = map0.get(s);
						if(pageCnt==null){
							System.out.println("pageCnt ==null\n");
							System.out.println(s);
							pageCnt=-1;
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
			String sessionID = strArray[11];
			Visit v = new Visit(strArray);
			List<Visit> l = new ArrayList<Visit>();
			l.add(v);
			this.curPVHis.getSessionMap().put(sessionID, l);

		} else {

			if (this.curPVHis.getId() == id) {

				Visit v = new Visit(strArray);
				String sessionID = strArray[11];
				List<Visit> list = this.curPVHis.getSessionMap().get(sessionID);
				if (list != null) {
					list.add(v);
					this.curPVHis.getSessionMap().put(sessionID, list);
				} else {
					List<Visit> l = new ArrayList<Visit>();
					l.add(v);
					this.curPVHis.getSessionMap().put(sessionID, l);
				}

			} else {

				// ////////////////////////////////////////////////////

				onReadHis(this.curPVHis);

				// ///////////////////////////////////////////////////
				this.curPVHis = new PVHistory();
				this.curPVHis.setId(id);
				String sessionID = strArray[11];
				Visit v = new Visit(strArray);
				List<Visit> l = new ArrayList<Visit>();
				l.add(v);
				this.curPVHis.getSessionMap().put(sessionID, l);
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

}
