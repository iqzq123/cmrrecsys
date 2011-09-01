package org.tseg.analyse;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.TimeZone;
import org.tseg.Ulits.Separator;
import org.tseg.Ulits.Ulits;
import org.tseg.model.Histogram;
import org.tseg.model.PVHistory;

public class StatAnalyse extends Analyse {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "StatAnalyse";
	}



	@Override
	public void readParam(String params) {
		// TODO Auto-generated method stub
		String[] paramArray = params.split(Separator.PARAM_SEPARATOR1);
		this.setType(Byte.parseByte(paramArray[0]));

	}

	private PVHistory curPVHis = null;
	private Histogram senNumHis = new Histogram();
	private Histogram durationHis = new Histogram();
	private Histogram pathLenghtHis = new Histogram();
	private Histogram pvNumHis = new Histogram();
	private Histogram pageNumHis= new Histogram();


	private List<PVHistory> cachePVHis = new ArrayList<PVHistory>();
	private int index = 0;
	private int saveIndex = 0;
	private int maxCacheNum = 10000;

	private String pvHisPath ="";
	private String fpPath = "";
	private String statPath = "";

	public StatAnalyse() {

		super();
		// TODO Auto-generated constructor stub

		this.senNumHis.setName("logNumHistogram");
		this.senNumHis.setXName("logNum");
		this.senNumHis.setYName("population");
		this.senNumHis.setColumnNum(15);

		this.durationHis.setName("durationHistogram");
		this.durationHis.setXName("time/s");
		this.durationHis.setYName("population");
		this.durationHis.setColumnNum(15);

		this.pathLenghtHis.setName("pathHistogram");
		this.pathLenghtHis.setXName("lenght");
		this.pathLenghtHis.setYName("number");
		this.pathLenghtHis.setColumnNum(15);

		this.pvNumHis.setName("pvNumHistogram");
		this.pvNumHis.setXName("pvNumber");
		this.pvNumHis.setYName("population");
		this.pvNumHis.setColumnNum(15);
		
		this.pageNumHis.setName("pageNumHis");
		this.pageNumHis.setXName("pageNumber");
		this.pageNumHis.setYName("population");
		this.pageNumHis.setColumnNum(15);
	}

	@Override
	public void onInitial() {
		// TODO Auto-generated method stub

		pvHisPath = this.getOutputPath() + "/pvHis_" + this.getType();
		fpPath = this.getOutputPath() + "/fp_" + this.getType();
		statPath = this.getOutputPath() + "/stat_" + this.getType();
		Ulits.newFolder(this.pvHisPath);
		Ulits.newFolder(this.fpPath);
		Ulits.newFolder(this.statPath);
	}

	@Override
	public void onReadEnd() throws IOException {
		// TODO Auto-generated method stub
		this.saveAndClean();
		this.saveHistogram();
		/////////////////////
	}

	@Override
	public void onReadLog(String[] strArray) throws IOException {

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
				//this.cachePVHis.add(index, this.curPVHis);
				this.updateHistogram(this.curPVHis);
				

				this.index++;

				if (this.index > this.maxCacheNum) {
					//System.out.println("save and clean");
					//this.saveAndClean();
					this.saveIndex++;
					index = 0;
				}
				this.curPVHis = new PVHistory();
				this.curPVHis.setId(id);
				this.curPVHis.addLog(strArray);
			}

		}

	}

	public void saveHistogram() {

		this.senNumHis.build();
		this.senNumHis.saveXML(statPath + "/senNumHis.xml");
		this.durationHis.build();
		this.durationHis.saveXML(statPath + "/durationHis.xml");
		this.pathLenghtHis.build();
		this.pathLenghtHis.saveXML(statPath + "/pathHis.xml");
		this.pvNumHis.build();
		this.pvNumHis.saveXML(statPath + "/pvNumHis.xml");
		this.pageNumHis.build();
		this.pageNumHis.saveXML(statPath+"/pageNumHis.xml");
	}

	public void saveAndClean() throws IOException {

	
//		FileWriter fw = new FileWriter(this.pvHisPath + "/" + this.saveIndex
//				+ ".txt");
//		BufferedWriter writer = new BufferedWriter(fw);
//		for (PVHistory s : this.cachePVHis) {
//			writer.write(s.toString());
//		}
//		writer.flush();
//		writer.close();
//
//		FileWriter fw1 = new FileWriter(this.fpPath + "/" + this.saveIndex
//				+ ".txt");
//		BufferedWriter writer1 = new BufferedWriter(fw1);
//		for (String s : this.cacheFrePathList) {
//			writer1.write(s);
//		}
//		writer1.flush();
//		writer1.close();
//
//		this.cacheFrePathList.clear();
//		this.cachePVHis.clear();

	}

	public void updateHistogram(PVHistory his) {

		int a = his.getSessionMap().size();
		this.senNumHis.getDataList().add(a);
		int duration = his.getDuration();
		this.durationHis.getDataList().add(duration);
		this.pvNumHis.getDataList().add(his.getPvNum());
		
		for (String s : his.getPathString()) {
			String []pageArray=s.split(",");
			this.pathLenghtHis.getDataList().add(pageArray.length);
			Set<String> set=new HashSet<String>();
			for(String page:pageArray){
				set.add(page);
			}
			int num=set.size()-2;
			if(num<0){
				num=0;
			}
			this.pageNumHis.getDataList().add(num);
			// System.out.println(s);
		}

	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss", java.util.Locale.US);
		try {
			// TimeZone.getTimeZone("GMT+8");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
			java.util.Date d = sdf.parse("2011-06-04 06:35:50");
			java.util.Date d1 = sdf.parse("2011-06-04 12:26:05");

			long l = d.getTime();
			long l1 = d1.getTime();

			System.out.println((l1));
			System.out.println(d1.toLocaleString());
			int time = (int) (l1 / 1000);
			System.out.println(time);
			long ltime = (long) time * 1000;
			System.out.println(ltime);
			java.util.Date dd = new java.util.Date(ltime);
			System.out.println(dd.toLocaleString());

			// LogToTransaction t = new LogToTransaction();
			// t.addLog("E:/data/pagevisit/pagevisit_2011061130.txt");
			// t.savePerHis("E:/data/pagevisit/his.txt");

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getMaxCacheNum() {
		return maxCacheNum;
	}

	public void setMaxCacheNum(int maxCacheNum) {
		this.maxCacheNum = maxCacheNum;
	}

}
