package org.tseg.analyse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tseg.graph.community.VertexCommunity;
import org.tseg.graph.vertex.Vertex;
import org.tseg.model.MarkovModel;
import org.tseg.model.Page;
import org.tseg.model.Visit;

public class MarkovModelBuilder {
	
	
	private MarkovModel aggregatemodel = new MarkovModel();
	private HashMap personModelMap = new HashMap();
	
	
	void buildGlobalModel(String []strArray){
		Visit v = new Visit(strArray);
		updateModel(this.aggregatemodel,v);
	}
	
	void updateModel(MarkovModel model, Visit v) {

		String keyStr = v.getCurPage() + "	" + v.getNextPage();

		model.setClickAmout(model.getClickAmout() + 1);

		if (model.getEdgeMap().get(keyStr) == null) {
			model.getEdgeMap().put(keyStr, 1.0);
		} else {
			Double d = (Double) model.getEdgeMap().get(keyStr);
			d += 1.0;
			model.getEdgeMap().put(keyStr, d);
		}
		if (model.getPageMap().get(v.getCurPage()) == null) {
			Page p = new Page();
			p.setClickNum(1);
			p.setDuration(v.getDruation());
			p.setPageName(v.getCurPage());
			model.getPageMap().put(v.getCurPage(), p);
		} else {
			Page p = (Page) model.getPageMap().get(v.getCurPage());
			p.setClickNum(p.getClickNum() + 1);
			int duration = v.getDruation();
			p.setDuration(p.getDuration() + duration);
			model.getPageMap().put(v.getCurPage(), p);
		}

		if (v.getPrePage().contains("login")) {
			String edge1 = v.getPrePage() + "\t" + v.getCurPage();

			if (model.getEdgeMap().get(edge1) == null) {
				model.getEdgeMap().put(edge1, 1.0);
			} else {
				Double d = (Double) model.getEdgeMap().get(edge1);
				d += 1.0;
				model.getEdgeMap().put(edge1, d);
			}
			if (model.getPageMap().get(v.getPrePage()) == null) {
				Page p = new Page();
				p.setClickNum(1);
				p.setPageName(v.getPrePage());
				model.getPageMap().put(v.getPrePage(), p);
			} else {
				Page p = (Page) model.getPageMap().get(v.getPrePage());
				p.setClickNum(p.getClickNum() + 1);
				model.getPageMap().put(v.getPrePage(), p);
			}

		}

	}

	void buildPersonModel(String[] strArray) {

		// String []strArray=line.split(",");
		MarkovModel m = (MarkovModel) personModelMap.get(strArray[0]);
		Visit v = new Visit(strArray);
		if (m == null) {
			if (this.personModelMap.size() < 10000) {
				m = new MarkovModel();
				updateModel(m, v);
				personModelMap.put(strArray[0], m);

			}

		} else {
			updateModel(m, v);
			personModelMap.put(strArray[0], m);
		}

	}

	void savePersonModels(String filePath) {

		Iterator iter = personModelMap.entrySet().iterator();

		while (iter.hasNext()) {

			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			MarkovModel val = (MarkovModel) entry.getValue();
			val.saveModel(filePath + "/" + key + ".txt", 0, false);

		}
	}

	public void buildGroupModel(List clusterList, String fileName) {

		for (int i = 0; i < clusterList.size(); i++) {

			VertexCommunity community = (VertexCommunity) clusterList.get(i);
			Set communityNodeSet = community.getVertexSet();
			Iterator iter = communityNodeSet.iterator();
			MarkovModel groupModel = new MarkovModel();
			int size = 0;
			while (iter.hasNext()) {
				Vertex v = (Vertex) iter.next();
				MarkovModel m = (MarkovModel) this.personModelMap
						.get((String) v.getID());
				groupModel.mergeWith(m);
				size++;

			}
			groupModel.saveModel(fileName + "/" + i + "-" + size + ".txt", 0,
					false);
			if (size > 100) {
				groupModel.saveModelMXL(fileName + "/" + i + "-" + size
						+ ".xml", 0, false);
			}

		}

	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public MarkovModel getGlobalModel() {
		return aggregatemodel;
	}

	public void setAggregatemodel(MarkovModel aggregatemodel) {
		this.aggregatemodel = aggregatemodel;
	}

	public HashMap getPersonModelMap() {
		return personModelMap;
	}

	public void setPersonModelMap(HashMap personModelMap) {
		this.personModelMap = personModelMap;
	}

}
