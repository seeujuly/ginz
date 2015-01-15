package com.ginz.util.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 计算文本相似度
 */
public class CosineDis {

	public static double getSimilarity(ArrayList<String> doc1,
			ArrayList<String> doc2) {
		if (doc1 != null && doc1.size() > 0 && doc2 != null && doc2.size() > 0) {

			Map<Long, int[]> AlgorithmMap = new HashMap<Long, int[]>();

			for (int i = 0; i < doc1.size(); i++) {
				String d1 = doc1.get(i);
				long sIndex = hashId(d1);
				int[] fq = AlgorithmMap.get(sIndex);
				if (fq != null) {
					fq[0]++;
				} else {
					fq = new int[2];
					fq[0] = 1;
					fq[1] = 0;
					AlgorithmMap.put(sIndex, fq);
				}
			}

			for (int i = 0; i < doc2.size(); i++) {
				String d2 = doc2.get(i);
				long sIndex = hashId(d2);
				int[] fq = AlgorithmMap.get(sIndex);
				if (fq != null) {
					fq[1]++;
				} else {
					fq = new int[2];
					fq[0] = 0;
					fq[1] = 1;
					AlgorithmMap.put(sIndex, fq);
				}

			}

			Iterator<Long> iterator = AlgorithmMap.keySet().iterator();
			double sqdoc1 = 0;
			double sqdoc2 = 0;
			double denominator = 0;
			while (iterator.hasNext()) {
				int[] c = AlgorithmMap.get(iterator.next());
				denominator += c[0] * c[1];
				sqdoc1 += c[0] * c[0];
				sqdoc2 += c[1] * c[1];
			}
			System.out.println(denominator / Math.sqrt(sqdoc1 * sqdoc2));
			return denominator / Math.sqrt(sqdoc1 * sqdoc2);
		} else {
			return 0;
		}
	}

	public static long hashId(String s) {
		long seed = 131; // 31 131 1313 13131 131313 etc.. BKDRHash
		long hash = 0;
		for (int i = 0; i < s.length(); i++) {
			hash = (hash * seed) + s.charAt(i);
		}
		System.out.println(hash);
		return hash;
	}

	public static void main(String[] args) {
		/*ArrayList<String> t1 = new ArrayList<String>();
		ArrayList<String> t2 = new ArrayList<String>();
		t1.add("篮球");
		t1.add("爬山");
		t1.add("音乐");
		t1.add("旅行");

		t2.add("篮球活动");
		t2.add("周一10点一起打篮球");

		getSimilarity(t1, t2);*/
		
		String string = "周一10点一起打篮球";
		System.out.println(string.charAt(1)+0);
		
		System.out.println(string.contains("一起"));
	}

}
