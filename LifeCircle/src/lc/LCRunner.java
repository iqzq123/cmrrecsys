package lc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LCRunner {

	private String LCTablePath = null;
	private String inputPath = null;
	private String split=",";

	public void run() throws Exception {

		for (int i = 1; i <= 4; i++) {
			LCUpdator.main(new String[]{"" + i});
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LCRunner r = new LCRunner();
		try {
			r.run();
			System.out.println("success");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error");
		}

	}
	public String getLCTablePath() {
		return LCTablePath;
	}
	public void setLCTablePath(String tablePath) {
		LCTablePath = tablePath;
	}
	public String getInputPath() {
		return inputPath;
	}
	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}
	public String getSplit() {
		return split;
	}
	public void setSplit(String split) {
		this.split = split;
	}

}
