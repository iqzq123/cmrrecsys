package org.tseg;

import java.util.HashMap;

import org.tseg.Ulits.AnalyseType;
import org.tseg.Ulits.Separator;
import org.tseg.analyse.Analyse;
import org.tseg.analyse.AnalyseRunner;
import org.tseg.analyse.PathFinder;
import org.tseg.analyse.StatAnalyse;
import org.tseg.analyse.GlobalAnalyse;
import org.tseg.analyse.Tundish;

public class Starter {

	
	private String inputPath = "";
	private String outputPath = "";
	private String siteDataPath = "";
	private String logSplit="\\|";
	private HashMap<String, Analyse> analyseMap = new HashMap<String, Analyse>();

	public Analyse getInstanceByName(String name) {

		Analyse obj = this.analyseMap.get(name);
		try {
			Analyse newInstance = (Analyse) Class.forName(
					obj.getClass().getName()).newInstance();
			this.analyseMap.put(newInstance.getName(), newInstance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return obj;
	}

	public Starter() {
		super();
		// TODO Auto-generated constructor stub
		StatAnalyse analyse1 = new StatAnalyse();
		analyseMap.put(analyse1.getName(), analyse1);

		Tundish analyse2 = new Tundish();
		analyseMap.put(analyse2.getName(), analyse2);

		PathFinder pathFinder = new PathFinder();
		analyseMap.put(pathFinder.getName(), pathFinder);

		GlobalAnalyse mk = new GlobalAnalyse();
		analyseMap.put(mk.getName(), mk);
	}

	public void start(String cmd) throws Exception {

		AnalyseRunner b = new AnalyseRunner();
	
		b.setInputPath(this.inputPath);
		b.setOutputPath(this.outputPath);
		b.setSiteDataPath(this.siteDataPath);
		b.setLogSplit(this.logSplit);
		
		String[] cmdArray = cmd.split(Separator.cmdLineSeparator);
		for (String cmdLine : cmdArray) {
			String[] strArray = cmdLine.split(Separator.cmdSeparator);
			String name = strArray[0];
			String param = "";
			for (int i = 1; i < strArray.length; i++) {
				param += strArray[i] + Separator.cmdSeparator;
			}
			param = param.substring(0, param.length() - 1);
			// Analyse analyse = this.analyseMap.get(name);
			Analyse analyse = getInstanceByName(name);
			if (analyse != null) {
				analyse.readParam(param);
				b.addAnalyse(analyse);
			}

		}
		b.seqRun();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Starter s = new Starter();
		s.setLogSplit("\\|");
		s.setInputPath("E:/data/pvData/cuixr_pagevisit");
		s.setOutputPath("E:/data/pvData/cui_out");
		s.setSiteDataPath("E:/data");
		try {
			String cmd = "StatAnalyse\t" + AnalyseType.NegCate + "\n"
					+ "StatAnalyse\t" + AnalyseType.PageToCate + "\n"
					+ "StatAnalyse\t" + AnalyseType.Original + "\n";
//					+ "GlobalAnalyse\t" + AnalyseType.Original +Separator.PARAM_SEPARATOR1+100+ "\n"
//					+ "GlobalAnalyse\t" + AnalyseType.PageToCate + Separator.PARAM_SEPARATOR1+100+"\n"
//					+ "GlobalAnalyse\t" + AnalyseType.NegCate+Separator.PARAM_SEPARATOR1+100;
			String cmd2 = "PathFinderClass	1@@@1@@@0@@@1@@@0@@@全国手机阅读首页###取消包月成功提示页面&&&女生首页###取消包月成功提示页面&&&原创首页###取消包月成功提示页面&&&畅销首页###取消包月成功提示页面&&&全国手机阅读首页###专区包月结果确认页&&&女生首页###专区包月结果确认页&&&原创首页###专区包月结果确认页&&& 畅销首页###专区包月结果确认页";
			s.start(cmd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public HashMap<String, Analyse> getAnalyseMap() {
		return analyseMap;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getSiteDataPath() {
		return siteDataPath;
	}

	public void setSiteDataPath(String siteDataPath) {
		this.siteDataPath = siteDataPath;
	}

	public String getLogSplit() {
		return logSplit;
	}

	public void setLogSplit(String logSplit) {
		this.logSplit = logSplit;
	}



}
