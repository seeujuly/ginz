package com.ginz.util.base;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang.StringUtils;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * 使用IK Analyzer实现中文分词
 */
public class AnalyzerUtil {

	public static void main(String[] args) throws IOException{
	
		//String value = "基于java语言$$&……￥#@  ,开发,的轻量级的中文分词工具包。";  
		String value = "我要去";
		//String value = "程序|开发";
		String result = analyze(value);
		System.out.println(result);
	
	}
	
	public static String analyze(String value) throws IOException {  
        StringReader sr=new StringReader(value);  
        IKSegmenter ik=new IKSegmenter(sr, true);  
        Lexeme lex=null;
        String stringValue = "";
        while((lex=ik.next())!=null){  
            //System.out.print(lex.getLexemeText()+"|");  
        	if(StringUtils.isEmpty(stringValue)){
        		stringValue += lex.getLexemeText();
        	}else{
        		stringValue += "|" + lex.getLexemeText();
        	}
        }
        return stringValue;
    }  	  

}
