package org.tseg.analyse;

import java.io.IOException;

import org.tseg.Ulits.AnalyseType;

public abstract class Analyse {
	
	

	private String outputPath = "";
	private Byte type=AnalyseType.NegCate;
	public abstract void onInitial(); 
	public abstract void onReadLog(String []log)throws Exception;
	public abstract void onReadEnd()throws Exception;
	public abstract void readParam(String params);
	public abstract String getName();
	public String getOutputPath() {
		return outputPath;
	}
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	public Byte getType() {
		return type;
	}
	public void setType(byte type) {	
		this.type = type;
	}

}
