package com.ginz.util.base;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class Test {

	public static void main(String[] args) throws IOException {
/*		String keyWord = "逗比逗比小二货";
		String[] keys = keyWord.split(",");
		if(keys.length>0){
			for(int i=0;i<keys.length;i++){
					System.out.println(i + "=" + keys[i]);
			}
		}*/
		
		
		BigDecimal data = new BigDecimal(0.0);
		BigDecimal data1 = new BigDecimal("");
		
		System.out.println(data1.compareTo(data));
		
		
		/*List<String> stringList = new ArrayList<String>();
		Map<String, Integer> map = new HashMap<String, Integer>();// 用于统计各个单词的个数，排序
		String sentence = "hello,my name is Tom,what is your name?he said:\"my name is John\"";
		StringTokenizer token = new StringTokenizer(sentence);// 这个类会将字符串分解成一个个的标记
		while (token.hasMoreTokens()) { // 循环遍历
			String word = token.nextToken(", ?.!:\"\"''\n"); // 括号里的字符的含义是说按照,空格? . : "" ''
																// \n去分割，如果这里你没明确要求，即括号里为空，则默认按照空格，制表符，新行符和回车符去分割
			stringList.add(word);
		}
		
		for(int i=0;i<stringList.size();i++){
			String word = stringList.get(i);
			if (map.containsKey(word)) { // HashMap不允许重复的key，所以利用这个特性，去统计单词的个数
				int count = map.get(word);
				map.put(word, count + 1); // 如果HashMap已有这个单词，则设置它的数量加1
			} else{
				map.put(word, 1); // 如果没有这个单词，则新填入，数量为1
			}
		}
		sort(map); // 调用排序的方法，排序并输出！
*/		
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
