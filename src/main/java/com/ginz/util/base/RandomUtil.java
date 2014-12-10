package com.ginz.util.base;

import java.util.Random;

/**
 * 产生随机数工具类
 */
public class RandomUtil {
	
	/**
	 * 产生lowerLimit到upperLimit之间的随机数
	 * @param lowerLimit
	 * @param upperLimit
	 * @return
	 */
	public static long generateRandom(long lowerLimit,long upperLimit){
		long randomNumber = (long) (Math.random()*(upperLimit-lowerLimit))+lowerLimit;
		return randomNumber;
	}
	
	/**
	 * 产生10位随机整数
	 * @return
	 */
	public static String digitsRandom(){
	    final int maxNum = 11;
	    int i; //生成的随机数
	    int count = 0; //生成的密码的长度
		char[] str = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer result = new StringBuffer("");
	    Random r = new Random();
	    while(count < 10){
		     //生成随机数，取绝对值，防止生成负数，	   
		     i = Math.abs(r.nextInt(maxNum)); //生成的数最大为36-1	
		     if(i >= 0 && i < str.length) {
		    	 result.append(str[i]);
		         count ++;
		     }	 
	    }
	    return result.toString();
	}

	public static void main(String []args){
		
		System.out.println(digitsRandom());
		
	}
	
}
