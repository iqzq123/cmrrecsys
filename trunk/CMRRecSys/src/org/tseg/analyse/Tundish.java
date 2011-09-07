package org.tseg.analyse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

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
	private List<String> pathList = new ArrayList<String>(); // //////////格式为
																// a,b,c,d
	private List<String> tundishList = new ArrayList<String>(); // //////////格式为
																// a,1,b,0.6,c,0.2
	private ArrayList<ArrayList<Level>> tundishResults = new ArrayList<ArrayList<Level>>();
	private ArrayList<ArrayList<Level>> tundishResultsContinuous = new ArrayList<ArrayList<Level>>();
	private Boolean isDiscrete = false;
	private Boolean isContinuous = false;

	class Level {
		String name;
		int cnt;
		HashMap<String, Integer> runOffMap = new HashMap<String, Integer>();

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
		System.out.println("Tundish:readParam:" + params);
		String[] pArr = params.split(Separator.PARAM_SEPARATOR1);
		this.setType(Byte.parseByte(pArr[0]));
		int isDiscrete = Integer.valueOf(pArr[1]);
		if (isDiscrete != 0)
			this.isDiscrete = true;
		else
			this.isDiscrete = false;
		int isContinuous = Integer.valueOf(pArr[2]);
		if (isContinuous != 0)
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
			FileWriter fw = new FileWriter(this.localOutputPath
					+ "/漏斗.txt");
			BufferedWriter writer = new BufferedWriter(fw);
			for (ArrayList<Level> vector : this.tundishResults) {
				Iterator<Level> iter2 = vector.iterator();
				int total = vector.get(0).cnt;
				String s = "";
				for (Level level : vector) {
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
		for (ArrayList<Level> vector : this.tundishResults) {
			int total = vector.get(0).cnt;
			String s = "";
			for (Level level : vector) {
				Element levelEmt = doc.createElement("Level");
				float per = 0;
				System.out.println(level.name + " " + level.cnt);
				if (total != 0)
					per = (float) level.cnt / total;
				else
					per = 0;
				levelEmt.setAttribute("name", level.name);
				levelEmt.setAttribute("num", String.valueOf(level.cnt));
				levelEmt.setAttribute("proportion", String.valueOf(per));
				HashMap<String, Integer> hashMap = level.runOffMap;
				// 排序
				ByValueComparator bvc = new ByValueComparator(hashMap);
				List<String> keys = new ArrayList<String>(hashMap.keySet());
				Collections.sort(keys, bvc);
				for (String pageName : keys) {
					Element runOffEmt = doc.createElement("RunOffPage");
					int levelTotal = level.cnt;
					runOffEmt.setAttribute("name", pageName);
					runOffEmt.setAttribute("num", hashMap.get(pageName)
							.toString());
					if (levelTotal != 0)
						per = (float) hashMap.get(pageName) / levelTotal;
					else
						per = 0;
					runOffEmt.setAttribute("proportion", String.valueOf(per));
					if ( total != 0)
						per = (float) hashMap.get(pageName) / total;
					else
						per = 0;
					//runOffEmt.setAttribute("proportion2", String.valueOf(per));
					levelEmt.appendChild(runOffEmt);
				}
				root.appendChild(levelEmt);
			}
			System.out.println(s);
		}
		try {
			// 用xmlserializer把document的内容进行串化
			FileOutputStream os = null;
			OutputFormat outformat = new OutputFormat(doc);
			os = new FileOutputStream(this.localOutputPath + "/漏斗.xml");
			XMLSerializer xmlSerilizer = new XMLSerializer(os, outformat);
			xmlSerilizer.serialize(doc);

		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}

	}

	public void saveTundishListContinuous() {
		try {
			System.out.println("Continuous");
			FileWriter fw = new FileWriter(this.localOutputPath
					+ "/连续漏斗.txt");
			BufferedWriter writer = new BufferedWriter(fw);
			for (ArrayList<Level> vector : this.tundishResultsContinuous) {
				int total = vector.get(0).cnt;
				String s = "";
				for (Level level : vector) {
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
		for (ArrayList<Level> vector : this.tundishResultsContinuous) {
			int total = vector.get(0).cnt;
			String s = "";
			for (Level level : vector) {
				Element levelEmt = doc.createElement("Level");
				float per = 0;
				System.out.println(level.name + " " + level.cnt);
				if (total != 0)
					per = (float) level.cnt / total;
				else
					per = 0;
				levelEmt.setAttribute("name", level.name);
				levelEmt.setAttribute("num", String.valueOf(level.cnt));
				levelEmt.setAttribute("proportion", String.valueOf(per));
				HashMap<String, Integer> hashMap = level.runOffMap;
				// 排序
				ByValueComparator bvc = new ByValueComparator(hashMap);
				List<String> keys = new ArrayList<String>(hashMap.keySet());
				Collections.sort(keys, bvc);
				for (String pageName : keys) {
					Element runOffEmt = doc.createElement("RunOffPage");
					int levelTotal = level.cnt;
					runOffEmt.setAttribute("name", pageName);
					runOffEmt.setAttribute("num", hashMap.get(pageName)
							.toString());
					if (levelTotal != 0)
						per = (float) hashMap.get(pageName) / levelTotal;
					else
						per = 0;
					runOffEmt.setAttribute("proportion", String.valueOf(per));
					if ( total != 0)
						per = (float) hashMap.get(pageName) / total;
					else
						per = 0;
					//runOffEmt.setAttribute("proportion2", String.valueOf(per));
					levelEmt.appendChild(runOffEmt);
				}
				root.appendChild(levelEmt);
			}
			System.out.println(s);
		}
		try {
			// 用xmlserializer把document的内容进行串化
			FileOutputStream os = null;
			OutputFormat outformat = new OutputFormat(doc);
			os = new FileOutputStream(this.localOutputPath
					+ "/连续漏斗.xml");
			XMLSerializer xmlSerilizer = new XMLSerializer(os, outformat);
			xmlSerilizer.serialize(doc);

		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}

	}

	// 比较HashMap value的功能函数 http://www.oschina.net/code/snippet_12_546?from=rss
	static class ByValueComparator implements Comparator<String> {
		HashMap<String, Integer> base_map;

		public ByValueComparator(HashMap<String, Integer> base_map) {
			this.base_map = base_map;
		}

		public int compare(String arg0, String arg1) {
			if (!base_map.containsKey(arg0) || !base_map.containsKey(arg1)) {
				return 0;
			}
			if (base_map.get(arg0) < base_map.get(arg1)) {
				return 1;
			} else if (base_map.get(arg0) == base_map.get(arg1)) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	@Override
	public void onInitial() {
		// TODO Auto-generated method stub
		Byte type = this.getType();
		System.out.println("type " + type);
		Ulits.newFolder(this.getOutputPath() + "/漏斗分析");
		if (type.equals(AnalyseType.Original)) {
			this.localOutputPath = this.getOutputPath() + "/漏斗分析/网页名+目录";
		} else if (type.equals(AnalyseType.NegCate)) {
			this.localOutputPath = this.getOutputPath() + "/漏斗分析/仅网页名";
		} else if (type.equals(AnalyseType.PageToCate)) {
			this.localOutputPath = this.getOutputPath() + "/漏斗分析/板块分析（一级目录）";
		}
		Ulits.newFolder(this.localOutputPath);
		System.out.println("hi" + this.localOutputPath);

	}

	@Override
	public void onReadEnd() throws IOException {
		// TODO Auto-generated method stub
		if (this.isDiscrete) {
			this.saveTundishList();
			this.saveTundishListXML();
		}
		if (this.isContinuous) {
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
				if (this.isDiscrete)
					onReadHis(this.curPVHis);
				if (this.isContinuous)
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
		// 遍历每个Session
		for (String path : his.getPathString()) {
			String[] visits = path.split(",");
			// 初始化每个漏斗的层数标记，最顶层为0
			int[] indexs = new int[this.tundishResults.size()];
			String[] runOffPage = new String[this.tundishResults.size()];

			String preVisit = "";
			// 遍历Session中的路径
			for (int i = 0; i < visits.length; i++) {
				String visit = Preprocessor.getPageName(visits[i]);
				//System.out.print(visit + ",");
				// 遍历每个漏斗 seq 是漏斗的index
				for (int seq = 0; seq < this.tundishResults.size(); seq++) {
					// 目标漏斗
					ArrayList<Level> tundishResult = (ArrayList<Level>) this.tundishResults
							.get(seq);
					Level level0 = (Level) tundishResult.get(0);

					// 目标层 indexs[seq] 是第seq个漏斗当前的目标层，即它的第indexs[seq]层
					Level targetLevel = (Level) tundishResult.get(indexs[seq]);

					// 当前页面指向漏斗第0层时
					if (visit.equals(level0.name)) {
						// 如果目标层不是第0层，计算前一层的用户流失，且只有目标层不是第0层的时候计算
						if (targetLevel != level0) {
						} else {
							if (!preVisit.equals(level0.name)) {
								level0.cnt++;
							}
						}
						// System.out.println(indexs[seq]);
						// level0.cnt++;
						indexs[seq] = 1;
						// 一次漏斗结束后，目标层序号重置为第0层, 如果漏斗只有一层的特殊情况
						if (indexs[seq] == tundishResult.size())
							indexs[seq] = 0;
					}
					// 如果当前页面不指向漏斗第0层
					else {
						// 如果当前页面指向目标层
						if (visit.equals(targetLevel.name)) {
							if ( !preVisit.equals(level0.name) ){
								//System.out.println("\n!!!!!!!!!!!"+preVisit+"!!!!!!!!!!!!!!!");
							}
							targetLevel.cnt++;
							indexs[seq]++;
							// 如果一次漏斗结束后,比较层数重置到第0层
							if (indexs[seq] == tundishResult.size())
								indexs[seq] = 0;
						}
						// 如果当前页面不指向目标层，计算前一层的用户流失
						else {
							// //////////////////
							Level preLevel = level0;

							// 如果目标层不是第0层，且当前页不是第0层
							if (targetLevel != level0) {
								preLevel = (Level) tundishResult.get(indexs[seq] - 1);
								if ( preLevel.name.equals(preVisit) ){
									runOffPage[seq] = visit;
								
								}
								if (i == visits.length - 1) {

									preLevel = (Level) tundishResult
											.get(indexs[seq] - 1);
									// 如果当前页不是前一层的重复的话
									if (!visit.equals(preLevel)) {
										HashMap<String, Integer> hashMap = preLevel.runOffMap;
										if (hashMap.get(runOffPage[seq]) == null) {
											hashMap.put(runOffPage[seq], 1);
										} else {
											int cnt = hashMap.get(runOffPage[seq]);
											hashMap.put(runOffPage[seq], cnt + 1);
										}
										indexs[seq] = 0;
									}

									// 如果当前页是前一层的重复
									else {
										// 什么也不做
									}
								}
							}
							// 如果目标层是第0层，且当前页不是第0层
							else {
								// 什么也不做
							}
						}
					}
				}
				preVisit = visit;
			}
			//System.out.print("\n");
		}
	}

	private void onReadHisContinuous(PVHistory his) {
		// 遍历每个Session
		for (String path : his.getPathString()) {
			// String path = (String) iter.next();
			String[] visits = path.split(",");
			// 初始化每个漏斗的层数标记，最顶层为0
			int[] indexs = new int[tundishResultsContinuous.size()];
			String preVisit = "";

			// 遍历Session中的路径
			for (int i = 0; i < visits.length; i++) {
				// System.out.println(visits[i]);
				String visit = Preprocessor.getPageName(visits[i]);
				//System.out.print(visit + ",");
				// 遍历每个漏斗 seq 是漏斗的index
				for (int seq = 0; seq < tundishResultsContinuous.size(); seq++) {
					// 目标漏斗
					ArrayList<Level> tundishResult = (ArrayList<Level>) tundishResultsContinuous
							.get(seq);
					Level level0 = (Level) tundishResult.get(0);

					// 目标层 indexs[seq] 是第seq个漏斗当前的目标层，即它的第indexs[seq]层
					Level targetLevel = (Level) tundishResult.get(indexs[seq]);

					// 当前页面指向漏斗第0层时
					if (visit.equals(level0.name)) {
						// 如果目标层不是第0层，计算前一层的用户流失，且只有目标层不是第0层的时候计算
						if (targetLevel != level0) {
							Level preLevel = (Level) tundishResult
									.get(indexs[seq] - 1);
							HashMap<String, Integer> hashMap = preLevel.runOffMap;
							if (hashMap.get(visit) == null) {
								hashMap.put(visit, 1);
							} else {
								int cnt = hashMap.get(visit);
								hashMap.put(visit, cnt + 1);
							}
						} else {
							if (!preVisit.equals(level0.name)) {
								level0.cnt++;
							}
						}
						// System.out.println(indexs[seq]);
						// level0.cnt++;
						indexs[seq] = 1;
						// 一次漏斗结束后，目标层序号重置为第0层, 如果漏斗只有一层的特殊情况
						if (indexs[seq] == tundishResult.size())
							indexs[seq] = 0;
					}
					// 如果当前页面不指向漏斗第0层
					else {
						// 如果当前页面指向目标层
						if (visit.equals(targetLevel.name)) {
							targetLevel.cnt++;
							indexs[seq]++;
							// 如果一次漏斗结束后,比较层数重置到第0层
							if (indexs[seq] == tundishResult.size())
								indexs[seq] = 0;
						}
						// 如果当前页面不指向目标层，计算前一层的用户流失
						else {
							// //////////////////
							Level preLevel = level0;
							// 如果目标层不是第0层，且当前页不是第0层
							if (targetLevel != level0) {
								preLevel = (Level) tundishResult
										.get(indexs[seq] - 1);
								// 如果当前页不是前一层的重复的话
								if (!visit.equals(preLevel)) {
									HashMap<String, Integer> hashMap = preLevel.runOffMap;
									if (hashMap.get(visit) == null) {
										hashMap.put(visit, 1);
									} else {
										int cnt = hashMap.get(visit);
										hashMap.put(visit, cnt + 1);
									}
									indexs[seq] = 0;
								}
								// 如果当前页是前一层的重复
								else {
									// 什么也不做
								}

							}
							// 如果目标层是第0层，且当前页不是第0层
							else {
								// 什么也不做
							}

						}
					}
				}
				preVisit = visit;

			}
			//System.out.print("\n");
		}

	}

	public static void main(String[] args) {
		// AnalyseRunner b = new AnalyseRunner();
		// b.setNegCate(true);
		// // ReplaceFilter r=new ReplaceFilter();
		// try {
		// b.setInputPath("E:/data/pagevisit/pv6.txt");
		// b.setOutputPath("E:/data/pagevisit/pv6.txt.out");
		//			
		// b.setSiteDataPath("E:/data");
		//
		// Tundish td = new Tundish();
		// td.readPathList("E:/data/pagevisit/path.txt");
		// b.addAnalyse(td);
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
		
		try {
			// s.start("TundishClass 1? |?1? |?1? |?login*; |;手机阅读阅读页; |;我的书架");
			s.start("TundishClass" + Separator.cmdSeparator
					+ AnalyseType.NegCate + Separator.PARAM_SEPARATOR1 + 1
					+ Separator.PARAM_SEPARATOR1 + 1
					+ Separator.PARAM_SEPARATOR1 + "login*"
					+ Separator.PARAM_SEPARATOR3 + "手机阅读阅读页"
					+ Separator.PARAM_SEPARATOR3 + "我的书架");
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
		for (int i = 0; i < pathList.length; i++) {
			this.pathList.add(pathList[i]);
		}
		initResult(this.pathList);
	}

	private void initResult(List<String> pathList) {
		this.tundishResults.clear();
		this.tundishResultsContinuous.clear();
		for (int i = 0; i < this.pathList.size(); i++) {
			String[] strArray = this.pathList.get(i).split(
					Separator.PARAM_SEPARATOR3);
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
