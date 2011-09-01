package org.tseg.model;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tseg.Ulits.Separator;
import org.tseg.algorithm.community.clique.MaximalCliques;
import org.tseg.algorithm.community.clique.YeQiMaximalCliques;
import org.tseg.algorithm.community.clique.cpm.CPM;
import org.tseg.graph.UndigraphImpl;
import org.tseg.preprocess.Preprocessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class MarkovModel {

	private HashMap linkMap = new HashMap();
	private HashMap pageMap = new HashMap();
	private int clickAmout = 0;

	public void saveRelatedPage(String fileName) {
		
		try {
			
			FileWriter fw = new FileWriter(fileName);
			BufferedWriter writer = new BufferedWriter(fw);

			UndigraphImpl rawGraph = new UndigraphImpl();
			Iterator iter = this.linkMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String edge = (String) entry.getKey();
				String[] page = edge.split(Separator.edgeSeparator);
				String reverseEdge = page[1] + Separator.edgeSeparator
						+ page[0];
				Double val2 = (Double) linkMap.get(reverseEdge);
				Double val = (Double) entry.getValue();
				if (val2 != null) {
					double min = Math.min(val, val2);
					double ratio = 0.5 - (min / (val + val2));
					if (ratio < 0.2&&min>10.0) {
						page[0]=Preprocessor.getPageName(page[0]);
						page[1]=Preprocessor.getPageName(page[1]);
						rawGraph.addEdge(page[0], page[1]);
					}
				}
			}
			
			MaximalCliques cliques = new YeQiMaximalCliques(rawGraph);
			cliques.setMinimalCliqueSize(3);
			List<HashSet> clusterList =(cliques.findCommunityNodeSetList());
			System.out.println("Got all the cliques, there are " + clusterList.size() + "cliques");
			
			
			
			for(HashSet cluster:clusterList){
				writer.append(cluster.toString()+"\n");
				System.out.println(cluster.toString()+"\n");
			}
			System.out.println("clickAmout:"+this.clickAmout);
			writer.append("page community.......................................\n");
			CPM cpm = new CPM(3, clusterList);
			cpm.computeKCliqueCommunities();
			cpm.printKCliqueCommunity(writer);
			writer.flush();
			writer.close();
		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}

	}
	
	public void saveRelatedPageXML(String fileName){
		
		try {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = null;
			try {
				db = dbf.newDocumentBuilder();
			} catch (Exception pce) {
				System.err.println(pce);
			}
			Document doc = db.newDocument();
			// 在doc中创建"学生花名册"tag作为根节点
			Element root = doc.createElement("RelatedPage");
			doc.appendChild(root);

			UndigraphImpl rawGraph = new UndigraphImpl();
			Iterator iter = this.linkMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String edge = (String) entry.getKey();
				String[] page = edge.split(Separator.edgeSeparator);
				String reverseEdge = page[1] + Separator.edgeSeparator
						+ page[0];
				Double val2 = (Double) linkMap.get(reverseEdge);
				Double val = (Double) entry.getValue();
				if (val2 != null) {
					double min = Math.min(val, val2);
					double ratio = 0.5 - (min / (val + val2));
					if (ratio < 0.2&&min>10.0) {
						page[0]=Preprocessor.getPageName(page[0]);
						page[1]=Preprocessor.getPageName(page[1]);
						rawGraph.addEdge(page[0], page[1]);
					}
				}
			}
			
			MaximalCliques cliques = new YeQiMaximalCliques(rawGraph);
			cliques.setMinimalCliqueSize(3);
			List<HashSet> clusterList =(cliques.findCommunityNodeSetList());
			System.out.println("Got all the cliques, there are " + clusterList.size() + "cliques");
			
			
			
			for(HashSet cluster:clusterList){
				Element c = doc.createElement("cluster");
				c.setAttribute("menber", cluster.toString());
				root.appendChild(c);
				System.out.println(cluster.toString()+"\n");
			}
			
			
			// 用xmlserializer把document的内容进行串化
			FileOutputStream os = null;
			OutputFormat outformat = new OutputFormat(doc);
			os = new FileOutputStream(fileName);
			XMLSerializer xmlSerilizer = new XMLSerializer(os, outformat);
			xmlSerilizer.serialize(doc);
			
		
		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}
		
	}

	public MarkovModel mergeWith(MarkovModel model) {

		Iterator iter = model.linkMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Double val = (Double) entry.getValue();
			Double edgeNum = (Double) this.linkMap.get(key);
			if (edgeNum == null) {
				this.linkMap.put(key, val);
			} else {
				this.linkMap.put(key, val + edgeNum);
			}

		}
		Iterator iter1 = model.pageMap.entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry entry = (Map.Entry) iter1.next();
			String key = (String) entry.getKey();
			Page val = (Page) entry.getValue();
			Page p = (Page) this.pageMap.get(key);
			if (p == null) {
				this.pageMap.put(key, val);
			} else {
				p.setClickNum(val.getClickNum() + p.getClickNum());
				p.setDuration(val.getDuration() + p.getDuration());
				this.pageMap.put(key, p);
			}

		}

		return this;

	}

	public void saveModel(String fileName, int linkThreshold, boolean isFilter) {

		String seperator = Separator.edgeSeparator;
		try {
			FileWriter fw = new FileWriter(fileName);
			BufferedWriter writer = new BufferedWriter(fw);

			Iterator iter = linkMap.entrySet().iterator();

			while (iter.hasNext()) {

				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				Double val = (Double) entry.getValue();
				if (val > linkThreshold) {
					String pageName1 = key.split(seperator)[0];
					String pageName2 = key.split(seperator)[1];
					Page p1 = (Page) pageMap.get(pageName1);
					Page p2 = (Page) pageMap.get(pageName2);
					if (p1 != null) {
						pageName1 = p1.toString();
					}
					if (p2 != null) {
						pageName2 = p2.toString();
					}
					writer.write(pageName1 + seperator + pageName2 + seperator
							+ val + "\n");
				}

			}
			writer.flush();
			writer.close();
			fw.close();

		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}
	}

	public void saveModelMXL(String fileName, int linkThreshold,
			boolean isFilter) {

		
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (Exception pce) {
			System.err.println(pce);
		}
		Document doc = db.newDocument();
		// 在doc中创建"学生花名册"tag作为根节点
		Element root = doc.createElement("Graph");
		doc.appendChild(root);

		String seperator = Separator.edgeSeparator;
		HashSet pageSet = new HashSet();
		try {
			FileWriter fw = new FileWriter(fileName);
			BufferedWriter writer = new BufferedWriter(fw);

			Iterator iter = linkMap.entrySet().iterator();

			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				Double val = (Double) entry.getValue();
				if (val > linkThreshold) {
					String pageName1 =Preprocessor.getPageName( key.split(seperator)[0]);
					String pageName2 = Preprocessor.getPageName(key.split(seperator)[1]);			
					pageSet.add(key.split(seperator)[0]);
					pageSet.add(key.split(seperator)[1]);
					Element edgeNode = doc.createElement("Edge");
					edgeNode.setAttribute("fromID", pageName1);
					edgeNode.setAttribute("toID", pageName2);
					edgeNode.setAttribute("weight", val.toString());
					root.appendChild(edgeNode);

				}

			}
			Iterator iter1 = pageSet.iterator();
			while (iter1.hasNext()) {
				String pageIndex = (String) iter1.next();
				Element pageNode = doc.createElement("Node");
				pageNode.setAttribute("id", Preprocessor.getPageName(pageIndex));
				Page p = (Page) this.pageMap.get(pageIndex);
				if (p != null) {
					pageNode.setAttribute("clickNum", String.valueOf(p
							.getClickNum()));
					pageNode.setAttribute("time", String.valueOf(p
							.getDuration()));
					pageNode.setAttribute("UVNum", String.valueOf(p.getUserNum()));
					root.appendChild(pageNode);
				} else {
					pageNode.setAttribute("clickNum", String.valueOf(-1));
					pageNode.setAttribute("time", String.valueOf(0));
					pageNode.setAttribute("UVNum", String.valueOf(0));
					root.appendChild(pageNode);
				}

			}

			// 用xmlserializer把document的内容进行串化
			FileOutputStream os = null;
			OutputFormat outformat = new OutputFormat(doc);
			os = new FileOutputStream(fileName);
			XMLSerializer xmlSerilizer = new XMLSerializer(os, outformat);
			xmlSerilizer.serialize(doc);

		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}
	}

	public MarkovModel() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void setEdgeMap(HashMap edgeMap) {
		this.linkMap = edgeMap;
	}

	public HashMap getEdgeMap() {
		return linkMap;
	}

	public void setVertexMap(HashMap vertexMap) {
		this.pageMap = vertexMap;
	}

	public HashMap getPageMap() {
		return pageMap;
	}

	public void setClickAmout(int clickAmout) {
		this.clickAmout = clickAmout;
	}

	public int getClickAmout() {
		return clickAmout;
	}
	public void increClickAmount(){
		this.clickAmout++;
	}
}
