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
	//private final String SEPARATOR_STR = ";\t|;";
	private String localOutputPath = "";
	private final int MAX_LENGTH = 100;
	private final int MIN_LENGTH = 2;
	private PVHistory curPVHis = null;
	private ArrayList<EndPoint> endPoints = new ArrayList<EndPoint>();
	private ArrayList<HashMap> hasPathAL = new ArrayList<HashMap>();
	private int minLength = 0;
	private int maxLength = MAX_LENGTH;

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
		//byte a=Byte.valueOf(pArr[0]);
		this.setType(Byte.parseByte(pArr[0]));
		this.minLength = Integer.valueOf(pArr[1]);
		this.maxLength = Integer.valueOf(pArr[2]);
		if (this.minLength < MIN_LENGTH)
			this.minLength = MIN_LENGTH;
		if (this.maxLength < this.minLength) {
			this.maxLength = MAX_LENGTH;
			this.minLength = MIN_LENGTH;
		}
		String[] endPointsArr = pArr[3].split(Separator.PARAM_SEPARATOR2);
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
			HashMap hasPath = new HashMap();
			this.hasPathAL.add(hasPath);
		}
		reader.close();
		fr.close();
	}

	public void savePathList() {
		try {
			System.out.println(this.localOutputPath + "/findPath.txt");
			FileWriter fw = new FileWriter(this.localOutputPath
					+ "/findPath.txt");
			BufferedWriter writer = new BufferedWriter(fw);
			Iterator<EndPoint> iter = this.endPoints.iterator();
			while (iter.hasNext()) {
				EndPoint ep = iter.next();
				for (String s : ep.pathList) {
					String newStr = s.replace(Separator.PARAM_SEPARATOR3, ",");
					writer.write(newStr + '\n');
					System.out.println(newStr);
				}
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
		Element root = doc.createElement("Graph");
		doc.appendChild(root);
		Element nodes = doc.createElement("Nodes");
		root.appendChild(nodes);
		Element edges = doc.createElement("Edges");
		root.appendChild(edges);
		Iterator<EndPoint> iter = this.endPoints.iterator();
		int index = 0;
		String prePage = "";
		String[] pages;
		String fromPage = "";
		// String toPage = "";
		Element node;
		// 每一对端点的结果
		while (iter.hasNext()) {
			EndPoint ep = iter.next();
			if (ep.pathList.size() == 0)
				break;
			pages = ep.pathList.get(0).split(Separator.PARAM_SEPARATOR3);
			if (pages.length == 0)
				continue;
			node = doc.createElement("Node");
			fromPage = pages[0] + "(p" + index + ")";
			node.setAttribute("id", fromPage);
			node.setAttribute("name", pages[0]);
			node.setAttribute("type", "from");
			nodes.appendChild(node);
			int seq = 0;
			for (int i = 0; i < ep.pathList.size(); i++) {
				pages = ep.pathList.get(i).split(Separator.PARAM_SEPARATOR3);
				String weight = this.hasPathAL.get(index).get(
						ep.pathList.get(i)).toString();
				for (int j = 1; j < pages.length; j++) {
					String curPage = pages[j] + "(p" + index + "," + seq + ")";
					node = doc.createElement("Node");
					node.setAttribute("id", curPage);
					node.setAttribute("name", pages[j]);
					if (j < pages.length - 1)
						node.setAttribute("type", "middle");
					else
						node.setAttribute("type", "to");
					nodes.appendChild(node);
					if (j == 1) {
						Element edge = doc.createElement("Edge");
						edge.setAttribute("fromID", fromPage);
						edge.setAttribute("toID", curPage);
						edge.setAttribute("weight", weight);
						edges.appendChild(edge);
					} else {
						Element edge = doc.createElement("Edge");
						edge.setAttribute("fromID", prePage);
						edge.setAttribute("toID", curPage);
						edge.setAttribute("weight", weight);
						edges.appendChild(edge);
					}
					prePage = curPage;
					seq++;
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

	public void savePathListXML2() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (Exception pce) {
			System.err.println(pce);
		}
		Document doc = db.newDocument();
		Element root = doc.createElement("Graph");
		doc.appendChild(root);
		Element nodes = doc.createElement("Nodes");
		root.appendChild(nodes);
		Element edges = doc.createElement("Edges");
		root.appendChild(edges);
		Iterator<EndPoint> iter = this.endPoints.iterator();
		int index = 0;
		String prePage = "";
		String[] pages;
		String fromPage = "";
		String toPage = "";
		Element node;
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
		// 每一对端点的结果
		while (iter.hasNext()) {
			EndPoint ep = iter.next();
			if (ep.pathList.size() == 0)
				break;
			pages = ep.pathList.get(0).split(Separator.PARAM_SEPARATOR3);
			if (pages.length == 0)
				continue;
			node = doc.createElement("Node");
			fromPage = pages[0] + "(p" + index + ")";
			node.setAttribute("id", fromPage);
			node.setAttribute("name", pages[0]);
			node.setAttribute("type", "from");
			nodes.appendChild(node);
			// int seq = 0;
			// 每一条路径
			for (int i = 0; i < ep.pathList.size(); i++) {
				pages = ep.pathList.get(i).split(Separator.PARAM_SEPARATOR3);
				// this.hasPathAL.get(index).get(ep.pathList.get(i)).toString();
				int seq = 0;
				for (int j = 1; j < pages.length; j++) {
					String curPage = pages[j] + "(p" + (seq + 1) + ")";// pages[j]+"(p"+index+","+seq+")";
					// 去重
					if (hashMap.containsKey(curPage) == true) {
						prePage = curPage;
						seq++;
						continue;
					} else
						hashMap.put(curPage, 1);
					// 计算边权值
					String weight = "";
					int w = 0;
					for (int k = 0; k < ep.pathList.size(); k++) {
						String[] tempPages = ep.pathList.get(k).split(
								Separator.PARAM_SEPARATOR3);
						if (tempPages.length > j) {
							if (tempPages[j].equals(pages[j])) {
								w += Integer.parseInt(this.hasPathAL.get(index)
										.get(ep.pathList.get(k)).toString());
							}
						}
					}
					weight = String.valueOf(w);
					// 构造节点
					node = doc.createElement("Node");
					node.setAttribute("id", curPage);
					node.setAttribute("name", pages[j]);
					if (j < pages.length - 1)
						node.setAttribute("type", "middle");
					else
						node.setAttribute("type", "to");
					nodes.appendChild(node);
					// 构造边
					if (j == 1) {
						Element edge = doc.createElement("Edge");
						edge.setAttribute("fromID", fromPage);
						edge.setAttribute("toID", curPage);
						edge.setAttribute("weight", weight);
						edges.appendChild(edge);
					} else {
						Element edge = doc.createElement("Edge");
						edge.setAttribute("fromID", prePage);
						edge.setAttribute("toID", curPage);
						edge.setAttribute("weight", weight);
						edges.appendChild(edge);
					}
					prePage = curPage;
					seq++;
				}
			}
			index++;
		}
		try {
			// 用xmlserializer把document的内容进行串化
			FileOutputStream os = null;
			OutputFormat outformat = new OutputFormat(doc);
			System.out.println("hehehe");
			os = new FileOutputStream(this.localOutputPath + "/findPathAgg.xml");
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
		this.savePathList();
		this.savePathListXML();
		this.savePathListXML2();
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
	
		Iterator iter = his.getPathString().iterator();
		int[] states = new int[this.endPoints.size()];
		String[] targetPathes = new String[this.endPoints.size()];
		// 遍历每个Session
		while (iter.hasNext()) {
			String path = (String) iter.next();
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
							targetPathes[index] += Separator.PARAM_SEPARATOR3 + visit;
						} else {
							states[index] = 0;
							targetPathes[index] += Separator.PARAM_SEPARATOR3 + visit;
							HashMap hasPath = (HashMap) this.hasPathAL
									.get(index);
							int length = targetPathes[index]
									.split(Separator.PARAM_SEPARATOR3).length;

							if (length >= minLength && length <= maxLength) {
								// System.out.println(path);
								if (hasPath.get(targetPathes[index]) == null) {
									hasPath.put(targetPathes[index], 1);
									ep.pathList.add(targetPathes[index]);
								} else {
									int cnt = Integer.parseInt(hasPath.get(
											targetPathes[index]).toString());
									cnt++;
									hasPath.put(targetPathes[index], cnt);
								}
							}
						}
						break;
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// AnalyseRunner b = new AnalyseRunner();
		// // ReplaceFilter r=new ReplaceFilter();
		// try {
		//
		// b.setInputPath("E:/data/pagevisit/test.txt");
		// b.setSiteDataPath("E:/data");
		// PathFinder fp = new PathFinder();
		// fp.readEndPointList("E:/data/pagevisit/endPoints.txt");
		// //fp.setEndPoints(epAL) //直接配置端点参数
		// fp.setMinLength(2); //长度包括起始点
		// fp.setMaxLength(10);
		// b.addAnalyse(fp);
		//
		// b.seqRun();
		// System.out.println("success");
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		Starter s = new Starter();
		s.setInputPath("E:/data/pagevisit/test.txt");
		s.setOutputPath("E:/data/pagevisit/testout.txt");
		s.setSiteDataPath("E:/data");
		//s.setNegCate(true);
		try {
			String ss="PathFinderClass" + Separator.cmdSeparator
			+ AnalyseType.Original + Separator.PARAM_SEPARATOR1 + 2
			+ Separator.PARAM_SEPARATOR1 + 100
			+ Separator.PARAM_SEPARATOR1 + "login*"
			+ Separator.PARAM_SEPARATOR2 + "费用提示页面";
			s.start("PathFinderClass" + Separator.cmdSeparator
					+ AnalyseType.NegCate + Separator.PARAM_SEPARATOR1 + 2
					+ Separator.PARAM_SEPARATOR1 + 100
					+ Separator.PARAM_SEPARATOR1 + "login*"
					+ Separator.PARAM_SEPARATOR3 + "费用提示页面");
			//s.start("PathFinderClass	0?	|?2?	|?100?	|?login*;	|;费用提示页面");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setEndPoints(String[] epArr) {
		this.endPoints.clear();
		this.hasPathAL.clear();
		for (int i = 0; i < epArr.length; i++) {
			String s = epArr[i];
			EndPoint ep = new EndPoint();
			ep.fromPage = s.split(Separator.PARAM_SEPARATOR3)[0];
			ep.toPage = s.split(Separator.PARAM_SEPARATOR3)[1];
			this.endPoints.add(ep);
			HashMap hasPath = new HashMap();
			this.hasPathAL.add(hasPath);
		}
	}

	public ArrayList<String> getEndPoints() {
		ArrayList<String> epAL = new ArrayList<String>();
		Iterator iter = this.endPoints.iterator();
		while (iter.hasNext()) {
			EndPoint ep = (EndPoint) iter.next();
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
