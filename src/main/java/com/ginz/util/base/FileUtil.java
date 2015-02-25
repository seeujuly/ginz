package com.ginz.util.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;

/**
 * 文件操作类
 */
public class FileUtil {

	private static final int BUFFER = 1024;

	/**
	 * 拷贝文件(只能拷贝文件)
	 * @param strSourceFileName 指定的文件全路径名
	 * @param strDestDir 拷贝到指定的文件夹
	 * @return 如果成功true;否则false
	 */
	public static boolean copyTo(String strSourceFileName, String strDestDir) {
		File fileSource = new File(strSourceFileName);
		File fileDest = new File(strDestDir);

		// 如果源文件不存在或源文件是文件夹?
		if (!fileSource.exists() || !fileSource.isFile()) {
			System.out.println("源文件[" + strSourceFileName + "],不存在或是文件夹!");
			return false;
		}

		// 如果目标文件夹不存在
		if (!fileDest.isDirectory() || !fileDest.exists()) {
			if (!fileDest.mkdirs()) {
				System.out.println("目录文件夹不存在，创建目标文件夹时失�?");
				return false;
			}
		}

		try {
			String strAbsFilename = strDestDir + File.separator + fileSource.getName();

			FileInputStream fileInput = new FileInputStream(strSourceFileName);
			FileOutputStream fileOutput = new FileOutputStream(strAbsFilename);

			int count = -1;

			long nWriteSize = 0;
			long nFileSize = fileSource.length();

			byte[] data = new byte[BUFFER];

			while (-1 != (count = fileInput.read(data, 0, BUFFER))) {

				fileOutput.write(data, 0, count);

				nWriteSize += count;

				long size = (nWriteSize * 100) / nFileSize;
				long t = nWriteSize;

				String msg = null;

				if (size <= 100 && size >= 0) {
					msg = "\r拷贝文件进度:   " + size + "%   \t" + "\t   已拷贝   " + t;
					System.out.println(msg);
				} else if (size > 100) {
					msg = "\r拷贝文件进度:   " + 100 + "%   \t" + "\t   已拷贝   " + t;
					System.out.println(msg);
				}

			}

			fileInput.close();
			fileOutput.close();

			System.out.println("拷贝文件成功!");
			return true;

		} catch (Exception e) {
			System.out.println("异常信息：[" + e + "]");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除指定的文件
	 * @param strFileName 指定绝对路径的文件名
	 * @return 如果删除成功true否则false
	 */
	public boolean delete(String strFileName) {
		File fileDelete = new File(strFileName);

		if (!fileDelete.exists() || !fileDelete.isFile()) {
			System.out.println("错误: " + strFileName + "不存在");
			return false;
		}

		return fileDelete.delete();
	}

	/**
	 * 移动文件(只能移动文件)
	 * @param strSourceFileName 是指定的文件全路径名
	 * @param strDestDir 移动到指定的文件夹中
	 * @return 如果成功true; 否则false
	 */
	public boolean moveFile(String strSourceFileName, String strDestDir) {
		if (copyTo(strSourceFileName, strDestDir))
			return this.delete(strSourceFileName);
		else
			return false;
	}

	/**
	 * 创建文件夹
	 * @param strDir 要创建的文件夹名称
	 * @return 如果成功true;否则false
	 */
	public boolean makedir(String strDir) {
		File fileNew = new File(strDir);

		if (!fileNew.exists()) {
			System.out.println("文件夹不存在--创建文件夹");
			return fileNew.mkdirs();
		} else {
			System.out.println("文件夹存在");
			return true;
		}
	}

	/**
	 * 删除文件夹
	 * @param strDir 要删除的文件夹名称
	 * @return 如果成功true;否则false
	 */
	public boolean rmdir(String strDir) {
		File rmDir = new File(strDir);
		if (rmDir.isDirectory() && rmDir.exists()) {
			String[] fileList = rmDir.list();

			for (int i = 0; i < fileList.length; i++) {
				String subFile = strDir + File.separator + fileList[i];
				File tmp = new File(subFile);
				if (tmp.isFile())
					tmp.delete();
				else if (tmp.isDirectory())
					rmdir(subFile);
				else {
					System.out.println("error!");
				}
			}
			rmDir.delete();
		} else
			return false;
		return true;
	}
	
	/**
	 * 以PrintWriter来实现写入
	 * @param destFile目标文件
	 * @param content写入内容
	 */
	public static void write(String destFile,String content){
		//String path="e://a.txt";  
        try{  
            FileWriter fw = new FileWriter(destFile,true);  
            //fw.write(content);
            PrintWriter pw = new PrintWriter(fw);  
            pw.println(content);  
            pw.close();  
            fw.close();  
        }catch (IOException e){  
            e.printStackTrace();  
        }  
	}
	
	/**
	 * 以BufferedWriter来实现写入
	 * @param destFile目标文件
	 * @param content1写入内容1
	 * @param content2写入内容2
	 */
	public static void write(String destFile,String content,String content2){
        try {   
             FileWriter fw = new FileWriter(destFile,true);   
             BufferedWriter bw = new BufferedWriter(fw);   
             bw.newLine();   
			 bw.write(content);   
			 bw.close();  
			 fw.close();   
        } catch (IOException e) {   
          e.printStackTrace();   
        }   
	}
	
	/** 
	* A方法：使用RandomAccessFile 追加内容到文件尾
	* @param fileName 文件名 
	* @param content 追加的内容 
	*/  
	public static void appendMethodA(String fileName, String content){  
		try {  
			// 打开一个随机访问文件流，按读写方式  
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");  
			// 文件长度，字节数  
			long fileLength = randomFile.length();  
			//将写文件指针移到文件尾。  
			randomFile.seek(fileLength);  
			randomFile.writeBytes(content);  
			randomFile.close();  
		} catch (IOException e){  
			e.printStackTrace();  
		}	  
	} 
	
	/** 
	* B方法：使用FileWriter 追加内容到文件尾
	* @param fileName 
	* @param content 
	*/  
	public static void appendMethodB(String fileName, String content){  
		try {  
			//打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
			FileWriter writer = new FileWriter(fileName, true);  
			writer.write(content);  
			writer.close();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}
	
	/** 
	* 以字节为单位读取文件 
	* @param fileName 文件名 
	*/
	public static void readFileByBytes(String fileName){  
		File file = new File(fileName);  
		InputStream in = null;  
		
		//一次读一个字节
		try {  
			in = new FileInputStream(file);  
			int tempbyte;  
			while((tempbyte=in.read()) != -1){  
				System.out.write(tempbyte);  
			}  
			in.close();  
		} catch (IOException e) {  
			e.printStackTrace();  
			return;  
		} 
		
		//一次读多个字节 
		try {  
			byte[] tempbytes = new byte[100];  
			int byteread = 0;  
			in = new FileInputStream(fileName);  
			FileUtil.showAvailableBytes(in);  
			//读入多个字节到字节数组中，byteread为一次读入的字节数  
			while ((byteread = in.read(tempbytes)) != -1){  
			System.out.write(tempbytes, 0, byteread);  
			}  
		} catch (Exception e1) {  
		e1.printStackTrace();  
		} finally {  
			if (in != null){  
				try {  
					in.close();  
				} catch (IOException e1) {  
					
				}
			}  
		}  
	}  
	
	/** 
	* 以字符为单位读取文件，常用于读文本，数字等类型的文件 
	* @param fileName 文件名 
	*/  
	public static void readFileByChars(String fileName){  
		File file = new File(fileName);  
		Reader reader = null; 
		
		// 一次读一个字符  
		try {  
			reader = new InputStreamReader(new FileInputStream(file));  
			int tempchar;  
			while ((tempchar = reader.read()) != -1){  
			//对于windows下，rn这两个字符在一起时，表示一个换行。  
			//但如果这两个字符分开显示时，会换两次行。  
			//因此，屏蔽掉r，或者屏蔽n。否则，将会多出很多空行。  
				if (((char)tempchar) != 'r'){  
					System.out.print((char)tempchar);  
				}  
			}  
			reader.close();  
		} catch (Exception e) {  
			e.printStackTrace();  
		}
		
		//一次读多个字符  
		try {  
			char[] tempchars = new char[30];  
			int charread = 0;  
			reader = new InputStreamReader(new FileInputStream(fileName));  
			//读入多个字符到字符数组中，charread为一次读取字符数  
			while ((charread = reader.read(tempchars))!=-1){  
			//同样屏蔽掉r不显示  
				if ((charread == tempchars.length)&&(tempchars[tempchars.length-1] != 'r')){  
					System.out.print(tempchars);  
				}else{  
					for (int i=0; i<charread; i++){  
						if(tempchars[i] == 'r'){  
							continue;  
						}else{  
							System.out.print(tempchars[i]);  
						}  
					}  
				}  
			}  
		} catch (Exception e) {  
			e.printStackTrace();  
		}finally {  
			if (reader != null){  
				try {  
					reader.close();  
				} catch (IOException e1) {  
				}  
			}  
		}  
	}  
		
	/** 
	* 以行为单位读取文件，常用于读面向行的格式化文件 
	* @param fileName 文件名 
	*/  
	public static void readFileByLines(String fileName){  
		File file = new File(fileName);  
		BufferedReader reader = null;  
		try {  
			reader = new BufferedReader(new FileReader(file));  
			//reader = new BufferedReader(new InputStreamReader(new FileInputStream("ming.txt")));
			String tempString = null;  
			int line = 1;  
			//一次读入一行，直到读入null为文件结束  
			while ((tempString = reader.readLine()) != null){  
				//显示行号  
				System.out.println("line " + line + ": " + tempString);  
				line++;  
			}  
			reader.close();  
		} catch (IOException e) {  
			e.printStackTrace();  
		} finally {  
			if (reader != null){  
				try {  
					reader.close();  
				} catch (IOException e1) {  
				}  
			}  
		}
	}
	
	/** 
	* 随机读取文件内容 
	* @param fileName 文件名 
	*/  
	public static void readFileByRandomAccess(String fileName){  
		RandomAccessFile randomFile = null;  
		try {  
			System.out.println("随机读取一段文件内容：");  
			// 打开一个随机访问文件流，按只读方式  
			randomFile = new RandomAccessFile(fileName, "r");  
			// 文件长度，字节数  
			long fileLength = randomFile.length();  
			// 读文件的起始位置  
			int beginIndex = (fileLength > 4) ? 4 : 0;  
			//将读文件的开始位置移到beginIndex位置。  
			randomFile.seek(beginIndex);  
			byte[] bytes = new byte[10];  
			int byteread = 0;  
			//一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。  
			//将一次读取的字节数赋给byteread  
			while ((byteread = randomFile.read(bytes)) != -1){  
			System.out.write(bytes, 0, byteread);  
			}  
		} catch (IOException e){  
			e.printStackTrace();  
		} finally {  
			if (randomFile != null){  
				try {  
					randomFile.close();  
				} catch (IOException e1) {  
				}  
			}  
		}	  
	} 
	
	/** 
	* 显示输入流中还剩的字节数 
	* @param in 
	*/  
	private static void showAvailableBytes(InputStream in){  
		try {  
			System.out.println("当前字节输入流中的字节数为:" + in.available());  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}  
	
	public static void main(String[] args) {  
	
	}  
	
}
