package com.ginz.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

@Namespace("/")
@Action(value = "uploadAction")
public class UploadAction extends BaseAction {

	public void upload() throws Exception {
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		
		MultiPartRequestWrapper wrapper = (MultiPartRequestWrapper) request;  
		String[] fileNames = wrapper.getFileNames("images");
		File[] files = wrapper.getFiles("images");
		String ext = "";
		String filePath = request.getSession().getServletContext().getRealPath("/upload/") + "/" + sdf.format(new Date()) + "_" + UUID.randomUUID().toString() + ext;
		if(files.length>0&&fileNames.length>0){
			for(int i=0;i<files.length;i++){
				ext = fileNames[i].substring(fileNames[i].lastIndexOf("."), fileNames[i].length());
				FileUtils.copyFile(files[i], new File(filePath)); 
			}
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	public void singleUpload() throws Exception {
		
		HttpServletRequest request = ServletActionContext.getRequest();
		InputStream is = request.getInputStream();
		
		//得到图片的二进制数据，以二进制封装得到数据，具有通用性  
        byte[] data = readInputStream(is);  
        //new一个文件对象用来保存图片，默认保存当前工程根目录  
        File imageFile = new File("D:/temp/aa.jpg");
        //创建输出流  
        FileOutputStream outStream = new FileOutputStream(imageFile);  
        //写入数据  
        outStream.write(data);  
        outStream.close();  
	}
	
	public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        //创建一个Buffer字符串  
        byte[] buffer = new byte[1024];  
        //每次读取的字符串长度，如果为-1，代表全部读取完毕  
        int len = 0;  
        //使用一个输入流从buffer里把数据读取出来  
        while( (len=inStream.read(buffer)) != -1 ){  
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度  
            outStream.write(buffer, 0, len);  
        }  
        //关闭输入流  
        inStream.close();  
        //把outStream里的数据写入内存  
        return outStream.toByteArray();  
    }  
	
}
