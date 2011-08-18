package com;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class FileDirectoryBuilder
{
	private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private DocumentBuilder db = null;
	private Document doc = null;
    
	public static void main(String args[]) 
    {
        File directory = new File("."); 
        try {
        	directory.getCanonicalPath();
        	System.out.println("current:"+directory.getCanonicalPath());
        	FileDirectoryBuilder ft = new FileDirectoryBuilder();
        	ft.getFileDirXMLStr("E:\\data\\");
        }
        catch ( IOException ioexp ){
        	ioexp.printStackTrace();
        }
    }
	
	public String getFileDirXMLStr() {
		File directory = new File("."); 
		String curDir = "";
		try {
			curDir = directory.getCanonicalPath();
		}
		catch (IOException ioexp){
			ioexp.printStackTrace();
		}
		//处理curDir
    	File file = new File(curDir);
		try {
			db = dbf.newDocumentBuilder();
		} catch (Exception pce) {
			System.err.println(pce);
		}
		doc = db.newDocument();
		doc.appendChild(getFile(file));
        ByteArrayOutputStream os = new ByteArrayOutputStream();     
        try {
			// 用xmlserializer把document的内容进行串化
			OutputFormat of = new OutputFormat(doc);
			of.setEncoding("UTF-8");
			PrintWriter pw = new PrintWriter(os);
			XMLSerializer xs = new XMLSerializer(pw, of);
			xs.asDOMSerializer();
			xs.serialize(doc.getDocumentElement());
		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}   
        System.out.println(os.toString());   
		return os.toString();
    }
	
    public String getFileDirXMLStr(String path) {
    	File file = new File(path);
		try {
			db = dbf.newDocumentBuilder();
		} catch (Exception pce) {
			System.err.println(pce);
		}
		doc = db.newDocument();
		doc.appendChild(getFile(file));
        ByteArrayOutputStream os = new ByteArrayOutputStream();     
        try {
			// 用xmlserializer把document的内容进行串化
			OutputFormat of = new OutputFormat(doc);
			of.setEncoding("UTF-8");
			PrintWriter pw = new PrintWriter(os);
			XMLSerializer xs = new XMLSerializer(pw, of);
			xs.asDOMSerializer();
			xs.serialize(doc.getDocumentElement());
		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}   
        System.out.println(os.toString());   
		return os.toString();
    }
    
    private Element getFile(File f)
    {
    	Element node;
        int i = 0;
        String[] list = f.list();
        File childFile = null;
        ArrayList<File> fileArr = new ArrayList<File>();
        //保存当前f到XML文件中
        node = doc.createElement("node");
        node.setAttribute("label", f.getName());
        try {
        	node.setAttribute("path", f.getCanonicalPath());
        }
        catch ( IOException ioexp ){
        	ioexp.printStackTrace();
        }
        //如果f为目录，遍历当前f的子项
        while(f.isDirectory() && i < list.length)
        {
        	childFile = new File(f.getPath() + "\\" + list[i]);
        	//如果子项是文件，暂时保存到数组中
        	if ( childFile.isFile() ){
        		fileArr.add(childFile);
        	}
        	//如果子项是目录，进行递归
        	else {
        		Element childNode = getFile(new File(f.getPath() + "\\" + list[i]));
                node.appendChild(childNode);
        	}
        	i++;
        }
        //遍历所有文件子项
        Iterator iter = fileArr.iterator();
        while ( iter.hasNext() ){
        	childFile = (File)iter.next();
        	Element childNode = doc.createElement("node");
        	childNode.setAttribute("label", childFile.getName());
        	try {
        		childNode.setAttribute("path", childFile.getCanonicalPath());
            }
            catch ( IOException ioexp ){
            	ioexp.printStackTrace();
            }
            node.appendChild(childNode);
        }
        return node;
    }
}

