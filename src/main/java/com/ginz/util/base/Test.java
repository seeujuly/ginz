package com.ginz.util.base;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Test {

	public static void main(String[] args) throws IOException, ParseException {

		String userId = "39";
		long uId = 39L;
		
		System.out.println(userId.equals(String.valueOf(uId)));
		
		//FileUtil.write("D://test.txt","1234567890","");
	}
	
	public static void sort(Map<String, Integer> map) {
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(
				map.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		}); // 排序
		for (int i = 0; i < infoIds.size(); i++) { // 输出
			Entry<String, Integer> id = infoIds.get(i);
			System.out.println(id.getKey() + ":" + id.getValue());
		}
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
