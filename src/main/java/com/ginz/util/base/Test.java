package com.ginz.util.base;

public class Test {

	public static void main(String[] args){
		
		String aaa = "sasdaassd3|114";
		String bbb = "";
		String ccc = "";
		bbb = aaa.substring(0, aaa.indexOf("|"));
		ccc = aaa.substring(aaa.indexOf("|")+1,aaa.length());
		System.out.println(ccc);
	}
	
}
