package org.tseg.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class Histogram {

	private String name;
	private String xName;
	private String yName;
	private int max = Integer.MIN_VALUE;
	private double aver;
	private int min = Integer.MAX_VALUE;
	private int distance;
	private int columnNum = 10;
	private List<Integer> colValueList;
	private List<Integer> dataList = new ArrayList<Integer>();

	public void build() {

		colValueList = new ArrayList(this.columnNum + 1);
		for (int i = 0; i < this.columnNum + 1; i++) {
			this.colValueList.add(0);
		}

		BigInteger amount = new BigInteger("0");
		for (int a : dataList) {
			BigInteger b = new BigInteger(String.valueOf(a));
			amount = amount.add(b);
			if (max < a) {
				max = a;
			}
			if (min > a) {
				min = a;
			}
		}
		String s = amount.toString();
		aver = amount.doubleValue();
		aver = aver / dataList.size();

		int num = 0;
		if (min > 0) {
			num = ((int) aver / min) / 5;
		}else{
			num=(int)(aver/5);
		}
		if (num > 1) {
			this.distance = num;
		} else {
			this.distance = 1;
		}
		if (max < 10 * aver) {
			// this.distance = (max - min) / this.columnNum;
		}
		int lastColumnNum = 0;
		for (int b : dataList) {
			for (int i = 0; i < this.columnNum; i++) {
				if (i * distance + min <= b && b < (i + 1) * distance + min) {
					int tmp = (Integer) colValueList.get(i);
					tmp++;
					colValueList.set(i, new Integer(tmp));
				}
			}
			if (b >= this.columnNum*distance + min) {
				lastColumnNum++;
			}
		}
		colValueList.add(this.columnNum, lastColumnNum);

		// //////////////边界值
		// colValueList.set(this.columnNum-1,new Integer(
		// this.colValueList.get(this.columnNum-1)+1));
	}

	public void saveXML(String fileName) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (Exception pce) {
			System.err.println(pce);
		}
		Document doc = db.newDocument();
		
		Element root = doc.createElement("Histogram");
		doc.appendChild(root);

		Element hisName = doc.createElement("hisName");
		hisName.setAttribute("name", this.name);
		root.appendChild(hisName);

		Element xName = doc.createElement("xName");
		xName.setAttribute("name", this.xName);
		root.appendChild(xName);

		Element yName = doc.createElement("yName");
		yName.setAttribute("name", this.yName);
		root.appendChild(yName);

		Element maxNode = doc.createElement("max");
		maxNode.setAttribute("value", String.valueOf(this.max));
		root.appendChild(maxNode);

		Element minNode = doc.createElement("min");
		minNode.setAttribute("value", String.valueOf(this.min));
		root.appendChild(minNode);

		Element averNode = doc.createElement("aver");
		averNode.setAttribute("value", String.valueOf(this.aver));
		root.appendChild(averNode);
		
		Element amountNode = doc.createElement("amount");
		amountNode.setAttribute("value", String.valueOf(this.dataList.size()));
		root.appendChild(amountNode);

		Element columns = doc.createElement("columns");
		for (int i = 0; i < this.columnNum; i++) {
			Element column = doc.createElement("column");
			String name = (this.min + i * this.distance) + "-"
					+ (this.min + (i + 1) * this.distance - 1);
			column.setAttribute("name", name);
			column.setAttribute("value", String.valueOf(this.colValueList
					.get(i)));
			columns.appendChild(column);
		}
		Element column = doc.createElement("column");
		String name = (this.min + this.columnNum * this.distance) + "-"
				+ (this.max);
		column.setAttribute("name", name);
		column.setAttribute("value", String.valueOf(this.colValueList
				.get(this.columnNum)));
		columns.appendChild(column);
		root.appendChild(columns);

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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BigInteger b = new BigInteger("10");
		b = b.add(new BigInteger("20"));
		System.out.println(b.toString());
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public double getAver() {
		return aver;
	}

	public void setAver(int aver) {
		this.aver = aver;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public List getValueList() {
		return colValueList;
	}

	public void setValueList(List valueList) {
		this.colValueList = valueList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getXName() {
		return xName;
	}

	public void setXName(String name) {
		xName = name;
	}

	public String getYName() {
		return yName;
	}

	public void setYName(String name) {
		yName = name;
	}

	public int getColumnNum() {
		return columnNum;
	}

	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
	}

	public List<Integer> getDataList() {
		return dataList;
	}

	public void setDataList(List<Integer> dataList) {
		this.dataList = dataList;
	}

}
