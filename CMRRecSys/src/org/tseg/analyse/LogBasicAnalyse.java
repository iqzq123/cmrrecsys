package org.tseg.analyse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.tseg.algorithm.fp.*;
import org.tseg.model.Histogram;
import org.tseg.model.PVHistory;
import org.tseg.model.Visit;


public class LogBasicAnalyse {

	private HashMap<Long, PVHistory> personHisMap = new HashMap<Long, PVHistory>();

	public void saveSenNumHistogram(String fileName)throws Exception {

		Iterator iter = personHisMap.entrySet().iterator();
		Histogram his = new Histogram();
		his.setName("logNumHistogram");
		his.setXName("logNum");
		his.setYName("population");
		his.setColumnNum(10);

		while (iter.hasNext()) {

			Map.Entry entry = (Map.Entry) iter.next();
			PVHistory val = (PVHistory) entry.getValue();
			his.getDataList().add(val.getSessionMap().size());
		}
		his.build();
		his.saveXML(fileName);

	}

	public void saveDurationHistogram(String fileName)throws Exception {

		Iterator iter = personHisMap.entrySet().iterator();
		Histogram his = new Histogram();
		his.setName("durationHistogram");
		his.setXName("time/s");
		his.setYName("population");
		his.setColumnNum(10);

		while (iter.hasNext()) {

			Map.Entry entry = (Map.Entry) iter.next();
			PVHistory val = (PVHistory) entry.getValue();
			int totalTime = 0;
			totalTime += val.getDuration();
			his.getDataList().add(totalTime);
		}
		his.build();
		his.saveXML(fileName);

	}

	void readLog(String[] strArray) throws IOException {

		Long id = Long.parseLong(strArray[0]);
		if (personHisMap.get(id) == null) {
			PVHistory his = new PVHistory();
			his.setId(id);
			personHisMap.put(id, his);
		}
		PVHistory his = personHisMap.get(id);
		Visit v = new Visit(strArray);
		String sessionID = strArray[11];
		List list = his.getSessionMap().get(sessionID);
		if (list != null) {
			list.add(v);
			his.getSessionMap().put(sessionID, list);
		} else {
			List l = new ArrayList();
			l.add(v);
			his.getSessionMap().put(sessionID, l);
		}

	}

	public void getFrePath(String fileName) throws IOException {

		FileWriter fw = new FileWriter(fileName);
		BufferedWriter writer = new BufferedWriter(fw);

		Iterator iter = personHisMap.entrySet().iterator();

		while (iter.hasNext()) {

			Map.Entry entry = (Map.Entry) iter.next();
			Long key = (Long) entry.getKey();
			PVHistory val = (PVHistory) entry.getValue();
			String s = key + "\n";
			try {
				GenFAP g = new GenFAP();

				List<String> l1 = val.getPathString();

				// s+=l1.toString();
//				for (String str : l1) {
//					s += str + "\n";
//				}
//				s += "..................................\n";
				g.setInput(l1);
				g.readFromlist();
				g.run();
				List<String> l = g.getFrePath(3);

				for (String p : l) {
					System.out.println(p);
					s += p + "\n";
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			writer.write(s);
		}
		writer.flush();
		writer.close();

	}

	public void savePaths(String fileName) throws IOException {

		FileWriter fw = new FileWriter(fileName);
		BufferedWriter writer = new BufferedWriter(fw);

		Iterator iter = personHisMap.entrySet().iterator();

		while (iter.hasNext()) {

			Map.Entry entry = (Map.Entry) iter.next();
			Long key = (Long) entry.getKey();
			PVHistory val = (PVHistory) entry.getValue();
			String s = key + "\n";
			try {
				List<String> l1 = val.getPathString();
				// s+=l1.toString();
				for (String str : l1) {
					int size = str.split(",").length;
					s += str + "\n";
					s += size + "\n";

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			writer.write(s);
		}
		writer.flush();
		writer.close();

	}

	public void savePerHis(String fileName) throws IOException {

		FileWriter fw = new FileWriter(fileName);
		BufferedWriter writer = new BufferedWriter(fw);

		Iterator iter = personHisMap.entrySet().iterator();

		while (iter.hasNext()) {

			Map.Entry entry = (Map.Entry) iter.next();
			Long key = (Long) entry.getKey();
			PVHistory val = (PVHistory) entry.getValue();
			writer.write(val.toString());
		}
		writer.flush();
		writer.close();

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

	public void setPersonHisMap(HashMap<Long, PVHistory> personHisTable) {
		this.personHisMap = personHisTable;
	}

	public HashMap<Long, PVHistory> getPersonHisMap() {
		return personHisMap;
	}

}
