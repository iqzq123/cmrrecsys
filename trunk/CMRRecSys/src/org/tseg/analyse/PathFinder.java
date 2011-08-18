package org.tseg.analyse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tseg.Starter;
import org.tseg.model.PVHistory;
import org.tseg.model.Visit;
import org.tseg.preprocess.Preprocessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.tseg.Ulits.Separator;
import org.tseg.Ulits.AnalyseType;
import org.tseg.Ulits.Ulits;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class PathFinder extends Analyse {
	private static final String NAME = "PathFinderClass";
	private String localOutputPath = "";
	private final int MIN_LENGTH = 2;
	private final int MAX_LENGTH = Integer.MAX_VALUE;
	private final int MIN_USER = 1;
	private final int MAX_USER = Integer.MAX_VALUE;
	private PVHistory curPVHis = null;
	private ArrayList<EndPoint> endPoints = new ArrayList<EndPoint>();
	private ArrayList<HashMap<String,Integer>> pathCntMapAL = new ArrayList<HashMap<String,Integer>>();
	private int minLength = MIN_LENGTH;
	private int maxLength = MAX_LENGTH;
	private int minUser = MIN_USER;
	private int maxUser = MAX_USER;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public void readParam(String params) {
		// TODO Auto-generated method stub
		System.out.println("inPathFinder:" + params);
		String[] pArr = params.split(Separator.PARAM_SEPARATOR1);
		// byte a=Byte.valueOf(pArr[0]);
		this.setType(Byte.parseByte(pArr[0]));
		this.minLength = Integer.valueOf(pArr[1]);
		this.maxLength = Integer.valueOf(pArr[2]);
		if (this.minLength < MIN_LENGTH)
			this.minLength = MIN_LENGTH;
		if (this.maxLength < this.minLength) {
			this.maxLength = MAX_LENGTH;
		}
		this.minUser = Integer.valueOf(pArr[3]);
		this.maxUser = Integer.valueOf(pArr[4]);
		if (this.minUser < MIN_USER)
			this.minUser = MIN_USER;
		if (this.maxUser < this.minUser) {
			this.maxUser = MAX_USER;
		}
		String[] endPointsArr = pArr[5].split(Separator.PARAM_SEPARATOR2);
		setEndPoints(endPointsArr);
	}

	private class EndPoint {
		String fromPage = "";
		String toPage = "";
		ArrayList<String> pathList = new ArrayList<String>();
	}

	public PathFinder() {
		super();
	}

	// 格式
	/*
	 * a,b a,c d,f
	 */
	void readEndPointList(String fileName) throws IOException {
		FileReader fr = new FileReader(fileName);
		BufferedReader reader = new BufferedReader(fr);
		String str = "";
		while ((str = reader.readLine()) != null) {
			String[] strArray = str.split(",");
			if (strArray.length == 0)
				continue;
			EndPoint endPoint = new EndPoint();
			endPoint.fromPage = strArray[0];
			endPoint.toPage = strArray[1];
			this.endPoints.add(endPoint);
			HashMap<String,Integer> pathCnt = new HashMap<String,Integer>();
			this.pathCntMapAL.add(pathCnt);
		}
		reader.close();
		fr.close();
	}

	public void savePathList() {
		try {
			System.out.println(this.NAME + ":savePath:" + this.localOutputPath + "/findPath.txt");
			FileWriter fw = new FileWriter(this.localOutputPath
					+ "/findPath.txt");
			BufferedWriter writer = new BufferedWriter(fw);
			//Iterator<EndPoint> iter = this.endPoints.iterator();
			//while (iter.hasNext()) {
			int index = 0;
			for ( EndPoint ep : this.endPoints ) {
				for (String s : ep.pathList) {
					String newStr = s.replace(Separator.PARAM_SEPARATOR3, ",");
					String weight = this.pathCntMapAL.get(index).get(s).toString();
					writer.write(newStr + "," + weight + '\n');
					System.out.println(newStr + "," + weight);
				}
				index ++;
			}
			writer.flush();
			writer.close();
			fw.close();
		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}
	}
	
	public void savePathListXML() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (Exception pce) {
			System.err.println(pce);
		}
		Document doc = db.newDocument();
		Element root = doc.createElement("PathFinder");
		doc.appendChild(root);
		int index = 0;
		// String toPage = "";
		Element node;
		// 每一对端点的结果
		for ( EndPoint ep: this.endPoints ){
			Element endPointEmt = doc.createElement("EndPoints");
			endPointEmt.setAttribute("fromPage", ep.fromPage);
			endPointEmt.setAttribute("toPage", ep.toPage);
			root.appendChild(endPointEmt);
			for (String path : ep.pathList ) {
				String weight = this.pathCntMapAL.get(index).get(path).toString();
				Element pathEmt = doc.createElement("Path");
				pathEmt.setAttribute("userNum", weight);
				endPointEmt.appendChild(pathEmt);
				String[] pages = path.split(Separator.PARAM_SEPARATOR3);
				for (int i = 0; i < pages.length ; i++) {
					Element pageEmt = doc.createElement("Page");
					pageEmt.setAttribute("pageName", pages[i]);
					pathEmt.appendChild(pageEmt);
				}
			}
			index++;
		}
		try {
			// 用xmlserializer把document的内容进行串化
			FileOutputStream os = null;
			OutputFormat outformat = new OutputFormat(doc);
			os = new FileOutputStream(this.localOutputPath + "/findPath.xml");
			XMLSerializer xmlSerilizer = new XMLSerializer(os, outformat);
			xmlSerilizer.serialize(doc);

		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}

	}

	@Override
	public void onInitial() {
		// TODO Auto-generated method stub
		Byte type = this.getType();
		Ulits.newFolder(this.getOutputPath() + "/PathFinder");
		if (type.equals(AnalyseType.Original)) {
			this.localOutputPath = this.getOutputPath()
					+ "/PathFinder/FullName";
		} else if (type.equals(AnalyseType.NegCate)) {
			this.localOutputPath = this.getOutputPath()
					+ "/PathFinder/ShortName";
		} else if (type.equals(AnalyseType.PageToCate)) {
			this.localOutputPath = this.getOutputPath()
					+ "/PathFinder/Category";
		}
		Ulits.newFolder(this.localOutputPath);
		System.out.println(this.localOutputPath);
	}

	@Override
	public void onReadEnd() throws IOException {
		System.out.println("PathFinder: onReadEnd");
		this.userNumFilter();
		this.savePathList();
		this.savePathListXML();
	}
	
	private void userNumFilter() {
		int index = 0;
		for ( EndPoint ep : this.endPoints ) {
			ArrayList<String> newPathList = new ArrayList<String>();
			for (String s : ep.pathList) {
				String weight = this.pathCntMapAL.get(index).get(s).toString();
				int w = Integer.parseInt(weight);
				if ( w >= this.minUser && w <= this.maxUser ){
					newPathList.add(s);
				}
			}
			ep.pathList = newPathList;
			index ++;
		}
	}
	@Override
	public void onReadLog(String[] log) throws IOException {
		// TODO Auto-generated method stub
		Long id = Long.parseLong(log[0]);
		if (this.curPVHis == null) {
			this.curPVHis = new PVHistory();
			this.curPVHis.setId(id);
			String sessionID = log[11];
			Visit v = new Visit(log);
			List<Visit> l = new ArrayList<Visit>();
			l.add(v);
			this.curPVHis.getSessionMap().put(sessionID, l);

		} else {
			if (this.curPVHis.getId() == id) {
				Visit v = new Visit(log);
				String sessionID = log[11];
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
				// System.out.println("onReadHis");
				onReadHis(this.curPVHis, this.minLength, this.maxLength);
				// ///////////////////////////////////////////////////
				this.curPVHis = new PVHistory();
				this.curPVHis.setId(id);
				String sessionID = log[11];
				Visit v = new Visit(log);
				List<Visit> l = new ArrayList<Visit>();
				l.add(v);
				this.curPVHis.getSessionMap().put(sessionID, l);
			}
		}

	}

	private void onReadHis(PVHistory his, int minLength, int maxLength) {
		int[] states = new int[this.endPoints.size()];
		String[] targetPathes = new String[this.endPoints.size()];
		HashMap<String,Integer> rmDuplicatesMap = new HashMap<String,Integer>();
		// 遍历每个Session
		for (String path : his.getPathString()) {
			String[] visits = path.split(Separator.pathSeparator);
			for (int i = 0; i < states.length; i++) {
				states[i] = 0;
				targetPathes[i] = "";
			}
			// 遍历Session中的路径
			for (int i = 0; i < visits.length; i++) {
				// 遍历每条指定的起始终止点
				String visit = Preprocessor.getPageName(visits[i]);
				// System.out.println("visit:"+visit);
				for (int index = 0; index < this.endPoints.size(); index++) {
					EndPoint ep = this.endPoints.get(index);
					switch (states[index]) {
					case 0:
						if (visit.equals(ep.fromPage)) {
							states[index] = 1;
							targetPathes[index] = visit;
						}
						break;
					case 1:
						if (visit.equals(ep.fromPage)) {
							states[index] = 1;
							targetPathes[index] = visit;
						} else if (!visit.equals(ep.toPage)) {
							states[index] = 1;
							targetPathes[index] += Separator.PARAM_SEPARATOR3
									+ visit;
						} else {
							states[index] = 0;
							targetPathes[index] += Separator.PARAM_SEPARATOR3
									+ visit;
							if ( rmDuplicatesMap.get(targetPathes[index]) == null ){
								rmDuplicatesMap.put(targetPathes[index], 1);
							
								HashMap<String,Integer> pathCntMap = (HashMap<String,Integer>) this.pathCntMapAL
										.get(index);
								int length = targetPathes[index]
										.split(Separator.PARAM_SEPARATOR3).length;
	
								if (length >= minLength && length <= maxLength) {
									// System.out.println(path);
									if ( pathCntMap.get(targetPathes[index]) == null ) {
										pathCntMap.put(targetPathes[index], 1);
										ep.pathList.add(targetPathes[index]);
									} else {
										int cnt = Integer.parseInt(pathCntMap.get(
												targetPathes[index]).toString());
										cnt++;
										pathCntMap.put(targetPathes[index], cnt);
									}
								}
							}
//							else {
//								System.out.println("id:"+his.getId()+" pvNum:"+his.getPvNum()+" "+targetPathes[index]);
//							}
						}
						break;
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		Starter s = new Starter();
		s.setInputPath("E:/data/pagevisit/test.txt");
		s.setOutputPath("E:/data/pagevisit/testout.txt");
		s.setSiteDataPath("E:/data");
		try {
			s.start("PathFinderClass" + Separator.cmdSeparator
					+ AnalyseType.NegCate + Separator.PARAM_SEPARATOR1 + 2
					+ Separator.PARAM_SEPARATOR1 + 100
					+ Separator.PARAM_SEPARATOR1 + 1
					+ Separator.PARAM_SEPARATOR1 + 0
					+ Separator.PARAM_SEPARATOR1 + "login*"
					+ Separator.PARAM_SEPARATOR3 + "费用提示页面");
			 //s.start("PathFinderClass	1@@@2@@@5@@@2@@@0@@@login*###费用提示页面");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setEndPoints(String[] epArr) {
		this.endPoints.clear();
		this.pathCntMapAL.clear();
		for (int i = 0; i < epArr.length; i++) {
			String s = epArr[i];
			EndPoint ep = new EndPoint();
			ep.fromPage = s.split(Separator.PARAM_SEPARATOR3)[0];
			ep.toPage = s.split(Separator.PARAM_SEPARATOR3)[1];
			this.endPoints.add(ep);
			HashMap<String,Integer> pathCnt = new HashMap<String,Integer>();
			this.pathCntMapAL.add(pathCnt);
		}
	}

	public ArrayList<String> getEndPoints() {
		ArrayList<String> epAL = new ArrayList<String>();
		for ( EndPoint ep:this.endPoints ) {
			String s = ep.fromPage + "," + ep.toPage;
			epAL.add(s);
		}
		return epAL;
	}

	public void setMinLength(int n) {
		this.minLength = n;
	}

	public int getMinLength() {
		return this.minLength;
	}

	public void setMaxLength(int n) {
		this.maxLength = n;
	}

	public int getMaxLength() {
		return this.maxLength;
	}
}
