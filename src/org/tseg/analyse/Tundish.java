package org.tseg.analyse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.tseg.Starter;
import org.tseg.Ulits.AnalyseType;
import org.tseg.Ulits.Separator;
import org.tseg.Ulits.Ulits;
import org.tseg.model.PVHistory;
import org.tseg.model.Visit;
import org.tseg.preprocess.Preprocessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class Tundish extends Analyse {
	private static final String NAME = "TundishClass";
	private String localOutputPath = "";
	private PVHistory curPVHis = null;
	private List<String> pathList = new ArrayList<String>(); 	// //////////格式为 a,b,c,d
	private List<String> tundishList = new ArrayList<String>();		// //////////格式为 a,1,b,0.6,c,0.2
	private ArrayList tundishResults = new ArrayList();
	private ArrayList tundishResultsContinuous = new ArrayList();
	private Boolean isDiscrete = false;
	private Boolean isContinuous = false;
	
	class Level {
		String name;
		int cnt;
		public Level(String name, int cnt) {
			this.name = name;
			this.cnt = cnt;
		}
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public void readParam(String params) {
		// TODO Auto-generated method stub
		System.out.println("Tundish:readParam:"+params);
		String[] pArr = params.split(Separator.PARAM_SEPARATOR1);
		this.setType(Byte.parseByte(pArr[0]));
		int isDiscrete  = Integer.valueOf(pArr[1]);
		if ( isDiscrete != 0 )
			this.isDiscrete = true;
		else 
			this.isDiscrete = false;
		int isContinuous = Integer.valueOf(pArr[2]);
		if ( isContinuous != 0 )
			this.isContinuous = true;
		else 
			this.isContinuous = false;
		String[] pathArr = pArr[3].split(Separator.PARAM_SEPARATOR2);
		setPathList(pathArr);
	}
	
	void readPathList(String fileName) throws IOException {
		FileReader fr = new FileReader(fileName);
		BufferedReader reader = new BufferedReader(fr);
		String str = "";
		while ((str = reader.readLine()) != null) {
			System.out.println(str);
			this.pathList.add(str);
		}
		initResult(this.pathList);
	}

	public void saveTundishList() {
		try {
			FileWriter fw = new FileWriter(this.localOutputPath+"/tundish.txt");
			BufferedWriter writer = new BufferedWriter(fw);
			Iterator<ArrayList> iter = this.tundishResults.iterator();
			while (iter.hasNext()) {
				ArrayList<Level> vector = iter.next();
				Iterator<Level> iter2 = vector.iterator();
				int total = vector.get(0).cnt;
				String s = "";
				while (iter2.hasNext()) {
					Level level = iter2.next();
					float per = 0;
					System.out.println(level.name + " " + level.cnt);
					if (total != 0)
						per = (float) level.cnt / total;
					else
						per = 0;
					s += level.name + "," + per + ",";
				}
				System.out.println(s);
				writer.write(s + '\n');
			}
			writer.flush();
			writer.close();
			fw.close();
		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}
	}
	public void saveTundishListXML() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (Exception pce) {
			System.err.println(pce);
		}
		Document doc = db.newDocument();
		// 在doc中创建"学生花名册"tag作为根节点
		Element root = doc.createElement("Tundish");
		doc.appendChild(root);
		
		Element name = doc.createElement("Name");
		name.setAttribute("name","TundishTask");
		root.appendChild(name);
		
		Element results=doc.createElement("results");
		root.appendChild(results);
		Iterator<ArrayList> iter = this.tundishResults.iterator();
		while (iter.hasNext()) {
			ArrayList<Level> vector = iter.next();
			Iterator<Level> iter2 = vector.iterator();
			int total = vector.get(0).cnt;
			String s = "";
			Element result=doc.createElement("result");
			while (iter2.hasNext()) {
				Element levelNode=doc.createElement("level");
				Level level = iter2.next();
				float per = 0;
				System.out.println(level.name + " " + level.cnt);
				if (total != 0)
					per = (float) level.cnt / total;
				else
					per = 0;
				levelNode.setAttribute("name", level.name);
				levelNode.setAttribute("proportion", String.valueOf(per));
				result.appendChild(levelNode);
			}
			System.out.println(s);
			results.appendChild(result);
		}
		try {
			// 用xmlserializer把document的内容进行串化
			FileOutputStream os = null;
			OutputFormat outformat = new OutputFormat(doc);
			os = new FileOutputStream(this.localOutputPath+"/tundish.xml");
			XMLSerializer xmlSerilizer = new XMLSerializer(os, outformat);
			xmlSerilizer.serialize(doc);

		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}
			
	}
	public void saveTundishListContinuous() {
		try {
			System.out.println("Continuous");
			FileWriter fw = new FileWriter(this.localOutputPath+"/tundishContinuous.txt");
			BufferedWriter writer = new BufferedWriter(fw);
			Iterator<ArrayList> iter = this.tundishResultsContinuous.iterator();
			while (iter.hasNext()) {
				ArrayList<Level> vector = iter.next();
				Iterator<Level> iter2 = vector.iterator();
				int total = vector.get(0).cnt;
				String s = "";
				while (iter2.hasNext()) {
					Level level = iter2.next();
					float per = 0;
					System.out.println(level.name + " " + level.cnt);
					if (total != 0)
						per = (float) level.cnt / total;
					else
						per = 0;
					s += level.name + "," + per + ",";
				}
				System.out.println(s);
				writer.write(s + '\n');
			}
			writer.flush();
			writer.close();
			fw.close();
		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}
	}
	public void saveTundishListContinuousXML() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (Exception pce) {
			System.err.println(pce);
		}
		Document doc = db.newDocument();
		// 在doc中创建"学生花名册"tag作为根节点
		Element root = doc.createElement("Tundish");
		doc.appendChild(root);
		
		Element name = doc.createElement("Name");
		name.setAttribute("name","TundishTask");
		root.appendChild(name);
		
		Element results=doc.createElement("results");
		root.appendChild(results);
		Iterator<ArrayList> iter = this.tundishResultsContinuous.iterator();
		while (iter.hasNext()) {
			ArrayList<Level> vector = iter.next();
			Iterator<Level> iter2 = vector.iterator();
			int total = vector.get(0).cnt;
			String s = "";
			Element result=doc.createElement("result");
			while (iter2.hasNext()) {
				Element levelNode=doc.createElement("level");
				Level level = iter2.next();
				float per = 0;
				System.out.println(level.name + " " + level.cnt);
				if (total != 0)
					per = (float) level.cnt / total;
				else
					per = 0;
				levelNode.setAttribute("name", level.name);
				levelNode.setAttribute("proportion", String.valueOf(per));
				result.appendChild(levelNode);
			}
			System.out.println(s);
			results.appendChild(result);
		}
		try {
			// 用xmlserializer把document的内容进行串化
			FileOutputStream os = null;
			OutputFormat outformat = new OutputFormat(doc);
			os = new FileOutputStream(this.localOutputPath+"/tundishContinuous.xml");
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
		System.out.println("type "+type);
		Ulits.newFolder(this.getOutputPath() + "/Tundish");
		if ( type.equals(AnalyseType.Original) ){
			this.localOutputPath = this.getOutputPath() + "/Tundish/FullName";
		}
		else if ( type.equals(AnalyseType.NegCate) ){
			this.localOutputPath = this.getOutputPath() + "/Tundish/ShortName";
		}
		else if ( type.equals(AnalyseType.PageToCate) ){
			this.localOutputPath = this.getOutputPath() + "/Tundish/Category";
		}
		Ulits.newFolder(this.localOutputPath);
		System.out.println("hi"+this.localOutputPath);

	}

	@Override
	public void onReadEnd() throws IOException {
		// TODO Auto-generated method stub
		if ( this.isDiscrete ){
			this.saveTundishList();
			this.saveTundishListXML();
		}
		if ( this.isContinuous ){
			this.saveTundishListContinuous();
			this.saveTundishListContinuousXML();
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
				if ( this.isDiscrete )
					onReadHis(this.curPVHis);
				if ( this.isContinuous )
					onReadHisContinuous(this.curPVHis);
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
	private void onReadHis(PVHistory his) {
		Iterator iter = his.getPathString().iterator();
		// 遍历每个Session
		while (iter.hasNext()) {
			String path = (String) iter.next();
			String[] visits = path.split(Separator.pathSeparator);
			// 初始化每个漏斗的层数标记，最顶层为0
			int[] indexs = new int[tundishResults.size()];
			// this.s += "===============\n";
			// 遍历Session中的路径
			for (int i = 0; i < visits.length; i++) {
				// 遍历每个漏斗
				String visit = Preprocessor.getPageName(visits[i]);
				// this.s += visits[i]+'\n';
				for (int seq = 0; seq < tundishResults.size(); seq++) {
					// 目标漏斗
					ArrayList tundishResult = (ArrayList) tundishResults.get(seq);
					Level level0 = (Level) tundishResult.get(0);
					// 目标层
					Level level = (Level) tundishResult.get(indexs[seq]);
					if (visit.equals(level0.name)) {
						level0.cnt++;
						indexs[seq] = 1;
						// 一次漏斗结束后
						if (indexs[seq] == tundishResult.size())
							indexs[seq] = 0;
					} else {
						if (visit.equals(level.name)) {
							level.cnt++;
							indexs[seq]++;
							// 如果一次漏斗结束后,比较层数重置到第0层
							if (indexs[seq] == tundishResult.size())
								indexs[seq] = 0;
						}
					}
				}
			}
		}
	}

	private void onReadHisContinuous(PVHistory his) {
		Iterator iter = his.getPathString().iterator();
		// 遍历每个Session
		while (iter.hasNext()) {
			String path = (String) iter.next();
			String[] visits = path.split(",");
			// 初始化每个漏斗的层数标记，最顶层为0
			int[] indexs = new int[tundishResultsContinuous.size()];
			// 遍历Session中的路径
			for (int i = 0; i < visits.length; i++) {
				//System.out.println(visits[i]);
				String visit = Preprocessor.getPageName(visits[i]);
				// 遍历每个漏斗
				for (int seq = 0; seq < tundishResultsContinuous.size(); seq++) {
					// 目标漏斗
					ArrayList tundishResult = (ArrayList) tundishResultsContinuous
							.get(seq);
					Level level0 = (Level) tundishResult.get(0);
					// 目标层
					Level level = (Level) tundishResult.get(indexs[seq]);
					if (visit.equals(level0.name)) {
						level0.cnt++;
						indexs[seq] = 1;
						// 一次漏斗结束后
						if (indexs[seq] == tundishResult.size())
							indexs[seq] = 0;
					} else {
						if (visit.equals(level.name)) {
							level.cnt++;
							indexs[seq]++;
							// 如果一次漏斗结束后,比较层数重置到第0层
							if (indexs[seq] == tundishResult.size())
								indexs[seq] = 0;
						} else {
							indexs[seq] = 0;
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
//		AnalyseRunner b = new AnalyseRunner();
//		b.setNegCate(true);
//		// ReplaceFilter r=new ReplaceFilter();
//		try {
//			b.setInputPath("E:/data/pagevisit/pv6.txt");
//			b.setOutputPath("E:/data/pagevisit/pv6.txt.out");
//			
//			b.setSiteDataPath("E:/data");
//
//			Tundish td = new Tundish();
//			td.readPathList("E:/data/pagevisit/path.txt");
//			b.addAnalyse(td);
//
//			b.seqRun();
//			System.out.println("success");
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Starter s=new Starter();
		s.setInputPath("E:/data/pagevisit/test.txt");
		s.setOutputPath("E:/data/pagevisit/testout.txt");
		s.setSiteDataPath("E:/data");
		s.setNegCate(true);
		try{
			//s.start("TundishClass	1?	|?1?	|?1?	|?login*;	|;手机阅读阅读页;	|;我的书架");
			s.start("TundishClass"+Separator.cmdSeparator + AnalyseType.NegCate + 
					Separator.PARAM_SEPARATOR1 + 1 + Separator.PARAM_SEPARATOR1 + 
					1 + Separator.PARAM_SEPARATOR1 + "login*" + 
					Separator.PARAM_SEPARATOR3 + "手机阅读阅读页" + 
					Separator.PARAM_SEPARATOR3 + "我的书架");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public List<String> getPathList() {
		return pathList;
	}

	public void setPathList(List<String> pathList) {
		this.pathList = pathList;
	}
	
	public void setPathList(String[] pathList) {
		this.pathList.clear();
		for ( int i = 0 ; i < pathList.length ; i++ ){
			this.pathList.add(pathList[i]);
		}
		initResult(this.pathList);
	}
	
	private void initResult(List<String> pathList){
		this.tundishResults.clear();
		this.tundishResultsContinuous.clear();
		for ( int i = 0 ; i < this.pathList.size() ; i++ ){
			String[] strArray = this.pathList.get(i).split(Separator.PARAM_SEPARATOR3);
			ArrayList v = new ArrayList();
			ArrayList vc = new ArrayList();
			for (int j = 0; j < strArray.length; j++) {
				v.add(new Level(strArray[j], 0));
				vc.add(new Level(strArray[j], 0));
			}
			tundishResults.add(v);
			tundishResultsContinuous.add(vc);
		}
	}

	public List<String> getTundishList() {
		return tundishList;
	}

	public void setTundishList(List<String> tundishList) {
		this.tundishList = tundishList;
	}

}
