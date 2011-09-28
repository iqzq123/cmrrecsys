package org.tseg.analyse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tseg.Ulits.AnalyseType;
import org.tseg.Ulits.Separator;
import org.tseg.Ulits.Ulits;
import org.tseg.model.MarkovModel;
import org.tseg.model.Page;
import org.tseg.model.Visit;
import org.tseg.preprocess.Preprocessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class GlobalAnalyse extends Analyse {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "GlobalAnalyse";
	}

	@Override
	public void readParam(String params) {
		// TODO Auto-generated method stub
		String[] paramArray = params.split(Separator.PARAM_SEPARATOR1);
		this.setType(Byte.parseByte(paramArray[0]));
		this.minLinkNum=Integer.parseInt(paramArray[1]);
		
	}

	private MarkovModel globalModel = new MarkovModel();
	private MarkovModel curPerModel = null;	
	private String curPerID = "";
	private String outFloder="";
	private int minLinkNum=100;
	private int maxPerModelNum=100;
	private int curPerModelNum=0;
	@Override
	public void onInitial() {
		// TODO Auto-generated method stub
		this.outFloder = this.getOutputPath() + "/全局分析";
		Ulits.newFolder(this.outFloder);

	}

	@Override
	public void onReadEnd() throws IOException {
		// TODO Auto-generated method stub
		this.globalModel.saveModelMXL(this.outFloder+"/全局跳转图"+this.getType()+".xml", minLinkNum,
				false);
		String relPath=this.outFloder + "/相关页面"+this.getType();
		if(this.getType()==AnalyseType.PageToCate){
			relPath=this.outFloder + "/相关板块";
		}
		this.globalModel.saveRelatedPage(relPath+".txt",minLinkNum);
		this.globalModel.saveRelatedPageXML(relPath+".xml",minLinkNum);
		this.saveSortedPage(this.outFloder + "/页面uv"+this.getType()+".xml", "userNum");
		this.saveSortedPage(this.outFloder + "/页面pv"+this.getType()+".xml", "clickNum");
		this.saveSortedPage(this.outFloder + "/页面时长"+this.getType()+".xml", "duration");
		this.saveSortedPage(this.outFloder+"/页面人均pv"+this.getType()+".xml", "AverPerPv");

	}

	@Override
	public void onReadLog(String[] log) throws IOException {
		// TODO Auto-generated method stub
		buildGlobalModel(log);
		buildPersonModel(log);
	
	}

	void buildGlobalModel(String[] strArray) {
		Visit v = new Visit(strArray);
		updateModel(this.globalModel, v);
		
	}

	void buildPersonModel(String[] strArray) {
		
		if (!this.curPerID.equals(strArray[0])) {
			if (this.curPerModel != null) {
				//////////////计算UV			
				updateUVStat(this.curPerModel);
				//////////只保存一定数量的个人model
//				if(this.curPerModelNum<this.maxPerModelNum){
//					this.curPerModel.saveModelMXL(this.subFloder + "/"
//						+ this.curPerID + ".xml", 0, false);
//				}
				
				this.curPerModelNum++;
				
			}
			this.curPerID = strArray[0];
			this.curPerModel = new MarkovModel();
			
		}

		Visit v = new Visit(strArray);
		updateModel(this.curPerModel, v);

	}
	
	public void updateUVStat(MarkovModel perModel){
			
		Iterator iter1=perModel.getPageMap().entrySet().iterator();
		while(iter1.hasNext()){
			Map.Entry<String, Page> entry=(Map.Entry<String, Page>)iter1.next();
			String pageID=entry.getKey();
			Page p=(Page)this.globalModel.getPageMap().get(pageID);
			p.setUserNum(p.getUserNum()+1);
			this.globalModel.getPageMap().put(pageID, p);
		}
		
	}
	public void saveSortedPage(String fileName,String property){
		
		
		////////////////sort 
		Object []pageIDArray=this.globalModel.getPageMap().keySet().toArray();
		for(int i=0;i<pageIDArray.length;i++){
			for(int j=i+1;j<pageIDArray.length;j++){
				Page p1=(Page)this.globalModel.getPageMap().get(pageIDArray[i]);
				Page p2=(Page)this.globalModel.getPageMap().get(pageIDArray[j]);
				if(p2.isMoreThan(p1, property)){
					Object tmp=pageIDArray[i];
					pageIDArray[i]=pageIDArray[j];
					pageIDArray[j]=tmp;
				}
			}
		}
		/////////////save
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (Exception pce) {
			System.err.println(pce);
		}
		Document doc = db.newDocument();

		Element root = doc.createElement(property);
		doc.appendChild(root);
		for (Object pageID : pageIDArray) {
			Element page = doc.createElement("page");
			String pageName = Preprocessor.getPageName((String) pageID);
			page.setAttribute("pageName", pageName);
			Page p= (Page)this.globalModel.getPageMap().get(pageID);
			//page.setAttribute(property,String.valueOf(p.getValue(property)));
			page.setAttribute("userNum",String.valueOf(p.getUserNum()));
			page.setAttribute("clickNum",String.valueOf(p.getClickNum()));
			page.setAttribute("duration",String.valueOf(p.getDuration()));
			page.setAttribute("人均pv数", String.valueOf(p.getAverPerPv()));
			root.appendChild(page);
		}
	
		Element pageNum=doc.createElement("pageNum");
		pageNum.setAttribute("number", String.valueOf(this.globalModel.getPageMap().size()));
		root.appendChild(pageNum);
		try {
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

	public void buildGroupModel(List clusterList, String fileName) {

		
	}

	void updateModel(MarkovModel model, Visit v) {
		
		
		if(model==null){
			return;
		}
		String edgeSeperator = Separator.edgeSeparator;
		String keyStr = v.getCurPage() + edgeSeperator + v.getNextPage();

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
			String edge1 = v.getPrePage() + edgeSeperator + v.getCurPage();

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

	void savePersonModels(String filePath) {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public MarkovModel getGlobalModel() {
		return globalModel;
	}

	public void setAggregatemodel(MarkovModel aggregatemodel) {
		this.globalModel = aggregatemodel;
	}

	public int getMaxPerModelNum() {
		return maxPerModelNum;
	}

	public void setMaxPerModelNum(int maxPerModelNum) {
		this.maxPerModelNum = maxPerModelNum;
	}

	public int getMinLinkNum() {
		return minLinkNum;
	}

	public void setMinLinkNum(int minLinkNum) {
		this.minLinkNum = minLinkNum;
	}

}
