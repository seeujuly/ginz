package com.ginz.util.base;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test {

	public static void main(String[] args) {
		//changeFileName(new File("E:/document/20世纪中文小说100强"));
		int sum = 10^2;
		System.out.println(sum);
		
	}

	public static void changeFileName(File file) {
		String dirPath = file.getAbsolutePath();// 目录路径
		System.out.println(dirPath);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				String originalName = f.getName();
				String path = dirPath + "\\";
				//String newName = originalName.substring(0, StringUtil.getCharacterPosition(4, "0", originalName)-1) + ".mobi";
				String newName = originalName.substring(0, originalName.lastIndexOf(".")-1) + ".mobi";
				File finalName = new File(path + newName);
				f.renameTo(finalName);
			}
		}
	}

}
