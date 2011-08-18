package org.tseg.algorithm.cluster;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.tseg.algorithm.community.cnm.CNM;
import org.tseg.algorithm.community.metric.StrongCommunityMetric;
import org.tseg.graph.Graph;
import org.tseg.graph.community.VertexCommunity;
import org.tseg.graph.vertex.Vertex;
import org.tseg.io.NaiveFileGraphReader;
import org.tseg.model.MarkovModel;
import org.tseg.model.Page;
import org.tseg.utils.GlobalEnvValue;

public class Cluster {

	private HashMap dataMap = null;
	private List edgeList = new ArrayList();
	private int dataSize = 0;
	private double minSimilarity = 0.3;
	private List communityList=new ArrayList();

	private double getSimilarity(MarkovModel model1, MarkovModel model2) {

		double dis1 = 0.0;
		int intersectionNum = 0;
		HashSet set = new HashSet();
		Iterator iter = model1.getPageMap().values().iterator();
		while (iter.hasNext()) {
			Page p = ((Page) iter.next());
			if (!p.getPageName().contains("log")) {
				dis1 += (p.getClickNum() * 1.0 / model1.getClickAmout())
						* (p.getClickNum() * 1.0 / model1.getClickAmout());
				set.add(p.getPageName());
			}

		}
		dis1 = Math.sqrt(dis1);
		double dis2 = 0.0;
		double dot = 0.0;
		Iterator iter2 = model2.getPageMap().values().iterator();
		while (iter2.hasNext()) {

			Page p2 = (Page) iter2.next();
			if (!p2.getPageName().contains("log")) {
				dis2 += (p2.getClickNum() * 1.0 / model2.getClickAmout())
						* (p2.getClickNum() * 1.0 / model2.getClickAmout());
				Page p1 = (Page) model1.getPageMap().get(p2.getPageName());
				set.add(p2.getPageName());
				if (p1 != null) {
					dot += (p1.getClickNum() * 1.0 / model1.getClickAmout())
							* (p2.getClickNum() * 1.0 / model2.getClickAmout());
					intersectionNum++;
				}
			}

		}
		double ratio = intersectionNum * 1.0 / set.size();
		dis2 = Math.sqrt(dis2);
		if ((dis1 * dis2) == 0) {
			return 0.0;
		}
		double cosin = dot / (dis1 * dis2);
		return cosin * ratio;

	}

	public void getSimGraph(String fileName) throws IOException {

		dataSize = dataMap.size();
		FileWriter fw = new FileWriter(fileName);
		BufferedWriter writer = new BufferedWriter(fw);

		MarkovModel[] modelArray = new MarkovModel[dataSize];
		String[] idArray = new String[dataSize];
		int index = 0;
		Iterator iter = dataMap.entrySet().iterator();

		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			modelArray[index] = ((MarkovModel) entry.getValue());
			idArray[index] = (String) entry.getKey();
			index++;
		}
		for (int i = 0; i < dataSize - 1; i++) {
			for (int j = i + 1; j < dataSize; j++) {
				double s = getSimilarity(modelArray[i], modelArray[j]);
				if (s > 0.5) {
					writer.write(idArray[i] + "\t" + idArray[j] + "\t" + s
							+ "\n");
					// edgeList.add(new String(idArray[i]+","+idArray[j]));
					if (modelArray[i].getPageMap().size() > 10) {
						System.out.println(idArray[i]);
					}
				}

			}
		}
		writer.flush();
		writer.close();
	}

	public void runFastGN(String inFile, String outFile) throws IOException {
		
		FileWriter fw = new FileWriter(outFile);
		BufferedWriter writer = new BufferedWriter(fw);

		Graph rawGraph = NaiveFileGraphReader.readUndirectedGraph(inFile,
				GlobalEnvValue.STRING_VERTEX);
		CNM gn = new CNM(rawGraph);
		communityList = gn.findCommunities();
		System.out.println("done");
		int strongCommunity=0;
		for (int i = 0; i < communityList.size(); i++)
		{
			
			VertexCommunity community = (VertexCommunity)communityList.get(i);
			Set communityNodeSet = community.getVertexSet();
			String comStr="";
			System.out.println(i);
			System.out.println(communityNodeSet.size());
			Iterator iter=communityNodeSet.iterator();
			while(iter.hasNext()){
				//System.out.println("zzzzzzzzzzzzzzzzzzz");
				Vertex v=(Vertex)iter.next();
				comStr+=v.getID()+"\t";
				
			}
			comStr+="\n";
			writer.write(comStr);
			if (StrongCommunityMetric.isStrongCommunity(rawGraph, community)){
				strongCommunity++;
			}
			
			
		}
		writer.flush();
		writer.close();

	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		
		//String outFloder=inputFile+".out";
		
		//Ulits.newFolder(outFloder);
		FileWriter fw = new FileWriter("D://test.txt");
		BufferedWriter writer = new BufferedWriter(fw);

	}

	public void setDataIter(HashMap dataIter) {
		this.dataMap = dataIter;
	}

	public HashMap getDataIter() {
		return dataMap;
	}

	public void setEdgeList(List edgeList) {
		this.edgeList = edgeList;
	}

	public List getEdgeList() {
		return edgeList;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}

	public int getDataSize() {
		return dataSize;
	}

	public void setMinSimilarity(double minSimilarity) {
		this.minSimilarity = minSimilarity;
	}

	public double getMinSimilarity() {
		return minSimilarity;
	}

	public void setCommunityList(List communityList) {
		this.communityList = communityList;
	}

	public List getCommunityList() {
		return communityList;
	}

}
