package lc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GlobalValue {

	public static final String DATASEP="\\|";
	public static final String STATUSEP=",";
	public static final byte ABSENCE = 10;
	public static final byte WARN = 20;
	public static int lapsedInterval=15;
	public static int pvThreshold=10;
	public static String rootDirectory="D:\\杭州项目\\10月20日\\";
	public static Date initDate = new Date();
	public static int[] warnThreshold = new int[8];
	static{
		DocumentBuilderFactory dbf = null; 
		Document document = null;
		try
		{
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().
				parse(GlobalValue.class.getResourceAsStream("/lifecycle.cfg.xml"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		Element root = document.getDocumentElement();
		NodeList properties = root.getElementsByTagName("property"); 
		for(int i = 0; i < properties.getLength(); i++)
		{
			Element property = (Element) properties.item(i);
			String name = property.getAttribute("name");
			String value = property.getFirstChild().getNodeValue().trim();
			if("lapsedInterval".equals(name))
				lapsedInterval = Integer.parseInt(value);
			else if("pvThreshold".equals(name))
				pvThreshold = Integer.parseInt(value);
			else if("rootDirectory".equals(name))
				rootDirectory = value;
			else if("initDate".equals(name))
				try
				{
					initDate = new SimpleDateFormat("yyyyMMdd").parse(value);
				} catch (ParseException e)
				{
					initDate = new Date();
				}
			else
			{
				String[] arr = value.split(",");
				for(int j = 0; j < arr.length; j++)
					warnThreshold[j] = Byte.parseByte(arr[j]);
			}
		}
	}
	public static void main(String[] args)
	{
		new GlobalValue();
		System.out.println(GlobalValue.lapsedInterval);
		System.out.println(GlobalValue.pvThreshold);
		System.out.println(GlobalValue.rootDirectory);
	}
}

